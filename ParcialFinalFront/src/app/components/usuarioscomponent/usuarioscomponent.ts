import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { Role, User, UserForm } from '../../models/models';
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
  selector: 'app-usuarioscomponent',
  standalone: false,
  styleUrl: './usuarioscomponent.css',
  templateUrl: './usuarioscomponent.html',
})
export class Usuarioscomponent implements OnInit {
  private servicio = inject(UserService);
  auth = inject(AuthService);

  usuarios = signal<User[]>([]);
  busqueda = signal('');
  editando = signal<User | null>(null);
  mostrarFormulario = signal(false);
  cargando = signal(false);
  error = signal('');
  exito = signal('');

  formUsuario = '';
  formCorreo = '';
  formClave = '';
  formRol: Role = 'USER';
  formHabilitado = true;

  filtrados = computed(() => {
    const texto = this.busqueda().trim().toLowerCase();
    return texto
      ? this.usuarios().filter(
          (u) => u.username.toLowerCase().includes(texto) || (u.email ?? '').toLowerCase().includes(texto),
        )
      : this.usuarios();
  });

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.servicio.getAll().subscribe({
      next: (lista) => this.usuarios.set(lista),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  esYo(usuario: User): boolean {
    return usuario.username === this.auth.username();
  }

  nuevo() {
    this.editando.set(null);
    this.formUsuario = '';
    this.formCorreo = '';
    this.formClave = '';
    this.formRol = 'USER';
    this.formHabilitado = true;
    this.limpiarMensajes();
    this.mostrarFormulario.set(true);
  }

  editar(usuario: User) {
    this.editando.set(usuario);
    this.formUsuario = usuario.username;
    this.formCorreo = usuario.email ?? '';
    this.formClave = '';
    this.formRol = usuario.role;
    this.formHabilitado = usuario.enabled;
    this.limpiarMensajes();
    this.mostrarFormulario.set(true);
  }

  cancelar() {
    this.mostrarFormulario.set(false);
    this.editando.set(null);
  }

  guardar() {
    const actual = this.editando();
    const mensaje = this.validar(!actual);
    if (mensaje) {
      this.error.set(mensaje);
      return;
    }
    const datos: UserForm = {
      username: this.formUsuario.trim(),
      email: this.formCorreo.trim().toLowerCase(),
      role: this.formRol,
      enabled: this.formHabilitado,
    };
    if (this.formClave) {
      datos.password = this.formClave;
    }
    const peticion = actual ? this.servicio.update(actual.id, datos) : this.servicio.create(datos);
    this.cargando.set(true);
    this.limpiarMensajes();
    peticion.subscribe({
      next: (respuesta) => {
        this.exito.set(respuesta);
        this.cargando.set(false);
        this.mostrarFormulario.set(false);
        this.editando.set(null);
        this.cargar();
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.cargando.set(false);
      },
    });
  }

  cambiarEstado(usuario: User) {
    this.limpiarMensajes();
    this.servicio.update(usuario.id, { enabled: !usuario.enabled }).subscribe({
      next: (respuesta) => {
        this.exito.set(respuesta);
        this.cargar();
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  eliminar(usuario: User) {
    if (!confirm(`¿Eliminar al usuario ${usuario.username}? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.limpiarMensajes();
    this.servicio.delete(usuario.id).subscribe({
      next: (respuesta) => {
        this.exito.set(respuesta);
        this.cargar();
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  private validar(claveObligatoria: boolean): string {
    if (!usuarioValido(this.formUsuario)) {
      return MENSAJE_USUARIO;
    }
    if (!correoValido(this.formCorreo)) {
      return MENSAJE_CORREO;
    }
    if (claveObligatoria && !this.formClave) {
      return 'Escribe una contraseña para el nuevo usuario.';
    }
    if (this.formClave && !claveValida(this.formClave)) {
      return MENSAJE_CLAVE;
    }
    return '';
  }

  private limpiarMensajes() {
    this.error.set('');
    this.exito.set('');
  }
}
