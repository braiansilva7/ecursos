package com.ecursos.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class MilitarApi {

    private Long id;
    private String cd_org;
    private String cd_posto;
    private String nm_guerra;
    private String nm_pessoa;
    private String nr_cpf;
    private String nr_ordem;
    private String pesfis_type;
    private String sg_org;
    private String telefone_cel;
    private String nr_antiguidade;
    private LocalDate ultima_promocao;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrg() {
        return this.cd_org;
    }

    public void setOrg(String cd_org) {
        this.cd_org = cd_org;
    }

    public String getPosto() {
        return this.cd_posto;
    }

    public void setPosto(String cd_posto) {
        this.cd_posto = cd_posto;
    }

    public String getGuerra() {
        return this.nm_guerra;
    }

    public void setGuerra(String nm_guerra) {
        this.nm_guerra = nm_guerra;
    }

    public String getPessoa() {
        return this.nm_pessoa;
    }

    public void setPessoa(String nm_pessoa) {
        this.nm_pessoa = nm_pessoa;
    }

    public String getCpf() { 
        return this.nr_cpf;
    }

    public void setCpf(String nr_cpf) {
        this.nr_cpf = nr_cpf;
    }

    public String getOrdem() {
        return this.nr_ordem;
    }

    public void setOrdem(String nr_ordem) {
        this.nr_ordem = nr_ordem;
    }

    public String getPerfil() {
        return this.pesfis_type;
    }

    public void setPerfil(String pesfis_type) {
        this.pesfis_type = pesfis_type;
    }

    public String getSgOrg() {
        return this.sg_org;
    }

    public void setSgOrg(String sg_org) {
        this.sg_org = sg_org;
    }

    public String getTelefoneCel() {
        return this.telefone_cel;
    }

    public void setTelefoneCel(String telefone_cel) {
        this.telefone_cel = telefone_cel;
    }

    public String getNrAntiguidade() {
        return this.nr_antiguidade;
    }

    public void setNrAntiguidade(String nr_antiguidade) {
        this.nr_antiguidade = nr_antiguidade;
    }

    public LocalDate getUltimaPromocao() {
        return this.ultima_promocao;
    }

    public void setUltimaPromocao(LocalDate ultima_promocao) {
        this.ultima_promocao = ultima_promocao;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MilitarApi{" +
            ", codigoOrgao='" + getOrg() + "'" +
            ", codigoPosto='" + getPosto() + "'" +
            ", nomeGuerra='" + getGuerra() + "'" +
            ", nomePessoa='" + getPessoa() + "'" +
            ", cpf='" + getCpf() + "'" +
            ", saram='" + getOrdem() + "'" +
            ", antiguidade='" + getNrAntiguidade() + "'" +
            ", ultimaPromocao='" + getUltimaPromocao() + "'" +
            ", email='" + getEmail() + "'" +
            ", Perfil='" + getPerfil() + "'" +
            ", TelefoneCel='" + getTelefoneCel() + "'" +
            ", om='" + getSgOrg() + "'" +
            "}";
    }

   
}
