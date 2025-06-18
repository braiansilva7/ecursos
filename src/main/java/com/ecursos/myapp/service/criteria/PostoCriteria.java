package com.ecursos.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ecursos.myapp.domain.Posto} entity. This class is used
 * in {@link com.ecursos.myapp.web.rest.PostoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /postos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter postoSigla;

    private StringFilter descricao;

    private IntegerFilter prioridade;

    private IntegerFilter orgao;

    private IntegerFilter codSigpes;

    private Boolean distinct;

    public PostoCriteria() {}

    public PostoCriteria(PostoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.postoSigla = other.optionalPostoSigla().map(StringFilter::copy).orElse(null);
        this.descricao = other.optionalDescricao().map(StringFilter::copy).orElse(null);
        this.prioridade = other.optionalPrioridade().map(IntegerFilter::copy).orElse(null);
        this.orgao = other.optionalOrgao().map(IntegerFilter::copy).orElse(null);
        this.codSigpes = other.optionalCodSigpes().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PostoCriteria copy() {
        return new PostoCriteria(this);
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

    public StringFilter getPostoSigla() {
        return postoSigla;
    }

    public Optional<StringFilter> optionalPostoSigla() {
        return Optional.ofNullable(postoSigla);
    }

    public StringFilter postoSigla() {
        if (postoSigla == null) {
            setPostoSigla(new StringFilter());
        }
        return postoSigla;
    }

    public void setPostoSigla(StringFilter postoSigla) {
        this.postoSigla = postoSigla;
    }

    public StringFilter getDescricao() {
        return descricao;
    }

    public Optional<StringFilter> optionalDescricao() {
        return Optional.ofNullable(descricao);
    }

    public StringFilter descricao() {
        if (descricao == null) {
            setDescricao(new StringFilter());
        }
        return descricao;
    }

    public void setDescricao(StringFilter descricao) {
        this.descricao = descricao;
    }

    public IntegerFilter getPrioridade() {
        return prioridade;
    }

    public Optional<IntegerFilter> optionalPrioridade() {
        return Optional.ofNullable(prioridade);
    }

    public IntegerFilter prioridade() {
        if (prioridade == null) {
            setPrioridade(new IntegerFilter());
        }
        return prioridade;
    }

    public void setPrioridade(IntegerFilter prioridade) {
        this.prioridade = prioridade;
    }

    public IntegerFilter getOrgao() {
        return orgao;
    }

    public Optional<IntegerFilter> optionalOrgao() {
        return Optional.ofNullable(orgao);
    }

    public IntegerFilter orgao() {
        if (orgao == null) {
            setOrgao(new IntegerFilter());
        }
        return orgao;
    }

    public void setOrgao(IntegerFilter orgao) {
        this.orgao = orgao;
    }

    public IntegerFilter getCodSigpes() {
        return codSigpes;
    }

    public Optional<IntegerFilter> optionalCodSigpes() {
        return Optional.ofNullable(codSigpes);
    }

    public IntegerFilter codSigpes() {
        if (codSigpes == null) {
            setCodSigpes(new IntegerFilter());
        }
        return codSigpes;
    }

    public void setCodSigpes(IntegerFilter codSigpes) {
        this.codSigpes = codSigpes;
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
        final PostoCriteria that = (PostoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(postoSigla, that.postoSigla) &&
            Objects.equals(descricao, that.descricao) &&
            Objects.equals(prioridade, that.prioridade) &&
            Objects.equals(orgao, that.orgao) &&
            Objects.equals(codSigpes, that.codSigpes) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postoSigla, descricao, prioridade, orgao, codSigpes, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPostoSigla().map(f -> "postoSigla=" + f + ", ").orElse("") +
            optionalDescricao().map(f -> "descricao=" + f + ", ").orElse("") +
            optionalPrioridade().map(f -> "prioridade=" + f + ", ").orElse("") +
            optionalOrgao().map(f -> "orgao=" + f + ", ").orElse("") +
            optionalCodSigpes().map(f -> "codSigpes=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
