import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { errorMessage } from '../../utils/http-error';
import {
  MENSAJE_CLAVE,
  MENSAJE_CORREO,
  MENSAJE_USUARIO,
  claveValida,
  correoValido,
  usuarioValido,
} from '../../utils/validators';

@Component({
  selector: 'app-registrocomponent',
  standalone: false,
  styleUrl: './registrocomponent.css',
  templateUrl: './registrocomponent.html',
})
export class Registrocomponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  usuario = '';
  correo = '';
  clave = '';
  confirmarClave = '';

  cargando = signal(false);
  error = signal('');
  exito = signal('');

  crearCuenta() {
    const mensaje = this.validar();
    if (mensaje) {
      this.error.set(mensaje);
      return;
    }
    this.cargando.set(true);
    this.error.set('');
    this.auth
      .register({ username: this.usuario.trim(), email: this.correo.trim().toLowerCase(), password: this.clave })
      .subscribe({
        next: (respuesta) => {
          this.exito.set(respuesta + '. Te llevamos al inicio de sesión...');
          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (err) => {
          this.error.set(errorMessage(err, 'No se pudo crear la cuenta'));
          this.cargando.set(false);
        },
      });
  }

  private validar(): string {
    if (!this.usuario.trim() || !this.correo.trim() || !this.clave || !this.confirmarClave) {
      return 'Llena todos los campos.';
    }
    if (!usuarioValido(this.usuario)) {
      return MENSAJE_USUARIO;
    }
    if (!correoValido(this.correo)) {
      return MENSAJE_CORREO;
    }
    if (!claveValida(this.clave)) {
      return MENSAJE_CLAVE;
    }
    if (this.clave !== this.confirmarClave) {
      return 'Las contraseñas no coinciden.';
    }
    return '';
  }
}
