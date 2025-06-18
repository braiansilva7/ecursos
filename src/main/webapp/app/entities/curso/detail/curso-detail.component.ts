import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ICurso } from '../curso.model';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  standalone: true,
  selector: 'jhi-curso-detail',
  templateUrl: './curso-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, HasAnyAuthorityDirective],
})
export class CursoDetailComponent {
  curso = input<ICurso | null>(null);

  previousState(): void {
    window.history.back();
  }
}
