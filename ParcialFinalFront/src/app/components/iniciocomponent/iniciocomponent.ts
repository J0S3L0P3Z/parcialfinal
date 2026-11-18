import { Component, OnInit, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AdminService } from '../../services/admin.service';
import { Stats } from '../../models/models';
import { errorMessage } from '../../utils/http-error';

@Component({
  selector: 'app-iniciocomponent',
  standalone: false,
  styleUrl: './iniciocomponent.css',
  templateUrl: './iniciocomponent.html',
})
export class Iniciocomponent implements OnInit {
  auth = inject(AuthService);
  private admin = inject(AdminService);

  estadisticas = signal<Stats | null>(null);
  error = signal('');

  ngOnInit() {
    if (this.auth.isAdmin()) {
      this.admin.stats().subscribe({
        next: (datos) => this.estadisticas.set(datos),
        error: (err) => this.error.set(errorMessage(err, 'No se pudieron cargar las estadísticas')),
      });
    }
  }
}
