import { Component, input, OnInit, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormatDatePipe } from '../../../format-date.pipe';
import { IMilitar } from '../militar.model';
import { MilitarService } from '../service/militar.service';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  standalone: true,
  selector: 'jhi-militar-detail',
  templateUrl: './militar-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, FormatDatePipe, HasAnyAuthorityDirective],
})
export class MilitarDetailComponent implements OnInit{
  militar = input<IMilitar | null>(null);
  fotoUrl: string | null = null; // Caminho da foto

  public router = inject(Router);
  protected militarService = inject(MilitarService);

  previousState(): void {
    window.history.back();
  }

  ngOnInit(): void {
    const saram = this.militar()?.saram;
   
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

  formatCpf(cpf: string | null | undefined): string {
      if (!cpf){
        return '';
      } 
      cpf = cpf.replace(/\D/g, ''); // Remove não dígitos
      return cpf.length === 11 ? cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : cpf;
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
