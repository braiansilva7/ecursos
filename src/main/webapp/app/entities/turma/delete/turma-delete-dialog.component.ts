import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITurma } from '../turma.model';
import { TurmaService } from '../service/turma.service';

@Component({
  standalone: true,
  templateUrl: './turma-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TurmaDeleteDialogComponent {
  turma?: ITurma;

  protected turmaService = inject(TurmaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.turmaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
