import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPosto } from '../posto.model';

@Component({
  standalone: true,
  selector: 'jhi-posto-detail',
  templateUrl: './posto-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PostoDetailComponent {
  posto = input<IPosto | null>(null);

  previousState(): void {
    window.history.back();
  }

  getOrgaoName(orgao: number | null | undefined): string {
    if (orgao == null) {
      return 'DESCONHECIDO';
    }
    
    switch (orgao) {
      case 1:
        return 'MARINHA DO BRASIL';
      case 2:
        return 'EXÉRCITO BRASILEIRO';
      case 3:
        return 'FORÇA AÉREA BRASILEIRA';
      case 4:
        return 'POLÍCIA MILITAR';
      case 5:
        return 'CORPO DE BOMBEIROS';
      case 6:
        return 'ÓRGÃO CIVIL BRASILEIRO';
      case 7:
        return 'ÓRGÃO ESTRANGEIRO';
      default:
        return 'DESCONHECIDO';
    }
  }
}
