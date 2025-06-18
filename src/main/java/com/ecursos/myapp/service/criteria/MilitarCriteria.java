package com.ecursos.myapp.service.criteria;

import com.ecursos.myapp.domain.enumeration.ForcaEnum;
import com.ecursos.myapp.domain.enumeration.StatusMilitarEnum;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ecursos.myapp.domain.Militar} entity. This class is used
 * in {@link com.ecursos.myapp.web.rest.MilitarResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /militars?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MilitarCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatusMilitarEnum
     */
    public static class StatusMilitarEnumFilter extends Filter<StatusMilitarEnum> {

        public StatusMilitarEnumFilter() {}

        public StatusMilitarEnumFilter(StatusMilitarEnumFilter filter) {
            super(filter);
        }

        @Override
        public StatusMilitarEnumFilter copy() {
            return new StatusMilitarEnumFilter(this);
        }
    }

    /**
     * Class for filtering ForcaEnum
     */
    public static class ForcaEnumFilter extends Filter<ForcaEnum> {

        public ForcaEnumFilter() {}

        public ForcaEnumFilter(ForcaEnumFilter filter) {
            super(filter);
        }

        @Override
        public ForcaEnumFilter copy() {
            return new ForcaEnumFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter saram;

    private StringFilter nomeCompleto;

    private StringFilter nomeGuerra;

    private StringFilter om;

    private StringFilter telefone;

    private StatusMilitarEnumFilter statusMilitar;

    private ForcaEnumFilter forca;

    private StringFilter nrAntiguidade;

    private LocalDateFilter ultimaPromocao;

    private StringFilter cpf;

    private StringFilter email;

    private LongFilter postoId;

    private Boolean distinct;

    public MilitarCriteria() {}

    public MilitarCriteria(MilitarCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.saram = other.optionalSaram().map(StringFilter::copy).orElse(null);
        this.nomeCompleto = other.optionalNomeCompleto().map(StringFilter::copy).orElse(null);
        this.nomeGuerra = other.optionalNomeGuerra().map(StringFilter::copy).orElse(null);
        this.om = other.optionalOm().map(StringFilter::copy).orElse(null);
        this.telefone = other.optionalTelefone().map(StringFilter::copy).orElse(null);
        this.statusMilitar = other.optionalStatusMilitar().map(StatusMilitarEnumFilter::copy).orElse(null);
        this.forca = other.optionalForca().map(ForcaEnumFilter::copy).orElse(null);
        this.nrAntiguidade = other.optionalNrAntiguidade().map(StringFilter::copy).orElse(null);
        this.ultimaPromocao = other.optionalUltimaPromocao().map(LocalDateFilter::copy).orElse(null);
        this.cpf = other.optionalCpf().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.postoId = other.optionalPostoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MilitarCriteria copy() {
        return new MilitarCriteria(this);
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

    public StringFilter getSaram() {
        return saram;
    }

    public Optional<StringFilter> optionalSaram() {
        return Optional.ofNullable(saram);
    }

    public StringFilter saram() {
        if (saram == null) {
            setSaram(new StringFilter());
        }
        return saram;
    }

    public void setSaram(StringFilter saram) {
        this.saram = saram;
    }

    public StringFilter getNomeCompleto() {
        return nomeCompleto;
    }

    public Optional<StringFilter> optionalNomeCompleto() {
        return Optional.ofNullable(nomeCompleto);
    }

    public StringFilter nomeCompleto() {
        if (nomeCompleto == null) {
            setNomeCompleto(new StringFilter());
        }
        return nomeCompleto;
    }

    public void setNomeCompleto(StringFilter nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public StringFilter getNomeGuerra() {
        return nomeGuerra;
    }

    public Optional<StringFilter> optionalNomeGuerra() {
        return Optional.ofNullable(nomeGuerra);
    }

    public StringFilter nomeGuerra() {
        if (nomeGuerra == null) {
            setNomeGuerra(new StringFilter());
        }
        return nomeGuerra;
    }

    public void setNomeGuerra(StringFilter nomeGuerra) {
        this.nomeGuerra = nomeGuerra;
    }

    public StringFilter getOm() {
        return om;
    }

    public Optional<StringFilter> optionalOm() {
        return Optional.ofNullable(om);
    }

    public StringFilter om() {
        if (om == null) {
            setOm(new StringFilter());
        }
        return om;
    }

    public void setOm(StringFilter om) {
        this.om = om;
    }

    public StringFilter getTelefone() {
        return telefone;
    }

    public Optional<StringFilter> optionalTelefone() {
        return Optional.ofNullable(telefone);
    }

    public StringFilter telefone() {
        if (telefone == null) {
            setTelefone(new StringFilter());
        }
        return telefone;
    }

    public void setTelefone(StringFilter telefone) {
        this.telefone = telefone;
    }

    public StatusMilitarEnumFilter getStatusMilitar() {
        return statusMilitar;
    }

    public Optional<StatusMilitarEnumFilter> optionalStatusMilitar() {
        return Optional.ofNullable(statusMilitar);
    }

    public StatusMilitarEnumFilter statusMilitar() {
        if (statusMilitar == null) {
            setStatusMilitar(new StatusMilitarEnumFilter());
        }
        return statusMilitar;
    }

    public void setStatusMilitar(StatusMilitarEnumFilter statusMilitar) {
        this.statusMilitar = statusMilitar;
    }

    public ForcaEnumFilter getForca() {
        return forca;
    }

    public Optional<ForcaEnumFilter> optionalForca() {
        return Optional.ofNullable(forca);
    }

    public ForcaEnumFilter forca() {
        if (forca == null) {
            setForca(new ForcaEnumFilter());
        }
        return forca;
    }

    public void setForca(ForcaEnumFilter forca) {
        this.forca = forca;
    }

    public StringFilter getNrAntiguidade() {
        return nrAntiguidade;
    }

    public Optional<StringFilter> optionalNrAntiguidade() {
        return Optional.ofNullable(nrAntiguidade);
    }

    public StringFilter nrAntiguidade() {
        if (nrAntiguidade == null) {
            setNrAntiguidade(new StringFilter());
        }
        return nrAntiguidade;
    }

    public void setNrAntiguidade(StringFilter nrAntiguidade) {
        this.nrAntiguidade = nrAntiguidade;
    }

    public LocalDateFilter getUltimaPromocao() {
        return ultimaPromocao;
    }

    public Optional<LocalDateFilter> optionalUltimaPromocao() {
        return Optional.ofNullable(ultimaPromocao);
    }

    public LocalDateFilter ultimaPromocao() {
        if (ultimaPromocao == null) {
            setUltimaPromocao(new LocalDateFilter());
        }
        return ultimaPromocao;
    }

    public void setUltimaPromocao(LocalDateFilter ultimaPromocao) {
        this.ultimaPromocao = ultimaPromocao;
    }

    public StringFilter getCpf() {
        return cpf;
    }

    public Optional<StringFilter> optionalCpf() {
        return Optional.ofNullable(cpf);
    }

    public StringFilter cpf() {
        if (cpf == null) {
            setCpf(new StringFilter());
        }
        return cpf;
    }

    public void setCpf(StringFilter cpf) {
        this.cpf = cpf;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public LongFilter getPostoId() {
        return postoId;
    }

    public Optional<LongFilter> optionalPostoId() {
        return Optional.ofNullable(postoId);
    }

    public LongFilter postoId() {
        if (postoId == null) {
            setPostoId(new LongFilter());
        }
        return postoId;
    }

    public void setPostoId(LongFilter postoId) {
        this.postoId = postoId;
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
        final MilitarCriteria that = (MilitarCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(saram, that.saram) &&
            Objects.equals(nomeCompleto, that.nomeCompleto) &&
            Objects.equals(nomeGuerra, that.nomeGuerra) &&
            Objects.equals(om, that.om) &&
            Objects.equals(telefone, that.telefone) &&
            Objects.equals(statusMilitar, that.statusMilitar) &&
            Objects.equals(forca, that.forca) &&
            Objects.equals(nrAntiguidade, that.nrAntiguidade) &&
            Objects.equals(ultimaPromocao, that.ultimaPromocao) &&
            Objects.equals(cpf, that.cpf) &&
            Objects.equals(email, that.email) &&
            Objects.equals(postoId, that.postoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            saram,
            nomeCompleto,
            nomeGuerra,
            om,
            telefone,
            statusMilitar,
            forca,
            nrAntiguidade,
            ultimaPromocao,
            cpf,
            email,
            postoId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MilitarCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalSaram().map(f -> "saram=" + f + ", ").orElse("") +
            optionalNomeCompleto().map(f -> "nomeCompleto=" + f + ", ").orElse("") +
            optionalNomeGuerra().map(f -> "nomeGuerra=" + f + ", ").orElse("") +
            optionalOm().map(f -> "om=" + f + ", ").orElse("") +
            optionalTelefone().map(f -> "telefone=" + f + ", ").orElse("") +
            optionalStatusMilitar().map(f -> "statusMilitar=" + f + ", ").orElse("") +
            optionalForca().map(f -> "forca=" + f + ", ").orElse("") +
            optionalNrAntiguidade().map(f -> "nrAntiguidade=" + f + ", ").orElse("") +
            optionalUltimaPromocao().map(f -> "ultimaPromocao=" + f + ", ").orElse("") +
            optionalCpf().map(f -> "cpf=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalPostoId().map(f -> "postoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
