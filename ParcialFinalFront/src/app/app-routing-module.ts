import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard, guestGuard } from './services/auth.guard';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Tiendacomponent } from './components/tiendacomponent/tiendacomponent';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Logincomponent, canActivate: [guestGuard] },
  { path: 'tienda', component: Tiendacomponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
