package co.edu.unbosque.parcialfinal.controller;

import co.edu.unbosque.parcialfinal.dto.UserDTO;
import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.service.AuditService;
import co.edu.unbosque.parcialfinal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Gestión de Usuarios", description = "Cuenta propia y CRUD de usuarios (ADMIN)")
public class UserController {

  private final UserService userService;

  private final AuditService audit;

  public UserController(UserService userService, AuditService audit) {
    this.userService = userService;
    this.audit = audit;
  }

  @Operation(summary = "Mi cuenta")
  @GetMapping("/me")
  public ResponseEntity<UserDTO> me(@AuthenticationPrincipal User me) {
    return ResponseEntity.ok(userService.toDTO(me));
  }

  @Operation(summary = "Actualizar mi cuenta", description = "Cambia el correo y/o la contraseña del usuario autenticado.")
  @PutMapping(path = "/updateme", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateMe(@AuthenticationPrincipal User me, @RequestBody UserDTO data) {
    UserDTO allowed = new UserDTO();
    allowed.setEmail(data.getEmail());
    allowed.setPassword(data.getPassword());
    int status = userService.updateById(me.getId(), allowed);
    if (status == 0) {
      audit.log(me.getId(), me.getUsername(), AuditAction.USER_UPDATED, "cuenta propia");
    }
    return updateResponse(status);
  }

  @Operation(summary = "Cerrar sesión")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@AuthenticationPrincipal User me) {
    audit.log(me.getId(), me.getUsername(), AuditAction.LOGOUT, null);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Crear un usuario (ADMIN)")
  @PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createNewWithJSON(
      @AuthenticationPrincipal User me,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              value = "{\"username\": \"nuevo\", \"email\": \"nuevo@correo.com\", \"password\": \"Clave#2026\", \"role\": \"USER\"}")))
          @RequestBody
          UserDTO newUser) {
    int status = userService.create(newUser);
    switch (status) {
      case 0:
        audit.log(me.getId(), me.getUsername(), AuditAction.USER_CREATED, newUser.getUsername());
        return new ResponseEntity<>("Usuario creado exitosamente", HttpStatus.CREATED);
      case 1:
        return new ResponseEntity<>("El nombre de usuario ya existe", HttpStatus.CONFLICT);
      case 2:
        return new ResponseEntity<>("El correo ya está registrado", HttpStatus.CONFLICT);
      case 3:
        return new ResponseEntity<>(
            "La contraseña debe tener mínimo 8 caracteres, una minúscula, una mayúscula, un número y un símbolo",
            HttpStatus.BAD_REQUEST);
      default:
        return new ResponseEntity<>(
            "Datos inválidos: usuario de 3 a 30 caracteres y un correo válido", HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(summary = "Listar usuarios (ADMIN)")
  @GetMapping("/getall")
  public ResponseEntity<List<UserDTO>> getAll() {
    return ResponseEntity.ok(userService.getAll());
  }

  @Operation(summary = "Contar usuarios (ADMIN)")
  @GetMapping("/count")
  public ResponseEntity<Long> countAll() {
    return ResponseEntity.ok(userService.count());
  }

  @Operation(summary = "¿Existe el usuario? (ADMIN)")
  @GetMapping("/exists/{id}")
  public ResponseEntity<Boolean> exists(@Parameter(example = "1") @PathVariable Long id) {
    return ResponseEntity.ok(userService.exist(id));
  }

  @Operation(summary = "Obtener un usuario por id (ADMIN)")
  @GetMapping("/getbyid/{id}")
  public ResponseEntity<UserDTO> getById(@Parameter(example = "1") @PathVariable Long id) {
    UserDTO found = userService.getById(id);
    if (found != null) {
      return ResponseEntity.ok(found);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Actualizar un usuario (ADMIN)", description = "Solo cambia los campos enviados.")
  @PutMapping(path = "/updatejson", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateNewWithJSON(
      @AuthenticationPrincipal User me,
      @Parameter(example = "2") @RequestParam Long id,
      @RequestBody UserDTO newUser) {
    if (me.getId().equals(id)
        && (newUser.getRole() == User.Role.USER || Boolean.FALSE.equals(newUser.getEnabled()))) {
      return new ResponseEntity<>(
          "No puedes quitarte el rol de administrador ni deshabilitar tu propia cuenta", HttpStatus.BAD_REQUEST);
    }
    int status = userService.updateById(id, newUser);
    if (status == 0) {
      audit.log(me.getId(), me.getUsername(), AuditAction.USER_UPDATED, "usuario id=" + id);
    }
    return updateResponse(status);
  }

  @Operation(summary = "Eliminar un usuario (ADMIN)")
  @DeleteMapping("/deletebyid/{id}")
  public ResponseEntity<String> deleteById(
      @AuthenticationPrincipal User me, @Parameter(example = "2") @PathVariable Long id) {
    if (me.getId().equals(id)) {
      return new ResponseEntity<>("No puedes eliminar tu propia cuenta", HttpStatus.BAD_REQUEST);
    }
    int status = userService.deleteById(id);
    if (status == 0) {
      audit.log(me.getId(), me.getUsername(), AuditAction.USER_DELETED, "usuario id=" + id);
      return new ResponseEntity<>("Usuario eliminado exitosamente", HttpStatus.OK);
    }
    return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
  }

  private static ResponseEntity<String> updateResponse(int status) {
    switch (status) {
      case 0:
        return new ResponseEntity<>("Usuario actualizado exitosamente", HttpStatus.OK);
      case 1:
        return new ResponseEntity<>("El nombre de usuario o el correo ya están en uso", HttpStatus.CONFLICT);
      case 2:
        return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
      case 3:
        return new ResponseEntity<>(
            "La contraseña debe tener mínimo 8 caracteres, una minúscula, una mayúscula, un número y un símbolo",
            HttpStatus.BAD_REQUEST);
      default:
        return new ResponseEntity<>("Datos inválidos", HttpStatus.BAD_REQUEST);
    }
  }
}
