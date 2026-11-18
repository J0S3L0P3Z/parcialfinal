import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Fact } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ExternalService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/external`;

  randomFact(language = 'en'): Observable<Fact> {
    return this.http.get<Fact>(`${this.api}/fact/random`, { params: { language } });
  }

  todayFact(language = 'en'): Observable<Fact> {
    return this.http.get<Fact>(`${this.api}/fact/today`, { params: { language } });
  }
}
