package com.ecursos.myapp.domain;

import com.ecursos.myapp.domain.enumeration.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Capacitacao.
 */
@Entity
@Table(name = "capacitacao")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "capacitacao")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Capacitacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "capacitacao_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private StatusEnum capacitacaoStatus;

    @Column(name = "sigpes")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String sigpes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "posto" }, allowSetters = true)
    private Militar militar;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "curso" }, allowSetters = true)
    private Turma turma;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Capacitacao id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusEnum getCapacitacaoStatus() {
        return this.capacitacaoStatus;
    }

    public Capacitacao capacitacaoStatus(StatusEnum capacitacaoStatus) {
        this.setCapacitacaoStatus(capacitacaoStatus);
        return this;
    }

    public void setCapacitacaoStatus(StatusEnum capacitacaoStatus) {
        this.capacitacaoStatus = capacitacaoStatus;
    }

    public String getSigpes() {
        return this.sigpes;
    }

    public Capacitacao sigpes(String sigpes) {
        this.setSigpes(sigpes);
        return this;
    }

    public void setSigpes(String sigpes) {
        this.sigpes = sigpes;
    }

    public Militar getMilitar() {
        return this.militar;
    }

    public void setMilitar(Militar militar) {
        this.militar = militar;
    }

    public Capacitacao militar(Militar militar) {
        this.setMilitar(militar);
        return this;
    }

    public Turma getTurma() {
        return this.turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Capacitacao turma(Turma turma) {
        this.setTurma(turma);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Capacitacao)) {
            return false;
        }
        return getId() != null && getId().equals(((Capacitacao) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Capacitacao{" +
            "id=" + getId() +
            ", capacitacaoStatus='" + getCapacitacaoStatus() + "'" +
            ", sigpes='" + getSigpes() + "'" +
            "}";
    }
}
