import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable, firstValueFrom } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IPosto } from 'app/entities/posto/posto.model';
import { PostoService } from 'app/entities/posto/service/posto.service';
import { StatusMilitarEnum } from 'app/entities/enumerations/status-militar-enum.model';
import { ForcaEnum } from 'app/entities/enumerations/forca-enum.model';
import { MilitarService } from '../service/militar.service';
import { IMilitar } from '../militar.model';
import { MilitarFormGroup, MilitarFormService } from './militar-form.service';
import dayjs from 'dayjs';

@Component({
  standalone: true,
  selector: 'jhi-militar-update',
  templateUrl: './militar-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MilitarUpdateComponent implements OnInit {
  isSaving = false;
  militar: IMilitar | null = null;
  statusMilitarEnumValues = Object.keys(StatusMilitarEnum);
  forcaEnumValues = Object.keys(ForcaEnum);

  postosSharedCollection: IPosto[] = [];
  filteredPostos: IPosto[] = []; // Adiciona esta variável para os postos filtrados

  protected militarService = inject(MilitarService);
  protected militarFormService = inject(MilitarFormService);
  protected postoService = inject(PostoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MilitarFormGroup = this.militarFormService.createMilitarFormGroup();

  comparePosto = (o1: IPosto | null, o2: IPosto | null): boolean => this.postoService.comparePosto(o1, o2);

  // Novo método para lidar com a mudança de força
  onForcaChange(event: any): void {
    const selectedForca = event.target.selectedIndex;
    // Limpar os resultados anteriores antes de aplicar o novo filtro
    this.filteredPostos = [];
    // Filtrar os postos com base no órgão selecionado
    if (selectedForca) {
      this.filteredPostos = this.postosSharedCollection
      .filter(posto => Number(posto.orgao) === selectedForca)
      .sort((a, b) => {
        // Usando o operador de coalescência nula (??)
        const prioridadeA = a.prioridade ?? Number.MAX_SAFE_INTEGER;
        const prioridadeB = b.prioridade ?? Number.MAX_SAFE_INTEGER;
        
        return prioridadeA - prioridadeB;
      });
    } else {
      this.filteredPostos = this.postosSharedCollection;
    }
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ militar }) => {
      this.militar = militar;
      if (militar) {
        if (militar?.cpf) {
            militar.cpf = this.formatarCpf(militar.cpf);
        }
        this.updateForm(militar);
      }
      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const militar = this.militarFormService.getMilitar(this.editForm);
    if (militar.id !== null) {
      this.subscribeToSaveResponse(this.militarService.update(militar));
    } else {
      this.subscribeToSaveResponse(this.militarService.create(militar));
    }
  }

   // Método chamado ao clicar no ícone de pesquisa
   pesquisarSaram(): void {
    const saram = this.editForm.get('saram')?.value;
    if (saram) {
      // Chame seu método consultaMilitar aqui passando o valor 'saram'
      this.consultaMilitar(saram);
    }
  }

  formatarCpf(cpf: string | null | undefined): string {
    if (!cpf){
      return '';
    } 
    cpf = cpf.replace(/\D/g, ''); // Remove não dígitos
    return cpf.length === 11 ? cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4') : cpf;
  }

  formatCpf(event: any): void {
    let value = event.target.value.replace(/\D/g, ''); // Remove caracteres não numéricos
    if (value.length > 11) {
      value = value.slice(0, 11);
    } 
    event.target.value = value
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2'); // Aplica o formato 000.000.000-00
  }

  formatarData(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, ''); // Remove todos os caracteres não numéricos

    if (value.length > 2 && value.length <= 4) {
      value = value.replace(/^(\d{2})/, '$1/'); // Adiciona a barra após o dia
    } else if (value.length > 4) {
      value = value.replace(/^(\d{2})(\d{2})/, '$1/$2/'); // Adiciona a barra após o mês
    }

    input.value = value.substring(0, 10); // Limita o tamanho do campo
  }

  async consultaMilitar(saram: string): Promise<void> {    
    const statusMap: Record<string, "ATIVA" | "INATIVO" | "TRANSFERIDO" | null> = {
        "ATIVO": "ATIVA",
        "INATIVA": "INATIVO",
        "INAT": "INATIVO",
        "RESERVA": "TRANSFERIDO",
        "APOSENTADO": "TRANSFERIDO"
    };

    try {
        const militarData = await firstValueFrom(this.militarService.consultaMilitarSaram(saram));
        const postosOrgao3 = this.postosSharedCollection.filter(posto => Number(posto.orgao) === 3);
        this.filteredPostos = postosOrgao3;
        const selectedPosto = postosOrgao3.find(posto => posto.codSigpes === Number(militarData.body?.posto));
        //Mapeie o valor de pesfis_type para o valor esperado em statusMilitar
        const statusMilitar = militarData.body?.perfil ? statusMap[militarData.body.perfil] ?? null : null;
        const dataFormatada = dayjs(militarData.body?.ultimaPromocao, 'YYYY-MM-DD'); // Cria um objeto Dayjs a partir da string

        // Utilize patchValue para preencher o formulário com os dados retornados
        this.editForm.patchValue({
            nomeCompleto: militarData.body?.pessoa,
            nomeGuerra: militarData.body?.guerra,
            om: militarData.body?.sgOrg,
            telefone: militarData.body?.telefoneCel,
            cpf: this.formatarCpf(militarData.body?.cpf),
            email: militarData.body?.email,
            nrAntiguidade: militarData.body?.nrAntiguidade,
            ultimaPromocao: dataFormatada,
            statusMilitar,
            forca: "FORCA_AEREA_BRASILEIRA",
            posto: selectedPosto ?? null,
        });
    } catch (error) {
        console.error('Erro ao buscar dados do militar:', error);
    }
}

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMilitar>>): void {
    result.pipe(finalize(() => { this.onSaveFinalize(); })).subscribe({
      next: () => { this.onSaveSuccess(); },
      error: () => { this.onSaveError(); },
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(militar: IMilitar): void {
    this.militar = militar;
    const ultimaPromocao = dayjs(militar.ultimaPromocao, 'YYYY-MM-DD'); // Cria um objeto Dayjs a partir da string
    this.militarFormService.resetForm(this.editForm, {
      ...militar,
      ultimaPromocao // Preenche o campo do formulário já formatado
  });

    this.postosSharedCollection = this.postoService.addPostoToCollectionIfMissing<IPosto>(this.postosSharedCollection, militar.posto);
    this.filteredPostos = this.postosSharedCollection; // Inicializa a lista filtrada
  }

  protected loadRelationshipsOptions(): void {
    this.postoService
      .query()
      .pipe(map((res: HttpResponse<IPosto[]>) => res.body ?? []))
      .pipe(map((postos: IPosto[]) => this.postoService.addPostoToCollectionIfMissing<IPosto>(postos, this.militar?.posto)))
      .subscribe((postos: IPosto[]) => {
        this.postosSharedCollection = postos;
    });
  }
}
