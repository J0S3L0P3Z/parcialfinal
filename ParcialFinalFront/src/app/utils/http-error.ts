import { HttpErrorResponse } from '@angular/common/http';

export function errorMessage(err: unknown, fallback = 'Ocurrió un error inesperado'): string {
  if (!(err instanceof HttpErrorResponse)) {
    return fallback;
  }
  if (err.status === 0) {
    return 'No se pudo conectar con el servidor';
  }
  if (err.status === 403) {
    return 'No tienes permisos para esta acción';
  }
  const body = err.error;
  if (typeof body === 'string' && body.trim()) {
    return body;
  }
  return fallback;
}
