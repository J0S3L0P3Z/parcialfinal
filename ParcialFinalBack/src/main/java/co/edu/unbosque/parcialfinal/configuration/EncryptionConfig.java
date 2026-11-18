package co.edu.unbosque.parcialfinal.configuration;

import co.edu.unbosque.parcialfinal.util.AESUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfig {

  @Value("${aes.key}")
  private String key;

  @Value("${aes.iv}")
  private String iv;

  @PostConstruct
  void init() {
    AESUtil.configure(key, iv);
  }
}
