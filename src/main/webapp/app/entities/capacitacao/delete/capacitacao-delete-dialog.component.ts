import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICapacitacao } from '../capacitacao.model';
import { CapacitacaoService } from '../service/capacitacao.service';

@Component({
  standalone: true,
  templateUrl: './capacitacao-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CapacitacaoDeleteDialogComponent {
  capacitacao?: ICapacitacao;

  protected capacitacaoService = inject(CapacitacaoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.capacitacaoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
