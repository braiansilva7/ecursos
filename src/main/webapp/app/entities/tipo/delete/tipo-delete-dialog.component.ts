import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITipo } from '../tipo.model';
import { TipoService } from '../service/tipo.service';

@Component({
  standalone: true,
  templateUrl: './tipo-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TipoDeleteDialogComponent {
  tipo?: ITipo;

  protected tipoService = inject(TipoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tipoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
