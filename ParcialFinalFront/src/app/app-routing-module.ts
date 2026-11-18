import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard, guestGuard } from './services/auth.guard';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Registrocomponent } from './components/registrocomponent/registrocomponent';
import { Iniciocomponent } from './components/iniciocomponent/iniciocomponent';
import { Perfilcomponent } from './components/perfilcomponent/perfilcomponent';
import { Apiexternacomponent } from './components/apiexternacomponent/apiexternacomponent';
import { Usuarioscomponent } from './components/usuarioscomponent/usuarioscomponent';
import { Auditoriacomponent } from './components/auditoriacomponent/auditoriacomponent';

const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  { path: 'login', component: Logincomponent, canActivate: [guestGuard] },
  { path: 'registro', component: Registrocomponent, canActivate: [guestGuard] },
  { path: 'inicio', component: Iniciocomponent, canActivate: [authGuard] },
  { path: 'perfil', component: Perfilcomponent, canActivate: [authGuard] },
  { path: 'apiexterna', component: Apiexternacomponent, canActivate: [authGuard] },
  { path: 'usuarios', component: Usuarioscomponent, canActivate: [authGuard], data: { rol: 'ADMIN' } },
  { path: 'auditoria', component: Auditoriacomponent, canActivate: [authGuard], data: { rol: 'ADMIN' } },
  { path: '**', redirectTo: 'inicio' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
