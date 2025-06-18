import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { FormatDatePipe } from '../../../format-date.pipe';
import { ITurma } from '../turma.model';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  standalone: true,
  selector: 'jhi-turma-detail',
  templateUrl: './turma-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, FormatDatePipe, HasAnyAuthorityDirective],
})
export class TurmaDetailComponent {
  turma = input<ITurma | null>(null);

  previousState(): void {
    window.history.back();
  }
}
