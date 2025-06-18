import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ITipo } from '../tipo.model';

@Component({
  standalone: true,
  selector: 'jhi-tipo-detail',
  templateUrl: './tipo-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TipoDetailComponent {
  tipo = input<ITipo | null>(null);

  previousState(): void {
    window.history.back();
  }
}
