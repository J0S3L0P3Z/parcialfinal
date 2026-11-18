package co.edu.unbosque.parcialfinal.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptedConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return attribute == null ? null : AESUtil.encryptWithRandomIv(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    try {
      return AESUtil.decryptWithRandomIv(dbData);
    } catch (IllegalStateException e) {
      return dbData;
    }
  }
}
