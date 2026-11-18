package co.edu.unbosque.parcialfinal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.unbosque.parcialfinal.util.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:parcialtest;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "jwt.secret=integration-test-secret-0123456789-abcdefgh",
      "aes.key=llave-de-16-chrs",
      "aes.iv=iv-de-16-caract!",
      "app.seed.admin-username=admin",
      "app.seed.admin-email=admin@test.local",
      "app.seed.admin-password=Admin#12345",
      "app.seed.user-username=normaluser",
      "app.seed.user-email=normaluser@test.local",
      "app.seed.user-password=Usuario#12345"
    })
class ParcialFinalApplicationTests {

  @Autowired private MockMvc mvc;

  @Autowired private ObjectMapper mapper;

  @Autowired private JdbcTemplate jdbc;

  private String login(String user, String pass) throws Exception {
    String body =
        mvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"" + user + "\",\"password\":\"" + pass + "\"}"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return mapper.readTree(body).get("token").asText();
  }

  @Test
  void protectedEndpointWithoutTokenIs401() throws Exception {
    mvc.perform(get("/user/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void wrongPasswordIs401() throws Exception {
    mvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"mala\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void loginByEmailAndUserCannotUseAdminEndpoints() throws Exception {
    String token = login("normaluser@test.local", "Usuario#12345");
    mvc.perform(get("/user/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("normaluser"))
        .andExpect(jsonPath("$.password").doesNotExist());
    mvc.perform(get("/user/getall").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }

  @Test
  void registerForcesUserRoleAndValidatesPassword() throws Exception {
    mvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"debil\",\"email\":\"debil@test.local\",\"password\":\"123\"}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\":\"intruso\",\"email\":\"intruso@test.local\",\"password\":\"Clave#2026\",\"role\":\"ADMIN\"}"))
        .andExpect(status().isCreated());
    String token = login("intruso", "Clave#2026");
    mvc.perform(get("/user/me").header("Authorization", "Bearer " + token))
        .andExpect(jsonPath("$.role").value("USER"));
  }

  @Test
  void adminCrudAuditAndStats() throws Exception {
    String auth = "Bearer " + login("admin", "Admin#12345");
    mvc.perform(
            post("/user/createjson")
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"temporal\",\"email\":\"temporal@test.local\",\"password\":\"Clave#2026\"}"))
        .andExpect(status().isCreated());
    String list =
        mvc.perform(get("/user/getall").header("Authorization", auth))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long id = 0;
    for (var node : mapper.readTree(list)) {
      if ("temporal".equals(node.get("username").asText())) id = node.get("id").asLong();
    }
    mvc.perform(
            put("/user/updatejson")
                .param("id", String.valueOf(id))
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enabled\":false}"))
        .andExpect(status().isOk());
    mvc.perform(delete("/user/deletebyid/" + id).header("Authorization", auth)).andExpect(status().isOk());
    mvc.perform(get("/admin/audit").header("Authorization", auth))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].action").value("USER_DELETED"));
    mvc.perform(get("/admin/stats").header("Authorization", auth))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.admins").value(1));
  }

  @Test
  void emailIsStoredEncrypted() throws Exception {
    String stored =
        jdbc.queryForObject("select email from useraccount where username = ?", String.class, "admin");
    assertFalse(stored.contains("@"));
    assertEquals("admin@test.local", AESUtil.decrypt(stored));
  }
}
