import { Component, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { ExternalService } from '../../services/external.service';
import { Fact } from '../../models/models';
import { errorMessage } from '../../utils/http-error';

@Component({
  selector: 'app-apiexternacomponent',
  standalone: false,
  styleUrl: './apiexternacomponent.css',
  templateUrl: './apiexternacomponent.html',
})
export class Apiexternacomponent {
  private externa = inject(ExternalService);

  idioma = 'en';
  dato = signal<Fact | null>(null);
  historial = signal<Fact[]>([]);
  cargando = signal(false);
  error = signal('');

  datoAleatorio() {
    this.consultar(this.externa.randomFact(this.idioma));
  }

  datoDelDia() {
    this.consultar(this.externa.todayFact(this.idioma));
  }

  private consultar(peticion: Observable<Fact>) {
    this.cargando.set(true);
    this.error.set('');
    peticion.subscribe({
      next: (f) => {
        this.dato.set(f);
        this.historial.update((lista) => [f, ...lista.filter((x) => x.id !== f.id)].slice(0, 10));
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err, 'No se pudo consultar la API externa'));
        this.cargando.set(false);
      },
    });
  }
}
