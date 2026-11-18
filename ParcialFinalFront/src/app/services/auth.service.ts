import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, Session } from '../models/models';

const STORAGE_KEY = 'session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly api = `${environment.apiUrl}/auth`;

  private readonly sessionState = signal<Session | null>(this.readSession());

  readonly session = this.sessionState.asReadonly();
  readonly isLoggedIn = computed(() => {
    const s = this.sessionState();
    return !!s && s.expiresAt > Date.now();
  });
  readonly isAdmin = computed(() => this.isLoggedIn() && this.sessionState()?.role === 'ADMIN');
  readonly username = computed(() => this.sessionState()?.username ?? '');

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/login`, data).pipe(
      tap((res) => {
        const session: Session = {
          token: res.token,
          role: res.role,
          username: data.username,
          expiresAt: this.leerExpiracion(res.token),
        };
        sessionStorage.setItem(STORAGE_KEY, JSON.stringify(session));
        this.sessionState.set(session);
      }),
    );
  }

  register(data: LoginRequest): Observable<string> {
    return this.http.post(`${this.api}/register`, data, { responseType: 'text' });
  }

  token(): string | null {
    return this.isLoggedIn() ? (this.sessionState()?.token ?? null) : null;
  }

  logout(): void {
    this.clear();
    this.router.navigate(['/login']);
  }

  clear(): void {
    sessionStorage.removeItem(STORAGE_KEY);
    this.sessionState.set(null);
  }

  private leerExpiracion(token: string): number {
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.exp * 1000;
    } catch {
      return 0;
    }
  }

  private readSession(): Session | null {
    try {
      const raw = sessionStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return null;
      }
      const session = JSON.parse(raw) as Session;
      return session.expiresAt > Date.now() ? session : null;
    } catch {
      return null;
    }
  }
}
