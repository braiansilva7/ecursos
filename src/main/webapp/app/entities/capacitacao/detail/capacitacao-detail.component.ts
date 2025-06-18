import { Component, inject, input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ICapacitacao } from '../capacitacao.model';
import dayjs, { Dayjs } from 'dayjs';
import { MilitarService } from 'app/entities/militar/service/militar.service';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  standalone: true,
  selector: 'jhi-capacitacao-detail',
  templateUrl: './capacitacao-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, HasAnyAuthorityDirective],
})
export class CapacitacaoDetailComponent implements OnInit{

  fotoUrl: string | null = null; // Caminho da foto
  capacitacao = input<ICapacitacao | null>(null);
  
  protected militarService = inject(MilitarService);

  ngOnInit(): void {
    this.onGetFoto(this.capacitacao()?.militar?.saram);
  }
  
  previousState(): void {
    window.history.back();
  }
  
  getFormattedDate(inicio: Dayjs | string | null | undefined): string {
    if (inicio) {
      if (dayjs.isDayjs(inicio)) {
        return inicio.format('DD/MM/YYYY');
      }
      return dayjs(inicio).format('DD/MM/YYYY');
    }
    return 'NULL';
  }

  onGetFoto(saram: any): void {
    //Verifica se o valor foi encontrado antes de continuar
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

  checkImage(url: string): Promise<boolean> {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => resolve(true);
      img.onerror = () => resolve(false);
      img.src = url;
    });
  }
  
}
