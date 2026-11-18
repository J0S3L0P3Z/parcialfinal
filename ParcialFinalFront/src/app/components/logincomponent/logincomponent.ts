import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { errorMessage } from '../../utils/http-error';

@Component({
  selector: 'app-logincomponent',
  standalone: false,
  styleUrl: './logincomponent.css',
  templateUrl: './logincomponent.html',
})
export class Logincomponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  sesionVencida = inject(ActivatedRoute).snapshot.queryParamMap.has('expired');

  usuario = '';
  clave = '';

  cargando = signal(false);
  error = signal('');

  iniciarSesion() {
    if (!this.usuario.trim() || !this.clave) {
      this.error.set('Escribe tu usuario (o correo) y tu contraseña.');
      return;
    }
    this.cargando.set(true);
    this.error.set('');
    this.auth.login({ username: this.usuario.trim(), password: this.clave }).subscribe({
      next: () => this.router.navigate(['/inicio']),
      error: (err) => {
        this.error.set(errorMessage(err, 'No se pudo iniciar sesión'));
        this.cargando.set(false);
      },
    });
  }
}
