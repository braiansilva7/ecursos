package com.ecursos.myapp.domain;

import com.ecursos.myapp.domain.enumeration.ForcaEnum;
import com.ecursos.myapp.domain.enumeration.StatusMilitarEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Militar.
 */
@Entity
@Table(name = "militar")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "militar")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Militar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "saram", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String saram;

    @NotNull
    @Column(name = "nome_completo", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nomeCompleto;

    @NotNull
    @Column(name = "nome_guerra", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nomeGuerra;

    @NotNull
    @Column(name = "om", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String om;

    @Column(name = "telefone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String telefone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_militar", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private StatusMilitarEnum statusMilitar;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "forca", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ForcaEnum forca;

    @Column(name = "nr_antiguidade")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nrAntiguidade;

    @Column(name = "ultima_promocao")
    private LocalDate ultimaPromocao;

    @Column(name = "cpf")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String cpf;

    @Column(name = "email")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @ManyToOne(optional = false)
    @NotNull
    private Posto posto;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Militar id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaram() {
        return this.saram;
    }

    public Militar saram(String saram) {
        this.setSaram(saram);
        return this;
    }

    public void setSaram(String saram) {
        this.saram = saram;
    }

    public String getNomeCompleto() {
        return this.nomeCompleto;
    }

    public Militar nomeCompleto(String nomeCompleto) {
        this.setNomeCompleto(nomeCompleto);
        return this;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeGuerra() {
        return this.nomeGuerra;
    }

    public Militar nomeGuerra(String nomeGuerra) {
        this.setNomeGuerra(nomeGuerra);
        return this;
    }

    public void setNomeGuerra(String nomeGuerra) {
        this.nomeGuerra = nomeGuerra;
    }

    public String getOm() {
        return this.om;
    }

    public Militar om(String om) {
        this.setOm(om);
        return this;
    }

    public void setOm(String om) {
        this.om = om;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public Militar telefone(String telefone) {
        this.setTelefone(telefone);
        return this;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public StatusMilitarEnum getStatusMilitar() {
        return this.statusMilitar;
    }

    public Militar statusMilitar(StatusMilitarEnum statusMilitar) {
        this.setStatusMilitar(statusMilitar);
        return this;
    }

    public void setStatusMilitar(StatusMilitarEnum statusMilitar) {
        this.statusMilitar = statusMilitar;
    }

    public ForcaEnum getForca() {
        return this.forca;
    }

    public Militar forca(ForcaEnum forca) {
        this.setForca(forca);
        return this;
    }

    public void setForca(ForcaEnum forca) {
        this.forca = forca;
    }

    public String getNrAntiguidade() {
        return this.nrAntiguidade;
    }

    public Militar nrAntiguidade(String nrAntiguidade) {
        this.setNrAntiguidade(nrAntiguidade);
        return this;
    }

    public void setNrAntiguidade(String nrAntiguidade) {
        this.nrAntiguidade = nrAntiguidade;
    }

    public LocalDate getUltimaPromocao() {
        return this.ultimaPromocao;
    }

    public Militar ultimaPromocao(LocalDate ultimaPromocao) {
        this.setUltimaPromocao(ultimaPromocao);
        return this;
    }

    public void setUltimaPromocao(LocalDate ultimaPromocao) {
        this.ultimaPromocao = ultimaPromocao;
    }

    public String getCpf() {
        return this.cpf;
    }

    public Militar cpf(String cpf) {
        this.setCpf(cpf);
        return this;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return this.email;
    }

    public Militar email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Posto getPosto() {
        return this.posto;
    }

    public void setPosto(Posto posto) {
        this.posto = posto;
    }

    public Militar posto(Posto posto) {
        this.setPosto(posto);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Militar)) {
            return false;
        }
        return getId() != null && getId().equals(((Militar) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Militar{" +
            "id=" + getId() +
            ", saram='" + getSaram() + "'" +
            ", nomeCompleto='" + getNomeCompleto() + "'" +
            ", nomeGuerra='" + getNomeGuerra() + "'" +
            ", om='" + getOm() + "'" +
            ", telefone='" + getTelefone() + "'" +
            ", statusMilitar='" + getStatusMilitar() + "'" +
            ", forca='" + getForca() + "'" +
            ", nrAntiguidade='" + getNrAntiguidade() + "'" +
            ", ultimaPromocao='" + getUltimaPromocao() + "'" +
            ", cpf='" + getCpf() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
