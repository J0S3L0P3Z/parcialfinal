import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Producto, TipoProducto } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly http = inject(HttpClient);

  private api(tipo: TipoProducto): string {
    return `${environment.apiUrl}/${tipo}`;
  }

  getAll(tipo: TipoProducto): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.api(tipo)}/getall`);
  }

  getById(tipo: TipoProducto, id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.api(tipo)}/getbyid/${id}`);
  }

  create(tipo: TipoProducto, data: Producto): Observable<string> {
    return this.http.post(`${this.api(tipo)}/createjson`, data, { responseType: 'text' });
  }

  update(tipo: TipoProducto, id: number, data: Producto): Observable<string> {
    return this.http.put(`${this.api(tipo)}/updatejson/${id}`, data, { responseType: 'text' });
  }

  delete(tipo: TipoProducto, id: number): Observable<string> {
    return this.http.delete(`${this.api(tipo)}/deletebyid/${id}`, { responseType: 'text' });
  }

  comprar(tipo: TipoProducto, id: number, cantidad: number): Observable<string> {
    return this.http.put(`${this.api(tipo)}/comprar/${id}`, null, {
      params: { cantidad },
      responseType: 'text',
    });
  }
}
