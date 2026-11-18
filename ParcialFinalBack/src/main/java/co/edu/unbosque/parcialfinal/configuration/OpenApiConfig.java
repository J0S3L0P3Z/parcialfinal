package co.edu.unbosque.parcialfinal.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    String description =
        "<h2>API del parcial final</h2>"
            + "<ol>"
            + "<li>Inicia sesión con <code>POST /auth/login</code>.</li>"
            + "<li>Copia el valor de <code>token</code> de la respuesta.</li>"
            + "<li>Pulsa <b>Authorize</b> y pega el token.</li>"
            + "</ol>"
            + "<h3>Roles</h3><ul>"
            + "<li><strong>USER</strong>: su cuenta y la API externa.</li>"
            + "<li><strong>ADMIN</strong>: además gestiona usuarios, auditoría y estadísticas.</li>"
            + "</ul>";

    Info info =
        new Info()
            .title("API Parcial Final - Desarrollo de Sistemas de Información 3")
            .version("1.0")
            .description(description)
            .contact(new Contact().name("Universidad El Bosque"));

    SecurityScheme securityScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Token JWT obtenido en /auth/login");

    return new OpenAPI()
        .info(info)
        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
  }
}
