package co.edu.unbosque.parcialfinal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.edu.unbosque.parcialfinal.dto.FactDTO;
import co.edu.unbosque.parcialfinal.exception.ExternalApiException;
import co.edu.unbosque.parcialfinal.service.ExternalHTTPRequestHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExternalHTTPRequestHandlerTest {

  private HttpServer server;

  private String base;

  @BeforeEach
  void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/fact", ex -> reply(ex, 200, "{\"id\":\"1\",\"text\":\"Dato ñ\",\"language\":\"en\"}"));
    server.createContext("/facts", ex -> reply(ex, 200, "[{\"id\":\"1\"},{\"id\":\"2\"}]"));
    server.createContext("/missing", ex -> reply(ex, 404, "{}"));
    server.start();
    base = "http://127.0.0.1:" + server.getAddress().getPort();
  }

  @AfterEach
  void stop() {
    server.stop(0);
  }

  private static void reply(com.sun.net.httpserver.HttpExchange ex, int status, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().add("Content-Type", "application/json");
    ex.sendResponseHeaders(status, bytes.length);
    ex.getResponseBody().write(bytes);
    ex.close();
  }

  @Test
  void convertsResponseToDto() {
    FactDTO fact = ExternalHTTPRequestHandler.doGetAndConvertToDto(base + "/fact", FactDTO.class);
    assertEquals("Dato ñ", fact.getText());
  }

  @Test
  void convertsResponseToList() {
    List<FactDTO> facts = ExternalHTTPRequestHandler.doGetAndConvertToDtoList(base + "/facts", FactDTO.class);
    assertEquals(2, facts.size());
  }

  @Test
  void errorStatusThrows() {
    ExternalApiException e =
        assertThrows(
            ExternalApiException.class,
            () -> ExternalHTTPRequestHandler.doGetAndConvertToDto(base + "/missing", FactDTO.class));
    assertEquals(404, e.getStatus());
  }

  @Test
  void unreachableServerThrows() {
    assertThrows(ExternalApiException.class, () -> ExternalHTTPRequestHandler.doGet("http://127.0.0.1:1/x"));
  }
}
