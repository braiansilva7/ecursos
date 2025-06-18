package com.ecursos.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Curso.
 */
@Entity
@Table(name = "curso")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "curso")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "curso_nome", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String cursoNome;

    @NotNull
    @Column(name = "curso_sigla", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String cursoSigla;

    @NotNull
    @Column(name = "empresa", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String empresa;

    @ManyToOne(optional = false)
    @NotNull
    private Tipo tipo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Curso id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCursoNome() {
        return this.cursoNome;
    }

    public Curso cursoNome(String cursoNome) {
        this.setCursoNome(cursoNome);
        return this;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public String getCursoSigla() {
        return this.cursoSigla;
    }

    public Curso cursoSigla(String cursoSigla) {
        this.setCursoSigla(cursoSigla);
        return this;
    }

    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

    public String getEmpresa() {
        return this.empresa;
    }

    public Curso empresa(String empresa) {
        this.setEmpresa(empresa);
        return this;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Tipo getTipo() {
        return this.tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Curso tipo(Tipo tipo) {
        this.setTipo(tipo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Curso)) {
            return false;
        }
        return getId() != null && getId().equals(((Curso) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Curso{" +
            "id=" + getId() +
            ", cursoNome='" + getCursoNome() + "'" +
            ", cursoSigla='" + getCursoSigla() + "'" +
            ", empresa='" + getEmpresa() + "'" +
            "}";
    }
}
