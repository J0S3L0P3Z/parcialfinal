import { Component, OnInit, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { ProductoService } from '../../services/producto.service';
import { Producto, TipoProducto } from '../../models/models';
import { errorMessage } from '../../utils/http-error';

@Component({
  selector: 'app-tiendacomponent',
  standalone: false,
  templateUrl: './tiendacomponent.html',
})
export class Tiendacomponent implements OnInit {
  auth = inject(AuthService);
  private servicio = inject(ProductoService);

  lacteos = signal<Producto[]>([]);
  carnes = signal<Producto[]>([]);
  cantidades: Record<string, number> = {};
  error = signal('');
  exito = signal('');

  formTipo: TipoProducto = 'lacteo';
  editandoId: number | null = null;
  formNombre = '';
  formCantidad = 0;
  formPrecio = 0;
  formUrlImagen = '';

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.servicio.getAll('lacteo').subscribe({
      next: (lista) => this.lacteos.set(lista),
      error: (err) => this.error.set(errorMessage(err, 'No se pudieron cargar los lácteos')),
    });
    this.servicio.getAll('carne').subscribe({
      next: (lista) => this.carnes.set(lista),
      error: (err) => this.error.set(errorMessage(err, 'No se pudieron cargar las carnes')),
    });
  }

  clave(tipo: TipoProducto, producto: Producto): string {
    return `${tipo}-${producto.id}`;
  }

  comprar(tipo: TipoProducto, producto: Producto) {
    if (!producto.id) {
      return;
    }
    const cantidad = Number(this.cantidades[this.clave(tipo, producto)] ?? 1);
    this.limpiarMensajes();
    this.servicio.comprar(tipo, producto.id, cantidad).subscribe({
      next: (respuesta) => {
        this.exito.set(`${respuesta}: ${cantidad} x ${producto.nombre}`);
        this.cantidades[this.clave(tipo, producto)] = 1;
        this.cargar();
      },
      error: (err) => this.error.set(errorMessage(err, 'No se pudo realizar la compra')),
    });
  }

  editar(tipo: TipoProducto, producto: Producto) {
    this.formTipo = tipo;
    this.editandoId = producto.id ?? null;
    this.formNombre = producto.nombre;
    this.formCantidad = producto.cantidad;
    this.formPrecio = producto.precio;
    this.formUrlImagen = producto.urlImagen;
    this.limpiarMensajes();
  }

  limpiarFormulario() {
    this.editandoId = null;
    this.formNombre = '';
    this.formCantidad = 0;
    this.formPrecio = 0;
    this.formUrlImagen = '';
  }

  guardar() {
    const datos: Producto = {
      nombre: this.formNombre.trim(),
      cantidad: Number(this.formCantidad),
      precio: Number(this.formPrecio),
      urlImagen: this.formUrlImagen.trim(),
    };
    const peticion =
      this.editandoId !== null
        ? this.servicio.update(this.formTipo, this.editandoId, datos)
        : this.servicio.create(this.formTipo, datos);
    this.limpiarMensajes();
    peticion.subscribe({
      next: (respuesta) => {
        this.exito.set(respuesta);
        this.limpiarFormulario();
        this.cargar();
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  eliminar(tipo: TipoProducto, producto: Producto) {
    if (!producto.id || !confirm(`¿Eliminar ${producto.nombre}?`)) {
      return;
    }
    this.limpiarMensajes();
    this.servicio.delete(tipo, producto.id).subscribe({
      next: (respuesta) => {
        this.exito.set(respuesta);
        this.cargar();
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  salir() {
    this.auth.logout();
  }

  private limpiarMensajes() {
    this.error.set('');
    this.exito.set('');
  }
}
