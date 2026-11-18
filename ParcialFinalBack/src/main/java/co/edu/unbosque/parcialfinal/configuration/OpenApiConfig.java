package co.edu.unbosque.parcialfinal.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		final String esquemaSeguridad = "bearerAuth";

		return new OpenAPI()
				.info(new Info().title("API Parcial Final").version("1.0")
						.description("API de usuarios, roles y autenticación JWT"))
				.addSecurityItem(new SecurityRequirement().addList(esquemaSeguridad))
				.components(new Components().addSecuritySchemes(esquemaSeguridad, new SecurityScheme()
						.name(esquemaSeguridad).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}
}
