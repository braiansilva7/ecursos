package com.ecursos.myapp.service.dto;

import com.ecursos.myapp.domain.enumeration.StatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ecursos.myapp.domain.Capacitacao} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CapacitacaoDTO implements Serializable {

    private Long id;

    @NotNull
    private StatusEnum capacitacaoStatus;

    private String sigpes;

    private MilitarDTO militar;

    @NotNull
    private TurmaDTO turma;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusEnum getCapacitacaoStatus() {
        return capacitacaoStatus;
    }

    public void setCapacitacaoStatus(StatusEnum capacitacaoStatus) {
        this.capacitacaoStatus = capacitacaoStatus;
    }

    public String getSigpes() {
        return sigpes;
    }

    public void setSigpes(String sigpes) {
        this.sigpes = sigpes;
    }

    public MilitarDTO getMilitar() {
        return militar;
    }

    public void setMilitar(MilitarDTO militar) {
        this.militar = militar;
    }

    public TurmaDTO getTurma() {
        return turma;
    }

    public void setTurma(TurmaDTO turma) {
        this.turma = turma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CapacitacaoDTO)) {
            return false;
        }

        CapacitacaoDTO capacitacaoDTO = (CapacitacaoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, capacitacaoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CapacitacaoDTO{" +
            "id=" + getId() +
            ", capacitacaoStatus='" + getCapacitacaoStatus() + "'" +
            ", sigpes='" + getSigpes() + "'" +
            ", militar=" + getMilitar() +
            ", turma=" + getTurma() +
            "}";
    }
}
