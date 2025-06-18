package com.ecursos.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ecursos.myapp.domain.Tipo} entity. This class is used
 * in {@link com.ecursos.myapp.web.rest.TipoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tipos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TipoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter categoria;

    private Boolean distinct;

    public TipoCriteria() {}

    public TipoCriteria(TipoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.categoria = other.optionalCategoria().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TipoCriteria copy() {
        return new TipoCriteria(this);
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

    public StringFilter getCategoria() {
        return categoria;
    }

    public Optional<StringFilter> optionalCategoria() {
        return Optional.ofNullable(categoria);
    }

    public StringFilter categoria() {
        if (categoria == null) {
            setCategoria(new StringFilter());
        }
        return categoria;
    }

    public void setCategoria(StringFilter categoria) {
        this.categoria = categoria;
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
        final TipoCriteria that = (TipoCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(categoria, that.categoria) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoria, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TipoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCategoria().map(f -> "categoria=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
