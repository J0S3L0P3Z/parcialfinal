package co.edu.unbosque.parcialfinal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ParcialFinalApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	private String login(String user, String pass) throws Exception {
		String body = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return "Bearer " + mapper.readTree(body).get("token").asText();
	}

	@Test
	void sinTokenNoPuedeListar() throws Exception {
		mvc.perform(get("/lacteo/getall")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminHaceCrud() throws Exception {
		String admin = login("admin", "admin123");
		String json = "{\"nombre\":\"Kumis\",\"cantidad\":5,\"precio\":6000,\"urlImagen\":\"https://x.com/k.png\"}";

		mvc.perform(post("/lacteo/createjson").header("Authorization", admin)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
		mvc.perform(put("/lacteo/updatejson/1").header("Authorization", admin)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
		mvc.perform(post("/lacteo/createjson").header("Authorization", admin)
				.contentType(MediaType.APPLICATION_JSON).content(json.replace("6000", "-1")))
				.andExpect(status().isBadRequest());
		mvc.perform(delete("/carne/deletebyid/999").header("Authorization", admin))
				.andExpect(status().isNotFound());
	}

	@Test
	void clienteCompraYSeRestaDelInventario() throws Exception {
		String cliente = login("cliente", "cliente123");
		int antes = mapper.readTree(mvc.perform(get("/carne/getbyid/1").header("Authorization", cliente))
				.andReturn().getResponse().getContentAsString()).get("cantidad").asInt();

		mvc.perform(put("/carne/comprar/1?cantidad=2").header("Authorization", cliente)).andExpect(status().isOk());
		mvc.perform(get("/carne/getbyid/1").header("Authorization", cliente))
				.andExpect(jsonPath("$.cantidad").value(antes - 2));

		mvc.perform(put("/carne/comprar/1?cantidad=9999").header("Authorization", cliente))
				.andExpect(status().isConflict());
		mvc.perform(post("/carne/createjson").header("Authorization", cliente)
				.contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
	}
}
