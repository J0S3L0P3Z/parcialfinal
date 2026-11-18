const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])\S{8,64}$/;

const USERNAME_PATTERN = /^[A-Za-z0-9._-]{3,30}$/;

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function claveValida(clave: string): boolean {
  return PASSWORD_PATTERN.test(clave);
}

export function usuarioValido(usuario: string): boolean {
  return USERNAME_PATTERN.test(usuario.trim());
}

export function correoValido(correo: string): boolean {
  return EMAIL_PATTERN.test(correo.trim());
}

export const MENSAJE_CLAVE = 'La contraseña debe tener mínimo 8 caracteres, una minúscula, una mayúscula, un número y un símbolo.';

export const MENSAJE_USUARIO = 'El usuario debe tener de 3 a 30 caracteres: letras, números, punto, guion o guion bajo.';

export const MENSAJE_CORREO = 'El correo no es válido.';
