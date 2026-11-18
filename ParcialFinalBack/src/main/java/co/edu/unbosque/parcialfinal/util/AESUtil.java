package co.edu.unbosque.parcialfinal.util;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.DigestUtils;

public class AESUtil {

  private static final String ALGORITMO = "AES";

  private static final String TIPOCIFRADO = "AES/GCM/NoPadding";

  private static final int TAG_BITS = 128;

  private static final int RANDOM_IV_BYTES = 12;

  private static final SecureRandom RANDOM = new SecureRandom();

  private static volatile String configuredKey;

  private static volatile String configuredIv;

  private AESUtil() {}

  public static void configure(String llave, String iv) {
    int keyLen = llave == null ? 0 : llave.getBytes(StandardCharsets.UTF_8).length;
    if (keyLen != 16 && keyLen != 24 && keyLen != 32) {
      throw new IllegalArgumentException(
          "aes.key debe tener exactamente 16, 24 o 32 caracteres (tiene " + keyLen + ")");
    }
    int ivLen = iv == null ? 0 : iv.getBytes(StandardCharsets.UTF_8).length;
    if (ivLen != 12 && ivLen != 16) {
      throw new IllegalArgumentException(
          "aes.iv debe tener exactamente 12 o 16 caracteres (tiene " + ivLen + ")");
    }
    configuredKey = llave;
    configuredIv = iv;
  }

  public static boolean isConfigured() {
    return configuredKey != null && configuredIv != null;
  }

  public static String encrypt(String llave, String iv, String texto) {
    try {
      Cipher cipher = Cipher.getInstance(TIPOCIFRADO);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec(llave), new GCMParameterSpec(TAG_BITS, bytes(iv)));
      return new String(encodeBase64(cipher.doFinal(bytes(texto))), StandardCharsets.US_ASCII);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("No se pudo cifrar el texto", e);
    }
  }

  public static String decrypt(String llave, String iv, String encrypted) {
    try {
      Cipher cipher = Cipher.getInstance(TIPOCIFRADO);
      cipher.init(Cipher.DECRYPT_MODE, keySpec(llave), new GCMParameterSpec(TAG_BITS, bytes(iv)));
      return new String(cipher.doFinal(decodeBase64(encrypted)), StandardCharsets.UTF_8);
    } catch (GeneralSecurityException | IllegalArgumentException e) {
      throw new IllegalStateException("No se pudo descifrar el texto", e);
    }
  }

  public static String encrypt(String plainText) {
    requireConfigured();
    return encrypt(configuredKey, configuredIv, plainText);
  }

  public static String decrypt(String encrypted) {
    requireConfigured();
    return decrypt(configuredKey, configuredIv, encrypted);
  }

  public static String encryptWithRandomIv(String plainText) {
    requireConfigured();
    try {
      byte[] iv = new byte[RANDOM_IV_BYTES];
      RANDOM.nextBytes(iv);
      Cipher cipher = Cipher.getInstance(TIPOCIFRADO);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec(configuredKey), new GCMParameterSpec(TAG_BITS, iv));
      byte[] encrypted = cipher.doFinal(bytes(plainText));
      byte[] out = new byte[iv.length + encrypted.length];
      System.arraycopy(iv, 0, out, 0, iv.length);
      System.arraycopy(encrypted, 0, out, iv.length, encrypted.length);
      return new String(encodeBase64(out), StandardCharsets.US_ASCII);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("No se pudo cifrar el texto", e);
    }
  }

  public static String decryptWithRandomIv(String encrypted) {
    requireConfigured();
    try {
      byte[] all = decodeBase64(encrypted);
      if (all.length <= RANDOM_IV_BYTES) {
        throw new IllegalStateException("Texto cifrado demasiado corto");
      }
      byte[] iv = Arrays.copyOfRange(all, 0, RANDOM_IV_BYTES);
      byte[] data = Arrays.copyOfRange(all, RANDOM_IV_BYTES, all.length);
      Cipher cipher = Cipher.getInstance(TIPOCIFRADO);
      cipher.init(Cipher.DECRYPT_MODE, keySpec(configuredKey), new GCMParameterSpec(TAG_BITS, iv));
      return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
    } catch (GeneralSecurityException | IllegalArgumentException e) {
      throw new IllegalStateException("No se pudo descifrar el texto", e);
    }
  }

  public static String hashingToMD5(String content) {
    return DigestUtils.md5Hex(content);
  }

  public static String hashingToSHA1(String content) {
    return DigestUtils.sha1Hex(content);
  }

  public static String hashingToSHA256(String content) {
    return DigestUtils.sha256Hex(content);
  }

  public static String hashingToSHA384(String content) {
    return DigestUtils.sha384Hex(content);
  }

  public static String hashingToSHA512(String content) {
    return DigestUtils.sha512Hex(content);
  }

  private static void requireConfigured() {
    if (!isConfigured()) {
      throw new IllegalStateException("AESUtil no está configurado: faltan aes.key y aes.iv");
    }
  }

  private static SecretKeySpec keySpec(String llave) {
    return new SecretKeySpec(bytes(llave), ALGORITMO);
  }

  private static byte[] bytes(String s) {
    return s.getBytes(StandardCharsets.UTF_8);
  }
}
