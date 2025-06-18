package com.ecursos.myapp.service.criteria;

import com.ecursos.myapp.domain.enumeration.StatusEnum;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ecursos.myapp.domain.Capacitacao} entity. This class is used
 * in {@link com.ecursos.myapp.web.rest.CapacitacaoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /capacitacaos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CapacitacaoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatusEnum
     */
    public static class StatusEnumFilter extends Filter<StatusEnum> {

        public StatusEnumFilter() {}

        public StatusEnumFilter(StatusEnumFilter filter) {
            super(filter);
        }

        @Override
        public StatusEnumFilter copy() {
            return new StatusEnumFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StatusEnumFilter capacitacaoStatus;

    private StringFilter sigpes;

    private LongFilter militarId;

    private LongFilter turmaId;

    private Boolean distinct;

    public CapacitacaoCriteria() {}

    public CapacitacaoCriteria(CapacitacaoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.capacitacaoStatus = other.optionalCapacitacaoStatus().map(StatusEnumFilter::copy).orElse(null);
        this.sigpes = other.optionalSigpes().map(StringFilter::copy).orElse(null);
        this.militarId = other.optionalMilitarId().map(LongFilter::copy).orElse(null);
        this.turmaId = other.optionalTurmaId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CapacitacaoCriteria copy() {
        return new CapacitacaoCriteria(this);
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

    public StatusEnumFilter getCapacitacaoStatus() {
        return capacitacaoStatus;
    }

    public Optional<StatusEnumFilter> optionalCapacitacaoStatus() {
        return Optional.ofNullable(capacitacaoStatus);
    }

    public StatusEnumFilter capacitacaoStatus() {
        if (capacitacaoStatus == null) {
            setCapacitacaoStatus(new StatusEnumFilter());
        }
        return capacitacaoStatus;
    }

    public void setCapacitacaoStatus(StatusEnumFilter capacitacaoStatus) {
        this.capacitacaoStatus = capacitacaoStatus;
    }

    public StringFilter getSigpes() {
        return sigpes;
    }

    public Optional<StringFilter> optionalSigpes() {
        return Optional.ofNullable(sigpes);
    }

    public StringFilter sigpes() {
        if (sigpes == null) {
            setSigpes(new StringFilter());
        }
        return sigpes;
    }

    public void setSigpes(StringFilter sigpes) {
        this.sigpes = sigpes;
    }

    public LongFilter getMilitarId() {
        return militarId;
    }

    public Optional<LongFilter> optionalMilitarId() {
        return Optional.ofNullable(militarId);
    }

    public LongFilter militarId() {
        if (militarId == null) {
            setMilitarId(new LongFilter());
        }
        return militarId;
    }

    public void setMilitarId(LongFilter militarId) {
        this.militarId = militarId;
    }

    public LongFilter getTurmaId() {
        return turmaId;
    }

    public Optional<LongFilter> optionalTurmaId() {
        return Optional.ofNullable(turmaId);
    }

    public LongFilter turmaId() {
        if (turmaId == null) {
            setTurmaId(new LongFilter());
        }
        return turmaId;
    }

    public void setTurmaId(LongFilter turmaId) {
        this.turmaId = turmaId;
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
        final CapacitacaoCriteria that = (CapacitacaoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(capacitacaoStatus, that.capacitacaoStatus) &&
            Objects.equals(sigpes, that.sigpes) &&
            Objects.equals(militarId, that.militarId) &&
            Objects.equals(turmaId, that.turmaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, capacitacaoStatus, sigpes, militarId, turmaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CapacitacaoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCapacitacaoStatus().map(f -> "capacitacaoStatus=" + f + ", ").orElse("") +
            optionalSigpes().map(f -> "sigpes=" + f + ", ").orElse("") +
            optionalMilitarId().map(f -> "militarId=" + f + ", ").orElse("") +
            optionalTurmaId().map(f -> "turmaId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
