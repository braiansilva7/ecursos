package com.ecursos.myapp.service.criteria;

import com.ecursos.myapp.domain.enumeration.ModalidadeEnum;
import com.ecursos.myapp.domain.enumeration.StatusCursoEnum;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ecursos.myapp.domain.Turma} entity. This class is used
 * in {@link com.ecursos.myapp.web.rest.TurmaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /turmas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TurmaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatusCursoEnum
     */
    public static class StatusCursoEnumFilter extends Filter<StatusCursoEnum> {

        public StatusCursoEnumFilter() {}

        public StatusCursoEnumFilter(StatusCursoEnumFilter filter) {
            super(filter);
        }

        @Override
        public StatusCursoEnumFilter copy() {
            return new StatusCursoEnumFilter(this);
        }
    }

    /**
     * Class for filtering ModalidadeEnum
     */
    public static class ModalidadeEnumFilter extends Filter<ModalidadeEnum> {

        public ModalidadeEnumFilter() {}

        public ModalidadeEnumFilter(ModalidadeEnumFilter filter) {
            super(filter);
        }

        @Override
        public ModalidadeEnumFilter copy() {
            return new ModalidadeEnumFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter inicio;

    private LocalDateFilter termino;

    private IntegerFilter ano;

    private StatusCursoEnumFilter statusCurso;

    private ModalidadeEnumFilter modalidade;

    private StringFilter qtdVagas;

    private StringFilter numeroBca;

    private LongFilter cursoId;

    private Boolean distinct;

    public TurmaCriteria() {}

    public TurmaCriteria(TurmaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.inicio = other.optionalInicio().map(LocalDateFilter::copy).orElse(null);
        this.termino = other.optionalTermino().map(LocalDateFilter::copy).orElse(null);
        this.ano = other.optionalAno().map(IntegerFilter::copy).orElse(null);
        this.statusCurso = other.optionalStatusCurso().map(StatusCursoEnumFilter::copy).orElse(null);
        this.modalidade = other.optionalModalidade().map(ModalidadeEnumFilter::copy).orElse(null);
        this.qtdVagas = other.optionalQtdVagas().map(StringFilter::copy).orElse(null);
        this.numeroBca = other.optionalNumeroBca().map(StringFilter::copy).orElse(null);
        this.cursoId = other.optionalCursoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TurmaCriteria copy() {
        return new TurmaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getInicio() {
        return inicio;
    }

    public Optional<LocalDateFilter> optionalInicio() {
        return Optional.ofNullable(inicio);
    }

    public LocalDateFilter inicio() {
        if (inicio == null) {
            setInicio(new LocalDateFilter());
        }
        return inicio;
    }

    public void setInicio(LocalDateFilter inicio) {
        this.inicio = inicio;
    }

    public LocalDateFilter getTermino() {
        return termino;
    }

    public Optional<LocalDateFilter> optionalTermino() {
        return Optional.ofNullable(termino);
    }

    public LocalDateFilter termino() {
        if (termino == null) {
            setTermino(new LocalDateFilter());
        }
        return termino;
    }

    public void setTermino(LocalDateFilter termino) {
        this.termino = termino;
    }

    public IntegerFilter getAno() {
        return ano;
    }

    public Optional<IntegerFilter> optionalAno() {
        return Optional.ofNullable(ano);
    }

    public IntegerFilter ano() {
        if (ano == null) {
            setAno(new IntegerFilter());
        }
        return ano;
    }

    public void setAno(IntegerFilter ano) {
        this.ano = ano;
    }

    public StatusCursoEnumFilter getStatusCurso() {
        return statusCurso;
    }

    public Optional<StatusCursoEnumFilter> optionalStatusCurso() {
        return Optional.ofNullable(statusCurso);
    }

    public StatusCursoEnumFilter statusCurso() {
        if (statusCurso == null) {
            setStatusCurso(new StatusCursoEnumFilter());
        }
        return statusCurso;
    }

    public void setStatusCurso(StatusCursoEnumFilter statusCurso) {
        this.statusCurso = statusCurso;
    }

    public ModalidadeEnumFilter getModalidade() {
        return modalidade;
    }

    public Optional<ModalidadeEnumFilter> optionalModalidade() {
        return Optional.ofNullable(modalidade);
    }

    public ModalidadeEnumFilter modalidade() {
        if (modalidade == null) {
            setModalidade(new ModalidadeEnumFilter());
        }
        return modalidade;
    }

    public void setModalidade(ModalidadeEnumFilter modalidade) {
        this.modalidade = modalidade;
    }

    public StringFilter getQtdVagas() {
        return qtdVagas;
    }

    public Optional<StringFilter> optionalQtdVagas() {
        return Optional.ofNullable(qtdVagas);
    }

    public StringFilter qtdVagas() {
        if (qtdVagas == null) {
            setQtdVagas(new StringFilter());
        }
        return qtdVagas;
    }

    public void setQtdVagas(StringFilter qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public StringFilter getNumeroBca() {
        return numeroBca;
    }

    public Optional<StringFilter> optionalNumeroBca() {
        return Optional.ofNullable(numeroBca);
    }

    public StringFilter numeroBca() {
        if (numeroBca == null) {
            setNumeroBca(new StringFilter());
        }
        return numeroBca;
    }

    public void setNumeroBca(StringFilter numeroBca) {
        this.numeroBca = numeroBca;
    }

    public LongFilter getCursoId() {
        return cursoId;
    }

    public Optional<LongFilter> optionalCursoId() {
        return Optional.ofNullable(cursoId);
    }

    public LongFilter cursoId() {
        if (cursoId == null) {
            setCursoId(new LongFilter());
        }
        return cursoId;
    }

    public void setCursoId(LongFilter cursoId) {
        this.cursoId = cursoId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TurmaCriteria that = (TurmaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(inicio, that.inicio) &&
            Objects.equals(termino, that.termino) &&
            Objects.equals(ano, that.ano) &&
            Objects.equals(statusCurso, that.statusCurso) &&
            Objects.equals(modalidade, that.modalidade) &&
            Objects.equals(qtdVagas, that.qtdVagas) &&
            Objects.equals(numeroBca, that.numeroBca) &&
            Objects.equals(cursoId, that.cursoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inicio, termino, ano, statusCurso, modalidade, qtdVagas, numeroBca, cursoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TurmaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalInicio().map(f -> "inicio=" + f + ", ").orElse("") +
            optionalTermino().map(f -> "termino=" + f + ", ").orElse("") +
            optionalAno().map(f -> "ano=" + f + ", ").orElse("") +
            optionalStatusCurso().map(f -> "statusCurso=" + f + ", ").orElse("") +
            optionalModalidade().map(f -> "modalidade=" + f + ", ").orElse("") +
            optionalQtdVagas().map(f -> "qtdVagas=" + f + ", ").orElse("") +
            optionalNumeroBca().map(f -> "numeroBca=" + f + ", ").orElse("") +
            optionalCursoId().map(f -> "cursoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
