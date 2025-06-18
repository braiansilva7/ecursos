package com.ecursos.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Posto.
 */
@Entity
@Table(name = "posto")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "posto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Posto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "posto_sigla", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String postoSigla;

    @NotNull
    @Column(name = "descricao", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String descricao;

    @NotNull
    @Column(name = "prioridade", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer prioridade;

    @NotNull
    @Column(name = "orgao", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer orgao;

    @Column(name = "cod_sigpes")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer codSigpes;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Posto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostoSigla() {
        return this.postoSigla;
    }

    public Posto postoSigla(String postoSigla) {
        this.setPostoSigla(postoSigla);
        return this;
    }

    public void setPostoSigla(String postoSigla) {
        this.postoSigla = postoSigla;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public Posto descricao(String descricao) {
        this.setDescricao(descricao);
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPrioridade() {
        return this.prioridade;
    }

    public Posto prioridade(Integer prioridade) {
        this.setPrioridade(prioridade);
        return this;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public Integer getOrgao() {
        return this.orgao;
    }

    public Posto orgao(Integer orgao) {
        this.setOrgao(orgao);
        return this;
    }

    public void setOrgao(Integer orgao) {
        this.orgao = orgao;
    }

    public Integer getCodSigpes() {
        return this.codSigpes;
    }

    public Posto codSigpes(Integer codSigpes) {
        this.setCodSigpes(codSigpes);
        return this;
    }

    public void setCodSigpes(Integer codSigpes) {
        this.codSigpes = codSigpes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Posto)) {
            return false;
        }
        return getId() != null && getId().equals(((Posto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Posto{" +
            "id=" + getId() +
            ", postoSigla='" + getPostoSigla() + "'" +
            ", descricao='" + getDescricao() + "'" +
            ", prioridade=" + getPrioridade() +
            ", orgao=" + getOrgao() +
            ", codSigpes=" + getCodSigpes() +
            "}";
    }
}
