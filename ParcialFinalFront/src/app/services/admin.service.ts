import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuditLog, Page, Stats } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/admin`;

  audit(page = 0, size = 20, action?: string, userId?: number): Observable<Page<AuditLog>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (action) {
      params = params.set('action', action);
    }
    if (userId) {
      params = params.set('userId', userId);
    }
    return this.http.get<Page<AuditLog>>(`${this.api}/audit`, { params });
  }

  stats(): Observable<Stats> {
    return this.http.get<Stats>(`${this.api}/stats`);
  }
}
