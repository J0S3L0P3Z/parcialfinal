package co.edu.unbosque.parcialfinal.controller;

import co.edu.unbosque.parcialfinal.dto.AuthResponseDTO;
import co.edu.unbosque.parcialfinal.dto.UserDTO;
import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.security.JwtUtil;
import co.edu.unbosque.parcialfinal.service.AuditService;
import co.edu.unbosque.parcialfinal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Login y registro de usuarios")
public class AuthController {

  private final AuthenticationManager authenticationManager;

  private final JwtUtil jwtUtil;

  private final UserService userService;

  private final AuditService audit;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtUtil jwtUtil,
      UserService userService,
      AuditService audit) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
    this.audit = audit;
  }

  @Operation(summary = "Iniciar sesión", description = "Recibe el usuario (o correo) y la contraseña y devuelve un token JWT.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Inicio de sesión exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Cuenta deshabilitada")
      })
  @PostMapping("/login")
  public ResponseEntity<?> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      examples = @ExampleObject(value = "{\"username\": \"admin\", \"password\": \"Admin#12345\"}")))
          @RequestBody
          UserDTO loginRequest) {
    if (loginRequest == null
        || loginRequest.getUsername() == null
        || loginRequest.getUsername().isBlank()
        || loginRequest.getPassword() == null
        || loginRequest.getPassword().isBlank()) {
      return ResponseEntity.badRequest().body("Debes enviar el usuario y la contraseña");
    }
    String attempted = loginRequest.getUsername().trim();
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(attempted, loginRequest.getPassword()));
      User user = (User) authentication.getPrincipal();
      userService.registerLogin(user.getId());
      audit.log(user.getId(), user.getUsername(), AuditAction.LOGIN_SUCCESS, null);
      String jwt = jwtUtil.generateToken(user);
      return ResponseEntity.ok(
          new AuthResponseDTO(jwt, user.getRole().name(), user.getUsername(), jwtUtil.getExpirationMinutes()));
    } catch (DisabledException e) {
      audit.log(null, attempted, AuditAction.LOGIN_FAILED, "cuenta deshabilitada");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tu cuenta está deshabilitada");
    } catch (AuthenticationException e) {
      audit.log(null, attempted, AuditAction.LOGIN_FAILED, "credenciales inválidas");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nombre de usuario o contraseña inválidos");
    }
  }

  @Operation(summary = "Registrar un usuario", description = "Crea una cuenta con rol USER (el rol enviado se ignora).")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado"),
        @ApiResponse(responseCode = "409", description = "El usuario o el correo ya existen"),
        @ApiResponse(responseCode = "400", description = "Datos o contraseña inválidos")
      })
  @PostMapping("/register")
  public ResponseEntity<String> register(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              value = "{\"username\": \"ana.perez\", \"email\": \"ana@correo.com\", \"password\": \"Clave#2026\"}")))
          @RequestBody
          UserDTO registerRequest) {
    int result = userService.register(registerRequest);
    switch (result) {
      case 0:
        audit.log(null, registerRequest.getUsername().trim(), AuditAction.REGISTER, null);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
      case 1:
        return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
      case 2:
        return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado");
      case 3:
        return ResponseEntity.badRequest()
            .body("La contraseña debe tener mínimo 8 caracteres, una minúscula, una mayúscula, un número y un símbolo");
      default:
        return ResponseEntity.badRequest()
            .body("Datos inválidos: usuario de 3 a 30 caracteres (letras, números, . _ -) y un correo válido");
    }
  }
}
