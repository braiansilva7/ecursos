package com.ecursos.myapp.domain;

import com.ecursos.myapp.domain.enumeration.ModalidadeEnum;
import com.ecursos.myapp.domain.enumeration.StatusCursoEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Turma.
 */
@Entity
@Table(name = "turma")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "turma")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Turma implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "inicio")
    private LocalDate inicio;

    @Column(name = "termino")
    private LocalDate termino;

    @Column(name = "ano")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer ano;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_curso", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private StatusCursoEnum statusCurso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ModalidadeEnum modalidade;

    @Column(name = "qtd_vagas")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String qtdVagas;

    @Column(name = "numero_bca")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String numeroBca;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tipo" }, allowSetters = true)
    private Curso curso;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Turma id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getInicio() {
        return this.inicio;
    }

    public Turma inicio(LocalDate inicio) {
        this.setInicio(inicio);
        return this;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getTermino() {
        return this.termino;
    }

    public Turma termino(LocalDate termino) {
        this.setTermino(termino);
        return this;
    }

    public void setTermino(LocalDate termino) {
        this.termino = termino;
    }

    public Integer getAno() {
        return this.ano;
    }

    public Turma ano(Integer ano) {
        this.setAno(ano);
        return this;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public StatusCursoEnum getStatusCurso() {
        return this.statusCurso;
    }

    public Turma statusCurso(StatusCursoEnum statusCurso) {
        this.setStatusCurso(statusCurso);
        return this;
    }

    public void setStatusCurso(StatusCursoEnum statusCurso) {
        this.statusCurso = statusCurso;
    }

    public ModalidadeEnum getModalidade() {
        return this.modalidade;
    }

    public Turma modalidade(ModalidadeEnum modalidade) {
        this.setModalidade(modalidade);
        return this;
    }

    public void setModalidade(ModalidadeEnum modalidade) {
        this.modalidade = modalidade;
    }

    public String getQtdVagas() {
        return this.qtdVagas;
    }

    public Turma qtdVagas(String qtdVagas) {
        this.setQtdVagas(qtdVagas);
        return this;
    }

    public void setQtdVagas(String qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public String getNumeroBca() {
        return this.numeroBca;
    }

    public Turma numeroBca(String numeroBca) {
        this.setNumeroBca(numeroBca);
        return this;
    }

    public void setNumeroBca(String numeroBca) {
        this.numeroBca = numeroBca;
    }

    public Curso getCurso() {
        return this.curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Turma curso(Curso curso) {
        this.setCurso(curso);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Turma)) {
            return false;
        }
        return getId() != null && getId().equals(((Turma) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Turma{" +
            "id=" + getId() +
            ", inicio='" + getInicio() + "'" +
            ", termino='" + getTermino() + "'" +
            ", ano=" + getAno() +
            ", statusCurso='" + getStatusCurso() + "'" +
            ", modalidade='" + getModalidade() + "'" +
            ", qtdVagas='" + getQtdVagas() + "'" +
            ", numeroBca='" + getNumeroBca() + "'" +
            "}";
    }
}
