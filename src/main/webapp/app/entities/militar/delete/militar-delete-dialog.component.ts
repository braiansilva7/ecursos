import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMilitar } from '../militar.model';
import { MilitarService } from '../service/militar.service';

@Component({
  standalone: true,
  templateUrl: './militar-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MilitarDeleteDialogComponent {
  militar?: IMilitar;

  protected militarService = inject(MilitarService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.militarService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
