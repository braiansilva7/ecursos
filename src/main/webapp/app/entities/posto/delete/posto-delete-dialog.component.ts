import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPosto } from '../posto.model';
import { PostoService } from '../service/posto.service';

@Component({
  standalone: true,
  templateUrl: './posto-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PostoDeleteDialogComponent {
  posto?: IPosto;

  protected postoService = inject(PostoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.postoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
