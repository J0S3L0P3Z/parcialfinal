import { Component, OnInit, inject, signal } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User, UserForm } from '../../models/models';
import { errorMessage } from '../../utils/http-error';
import { MENSAJE_CLAVE, MENSAJE_CORREO, claveValida, correoValido } from '../../utils/validators';

@Component({
  selector: 'app-perfilcomponent',
  standalone: false,
  styleUrl: './perfilcomponent.css',
  templateUrl: './perfilcomponent.html',
})
export class Perfilcomponent implements OnInit {
  private usuarios = inject(UserService);

  usuario = signal<User | null>(null);
  cargando = signal(false);
  error = signal('');
  exito = signal('');

  correo = '';
  clave = '';
  confirmarClave = '';

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.usuarios.me().subscribe({
      next: (u) => {
        this.usuario.set(u);
        this.correo = u.email ?? '';
        this.clave = '';
        this.confirmarClave = '';
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  guardar() {
    this.error.set('');
    this.exito.set('');
    const datos: UserForm = {};
    if (this.correo.trim() && this.correo.trim().toLowerCase() !== this.usuario()?.email) {
      if (!correoValido(this.correo)) {
        this.error.set(MENSAJE_CORREO);
        return;
      }
      datos.email = this.correo.trim().toLowerCase();
    }
    if (this.clave) {
      if (!claveValida(this.clave)) {
        this.error.set(MENSAJE_CLAVE);
        return;
      }
      if (this.clave !== this.confirmarClave) {
        this.error.set('Las contraseñas no coinciden.');
        return;
      }
      datos.password = this.clave;
    }
    if (!datos.email && !datos.password) {
      this.error.set('No hay cambios para guardar.');
      return;
    }
    this.cargando.set(true);
    this.usuarios.updateMe(datos).subscribe({
      next: (respuesta) => {
        this.exito.set(respuesta);
        this.cargando.set(false);
        this.cargar();
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.cargando.set(false);
      },
    });
  }
}
