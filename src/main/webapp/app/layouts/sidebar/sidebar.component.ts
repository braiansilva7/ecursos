import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import SharedModule from 'app/shared/shared.module';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import { LANGUAGES } from 'app/config/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { MilitarService } from '../../entities/militar/service/militar.service';

@Component({
  selector: 'jhi-sidebar',
  standalone: true,
  imports: [RouterModule, SharedModule, HasAnyAuthorityDirective],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export default class SidebarComponent implements OnInit{
  
  account = inject(AccountService).trackCurrentAccount();
  isNavbarCollapsed = signal(true);
  languages = LANGUAGES;
  fotoUrl: string | null = null; // Caminho da foto

  protected militarService = inject(MilitarService);

  private translateService = inject(TranslateService);
  private stateStorageService = inject(StateStorageService);

  ngOnInit(): void {
    const saram = this.account()?.fabnrordem;
   
    if (saram) {
      this.militarService.getFotoApiLocal(saram).subscribe({
        next: (url) => {
          // Use diretamente a URL retornada pela API
          this.fotoUrl = url;

          // Faça uma verificação adicional para garantir que o recurso exista
          this.checkImage(url).then((exists) => {
            if (!exists) {
              console.warn('Imagem não encontrada no servidor:', url);
              this.fotoUrl = null; // Ou defina uma imagem padrão
            }
          });
        },
        error: (err) => {
          console.error('Erro ao carregar foto:', err);
          this.fotoUrl = null; // Define como nulo ou imagem padrão
        },
      });
    } else {
      console.warn('Militar ou SARAM não definido.');
    }
  }

  changeLanguage(languageKey: string): void {
    this.stateStorageService.storeLocale(languageKey);
    this.translateService.use(languageKey);
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  checkImage(url: string): Promise<boolean> {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => resolve(true);
      img.onerror = () => resolve(false);
      img.src = url;
    });
  }
}
