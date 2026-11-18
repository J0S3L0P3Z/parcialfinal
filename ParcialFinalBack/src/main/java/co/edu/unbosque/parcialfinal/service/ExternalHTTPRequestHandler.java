package co.edu.unbosque.parcialfinal.service;

import co.edu.unbosque.parcialfinal.exception.ExternalApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ExternalHTTPRequestHandler {

  private static final HttpClient HTTP_CLIENT =
      HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .connectTimeout(Duration.ofSeconds(10))
          .followRedirects(HttpClient.Redirect.NORMAL)
          .build();

  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private static final Gson GSON = new GsonBuilder().create();

  public record ExternalResponse(int statusCode, String body) {

    public boolean isSuccess() {
      return statusCode >= 200 && statusCode < 300;
    }
  }

  private ExternalHTTPRequestHandler() {}

  public static ExternalResponse doGet(String url) {
    return doGet(url, Map.of());
  }

  public static ExternalResponse doGet(String url, Map<String, String> headers) {
    HttpRequest.Builder builder = HttpRequest.newBuilder().GET().uri(URI.create(url));
    return send(builder, headers);
  }

  public static ExternalResponse doPost(String url, String json, Map<String, String> headers) {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .uri(URI.create(url))
            .header("Content-Type", "application/json");
    return send(builder, headers);
  }

  public static ExternalResponse doPut(String url, String json, Map<String, String> headers) {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .uri(URI.create(url))
            .header("Content-Type", "application/json");
    return send(builder, headers);
  }

  public static ExternalResponse doDelete(String url, Map<String, String> headers) {
    HttpRequest.Builder builder = HttpRequest.newBuilder().DELETE().uri(URI.create(url));
    return send(builder, headers);
  }

  public static <T> T doGetAndConvertToDto(String url, Class<T> type) {
    return toDto(requireSuccess(doGet(url)), type);
  }

  public static <T> T doGetAndConvertToDto(String url, Map<String, String> headers, Class<T> type) {
    return toDto(requireSuccess(doGet(url, headers)), type);
  }

  public static <T> List<T> doGetAndConvertToDtoList(String url, Class<T> type) {
    ExternalResponse response = requireSuccess(doGet(url));
    try {
      List<T> list = GSON.fromJson(response.body(), TypeToken.getParameterized(List.class, type).getType());
      return list == null ? List.of() : list;
    } catch (JsonSyntaxException e) {
      throw new ExternalApiException("La API externa respondió con un formato inesperado", e);
    }
  }

  public static <T> T toDto(ExternalResponse response, Class<T> type) {
    try {
      return GSON.fromJson(response.body(), type);
    } catch (JsonSyntaxException e) {
      throw new ExternalApiException("La API externa respondió con un formato inesperado", e);
    }
  }

  public static String prettyPrintUsingGson(String uglyJson) {
    Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
    JsonElement jsonElement = JsonParser.parseString(uglyJson);
    return gson.toJson(jsonElement);
  }

  private static ExternalResponse requireSuccess(ExternalResponse response) {
    if (!response.isSuccess()) {
      throw new ExternalApiException(
          "La API externa respondió con el código " + response.statusCode(), response.statusCode());
    }
    return response;
  }

  private static ExternalResponse send(HttpRequest.Builder builder, Map<String, String> headers) {
    builder.timeout(DEFAULT_TIMEOUT).setHeader("Accept", "application/json");
    if (headers != null) {
      headers.forEach(builder::setHeader);
    }
    try {
      HttpResponse<String> response =
          HTTP_CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      return new ExternalResponse(response.statusCode(), response.body());
    } catch (HttpTimeoutException e) {
      throw new ExternalApiException("La API externa tardó demasiado en responder", e);
    } catch (IOException e) {
      throw new ExternalApiException("No se pudo conectar con la API externa", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ExternalApiException("La llamada a la API externa fue interrumpida", e);
    }
  }
}
