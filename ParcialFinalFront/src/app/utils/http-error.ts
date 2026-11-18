import { HttpErrorResponse } from '@angular/common/http';

export function errorMessage(err: unknown, fallback = 'Ocurrió un error inesperado'): string {
  if (!(err instanceof HttpErrorResponse)) {
    return fallback;
  }
  if (err.status === 0) {
    return 'No se pudo conectar con el servidor';
  }
  const body = err.error;
  if (typeof body === 'string' && body.trim()) {
    try {
      const parsed = JSON.parse(body);
      return parsed?.message ?? body;
    } catch {
      return body;
    }
  }
  if (body && typeof body === 'object' && 'message' in body) {
    return String((body as { message: unknown }).message);
  }
  return fallback;
}
