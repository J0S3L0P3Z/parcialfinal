package co.edu.unbosque.parcialfinal.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ExternalHTTPRequestHandler {

	private final HttpClient httpClient;

	public ExternalHTTPRequestHandler() {
		this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(10)).build();
	}

	public HttpResponse<String> enviarPeticion(String url, String metodo, String cuerpo, Map<String, String> headers) {

		try {
			HttpRequest.Builder solicitud = HttpRequest.newBuilder().uri(URI.create(url))
					.timeout(Duration.ofSeconds(30)).header("Accept", "application/json");

			if (cuerpo != null && !cuerpo.isBlank()) {
				solicitud.header("Content-Type", "application/json");
			}

			if (headers != null) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					solicitud.header(header.getKey(), header.getValue());
				}
			}

			HttpRequest.BodyPublisher contenido;

			if (cuerpo == null || cuerpo.isBlank()) {
				contenido = HttpRequest.BodyPublishers.noBody();
			} else {
				contenido = HttpRequest.BodyPublishers.ofString(cuerpo);
			}

			solicitud.method(metodo.toUpperCase(), contenido);

			return httpClient.send(solicitud.build(), HttpResponse.BodyHandlers.ofString());

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("La petición HTTP fue interrumpida.", e);

		} catch (IOException | IllegalArgumentException e) {
			throw new RuntimeException("Error al consumir la API externa.", e);
		}
	}
}