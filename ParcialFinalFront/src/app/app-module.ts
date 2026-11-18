import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { authInterceptor } from './services/auth.interceptor';
import { Logincomponent } from './components/logincomponent/logincomponent';
import { Tiendacomponent } from './components/tiendacomponent/tiendacomponent';

@NgModule({
  declarations: [App, Logincomponent, Tiendacomponent],
  imports: [BrowserModule, CommonModule, FormsModule, AppRoutingModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient(withInterceptors([authInterceptor]))],
  bootstrap: [App],
})
export class AppModule {}
