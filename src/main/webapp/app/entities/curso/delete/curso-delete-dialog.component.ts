import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICurso } from '../curso.model';
import { CursoService } from '../service/curso.service';

@Component({
  standalone: true,
  templateUrl: './curso-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CursoDeleteDialogComponent {
  curso?: ICurso;

  protected cursoService = inject(CursoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cursoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
