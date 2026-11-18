package co.edu.unbosque.parcialfinal.service;

import co.edu.unbosque.parcialfinal.dto.FactDTO;
import co.edu.unbosque.parcialfinal.exception.ExternalApiException;
import co.edu.unbosque.parcialfinal.service.ExternalHTTPRequestHandler.ExternalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExternalApiService {

  @Value("${app.external.base-url:https://uselessfacts.jsph.pl/api/v2}")
  private String baseUrl;

  public FactDTO randomFact(String language) {
    return fact("/facts/random", language);
  }

  public FactDTO todayFact(String language) {
    return fact("/facts/today", language);
  }

  private FactDTO fact(String path, String language) {
    String lang = "de".equalsIgnoreCase(language) ? "de" : "en";
    ExternalResponse response = ExternalHTTPRequestHandler.doGet(base() + path + "?language=" + lang);
    if (!response.isSuccess()) {
      throw new ExternalApiException(
          "La API externa respondió con el código " + response.statusCode(), response.statusCode());
    }
    FactDTO fact = ExternalHTTPRequestHandler.toDto(response, FactDTO.class);
    if (fact == null) {
      throw new ExternalApiException("La API externa no devolvió datos", 502);
    }
    fact.setStatuscode(response.statusCode());
    return fact;
  }

  private String base() {
    return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }
}
