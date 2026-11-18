export type Role = 'USER' | 'ADMIN';

export type TipoProducto = 'lacteo' | 'carne';

export interface AuthResponse {
  token: string;
  role: Role;
}

export interface Session {
  token: string;
  role: Role;
  username: string;
  expiresAt: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface Producto {
  id?: number;
  nombre: string;
  cantidad: number;
  precio: number;
  urlImagen: string;
}
