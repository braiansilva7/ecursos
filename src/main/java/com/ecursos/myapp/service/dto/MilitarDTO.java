package com.ecursos.myapp.service.dto;

import com.ecursos.myapp.domain.enumeration.ForcaEnum;
import com.ecursos.myapp.domain.enumeration.StatusMilitarEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ecursos.myapp.domain.Militar} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MilitarDTO implements Serializable {

    private Long id;

    @NotNull
    private String saram;

    @NotNull
    private String nomeCompleto;

    @NotNull
    private String nomeGuerra;

    @NotNull
    private String om;

    private String telefone;

    @NotNull
    private StatusMilitarEnum statusMilitar;

    @NotNull
    private ForcaEnum forca;

    private String nrAntiguidade;

    private LocalDate ultimaPromocao;

    private String cpf;

    private String email;

    @NotNull
    private PostoDTO posto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaram() {
        return saram;
    }

    public void setSaram(String saram) {
        this.saram = saram;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeGuerra() {
        return nomeGuerra;
    }

    public void setNomeGuerra(String nomeGuerra) {
        this.nomeGuerra = nomeGuerra;
    }

    public String getOm() {
        return om;
    }

    public void setOm(String om) {
        this.om = om;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public StatusMilitarEnum getStatusMilitar() {
        return statusMilitar;
    }

    public void setStatusMilitar(StatusMilitarEnum statusMilitar) {
        this.statusMilitar = statusMilitar;
    }

    public ForcaEnum getForca() {
        return forca;
    }

    public void setForca(ForcaEnum forca) {
        this.forca = forca;
    }

    public String getNrAntiguidade() {
        return nrAntiguidade;
    }

    public void setNrAntiguidade(String nrAntiguidade) {
        this.nrAntiguidade = nrAntiguidade;
    }

    public LocalDate getUltimaPromocao() {
        return ultimaPromocao;
    }

    public void setUltimaPromocao(LocalDate ultimaPromocao) {
        this.ultimaPromocao = ultimaPromocao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PostoDTO getPosto() {
        return posto;
    }

    public void setPosto(PostoDTO posto) {
        this.posto = posto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MilitarDTO)) {
            return false;
        }

        MilitarDTO militarDTO = (MilitarDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, militarDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MilitarDTO{" +
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
            ", posto=" + getPosto() +
            "}";
    }
}
