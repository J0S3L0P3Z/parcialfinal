export type Role = 'USER' | 'ADMIN';

export interface AuthResponse {
  token: string;
  role: Role;
  username: string;
  expiresInMinutes: number;
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

export interface User {
  id: number;
  username: string;
  email: string;
  role: Role;
  enabled: boolean;
  createdAt?: string;
  lastLoginAt?: string;
}

export interface UserForm {
  username?: string;
  email?: string;
  password?: string;
  role?: Role;
  enabled?: boolean;
}

export interface AuditLog {
  id: number;
  userId?: number;
  username?: string;
  action: string;
  detail?: string;
  ip?: string;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface Stats {
  totalUsers: number;
  enabledUsers: number;
  admins: number;
  auditEntries: number;
  auditByAction: Record<string, number>;
}

export interface Fact {
  id: string;
  text: string;
  source: string;
  source_url: string;
  language: string;
  permalink: string;
  statuscode: number;
}
