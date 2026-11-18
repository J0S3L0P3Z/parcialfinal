import { Component, OnInit, inject, signal } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { AuditLog, Page } from '../../models/models';
import { errorMessage } from '../../utils/http-error';

@Component({
  selector: 'app-auditoriacomponent',
  standalone: false,
  styleUrl: './auditoriacomponent.css',
  templateUrl: './auditoriacomponent.html',
})
export class Auditoriacomponent implements OnInit {
  private admin = inject(AdminService);

  acciones = [
    'REGISTER',
    'LOGIN_SUCCESS',
    'LOGIN_FAILED',
    'LOGOUT',
    'USER_CREATED',
    'USER_UPDATED',
    'USER_DELETED',
    'EXTERNAL_API_CALL',
  ];

  accion = '';
  pagina = signal<Page<AuditLog> | null>(null);
  error = signal('');

  ngOnInit() {
    this.cargar(0);
  }

  cargar(numero: number) {
    this.error.set('');
    this.admin.audit(numero, 20, this.accion || undefined).subscribe({
      next: (p) => this.pagina.set(p),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
