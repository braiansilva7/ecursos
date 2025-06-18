package com.ecursos.myapp.service.dto;

import com.ecursos.myapp.domain.enumeration.ModalidadeEnum;
import com.ecursos.myapp.domain.enumeration.StatusCursoEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ecursos.myapp.domain.Turma} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurmaDTO implements Serializable {

    private Long id;

    private LocalDate inicio;

    private LocalDate termino;

    private Integer ano;

    @NotNull
    private StatusCursoEnum statusCurso;

    @NotNull
    private ModalidadeEnum modalidade;

    private String qtdVagas;

    private String numeroBca;

    @NotNull
    private CursoDTO curso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getTermino() {
        return termino;
    }

    public void setTermino(LocalDate termino) {
        this.termino = termino;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public StatusCursoEnum getStatusCurso() {
        return statusCurso;
    }

    public void setStatusCurso(StatusCursoEnum statusCurso) {
        this.statusCurso = statusCurso;
    }

    public ModalidadeEnum getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    public String getQtdVagas() {
        return qtdVagas;
    }

    public void setQtdVagas(String qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public String getNumeroBca() {
        return numeroBca;
    }

    public void setNumeroBca(String numeroBca) {
        this.numeroBca = numeroBca;
    }

    public CursoDTO getCurso() {
        return curso;
    }

    public void setCurso(CursoDTO curso) {
        this.curso = curso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TurmaDTO)) {
            return false;
        }

        TurmaDTO turmaDTO = (TurmaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, turmaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurmaDTO{" +
            "id=" + getId() +
            ", inicio='" + getInicio() + "'" +
            ", termino='" + getTermino() + "'" +
            ", ano=" + getAno() +
            ", statusCurso='" + getStatusCurso() + "'" +
            ", modalidade='" + getModalidade() + "'" +
            ", qtdVagas='" + getQtdVagas() + "'" +
            ", numeroBca='" + getNumeroBca() + "'" +
            ", curso=" + getCurso() +
            "}";
    }
}
