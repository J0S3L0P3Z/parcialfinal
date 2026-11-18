import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { authInterceptor } from './services/auth.interceptor';
import { Navbarcomponent } from './components/navbarcomponent/navbarcomponent';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Registrocomponent } from './components/registrocomponent/registrocomponent';
import { Iniciocomponent } from './components/iniciocomponent/iniciocomponent';
import { Perfilcomponent } from './components/perfilcomponent/perfilcomponent';
import { Apiexternacomponent } from './components/apiexternacomponent/apiexternacomponent';
import { Usuarioscomponent } from './components/usuarioscomponent/usuarioscomponent';
import { Auditoriacomponent } from './components/auditoriacomponent/auditoriacomponent';

@NgModule({
  declarations: [
    App,
    Navbarcomponent,
    Logincomponent,
    Registrocomponent,
    Iniciocomponent,
    Perfilcomponent,
    Apiexternacomponent,
    Usuarioscomponent,
    Auditoriacomponent,
  ],
  imports: [BrowserModule, CommonModule, FormsModule, AppRoutingModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient(withInterceptors([authInterceptor]))],
  bootstrap: [App],
})
export class AppModule {}
