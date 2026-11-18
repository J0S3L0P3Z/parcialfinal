import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User, UserForm } from '../models/models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/user`;

  me(): Observable<User> {
    return this.http.get<User>(`${this.api}/me`);
  }

  updateMe(data: UserForm): Observable<string> {
    return this.http.put(`${this.api}/updateme`, data, { responseType: 'text' });
  }

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(`${this.api}/getall`);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/getbyid/${id}`);
  }

  count(): Observable<number> {
    return this.http.get<number>(`${this.api}/count`);
  }

  create(data: UserForm): Observable<string> {
    return this.http.post(`${this.api}/createjson`, data, { responseType: 'text' });
  }

  update(id: number, data: UserForm): Observable<string> {
    return this.http.put(`${this.api}/updatejson`, data, { params: { id }, responseType: 'text' });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.api}/deletebyid/${id}`, { responseType: 'text' });
  }
}
