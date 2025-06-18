package com.ecursos.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ecursos.myapp.domain.Curso} entity. This class is used
 * in {@link com.ecursos.myapp.web.rest.CursoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cursos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CursoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter cursoNome;

    private StringFilter cursoSigla;

    private StringFilter empresa;

    private LongFilter tipoId;

    private Boolean distinct;

    public CursoCriteria() {}

    public CursoCriteria(CursoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.cursoNome = other.optionalCursoNome().map(StringFilter::copy).orElse(null);
        this.cursoSigla = other.optionalCursoSigla().map(StringFilter::copy).orElse(null);
        this.empresa = other.optionalEmpresa().map(StringFilter::copy).orElse(null);
        this.tipoId = other.optionalTipoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CursoCriteria copy() {
        return new CursoCriteria(this);
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

    public StringFilter getCursoNome() {
        return cursoNome;
    }

    public Optional<StringFilter> optionalCursoNome() {
        return Optional.ofNullable(cursoNome);
    }

    public StringFilter cursoNome() {
        if (cursoNome == null) {
            setCursoNome(new StringFilter());
        }
        return cursoNome;
    }

    public void setCursoNome(StringFilter cursoNome) {
        this.cursoNome = cursoNome;
    }

    public StringFilter getCursoSigla() {
        return cursoSigla;
    }

    public Optional<StringFilter> optionalCursoSigla() {
        return Optional.ofNullable(cursoSigla);
    }

    public StringFilter cursoSigla() {
        if (cursoSigla == null) {
            setCursoSigla(new StringFilter());
        }
        return cursoSigla;
    }

    public void setCursoSigla(StringFilter cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

    public StringFilter getEmpresa() {
        return empresa;
    }

    public Optional<StringFilter> optionalEmpresa() {
        return Optional.ofNullable(empresa);
    }

    public StringFilter empresa() {
        if (empresa == null) {
            setEmpresa(new StringFilter());
        }
        return empresa;
    }

    public void setEmpresa(StringFilter empresa) {
        this.empresa = empresa;
    }

    public LongFilter getTipoId() {
        return tipoId;
    }

    public Optional<LongFilter> optionalTipoId() {
        return Optional.ofNullable(tipoId);
    }

    public LongFilter tipoId() {
        if (tipoId == null) {
            setTipoId(new LongFilter());
        }
        return tipoId;
    }

    public void setTipoId(LongFilter tipoId) {
        this.tipoId = tipoId;
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
        final CursoCriteria that = (CursoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cursoNome, that.cursoNome) &&
            Objects.equals(cursoSigla, that.cursoSigla) &&
            Objects.equals(empresa, that.empresa) &&
            Objects.equals(tipoId, that.tipoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cursoNome, cursoSigla, empresa, tipoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CursoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCursoNome().map(f -> "cursoNome=" + f + ", ").orElse("") +
            optionalCursoSigla().map(f -> "cursoSigla=" + f + ", ").orElse("") +
            optionalEmpresa().map(f -> "empresa=" + f + ", ").orElse("") +
            optionalTipoId().map(f -> "tipoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
