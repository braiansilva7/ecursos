package com.ecursos.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ecursos.myapp.domain.Posto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostoDTO implements Serializable {

    private Long id;

    @NotNull
    private String postoSigla;

    @NotNull
    private String descricao;

    @NotNull
    private Integer prioridade;

    @NotNull
    private Integer orgao;

    private Integer codSigpes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostoSigla() {
        return postoSigla;
    }

    public void setPostoSigla(String postoSigla) {
        this.postoSigla = postoSigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public Integer getOrgao() {
        return orgao;
    }

    public void setOrgao(Integer orgao) {
        this.orgao = orgao;
    }

    public Integer getCodSigpes() {
        return codSigpes;
    }

    public void setCodSigpes(Integer codSigpes) {
        this.codSigpes = codSigpes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostoDTO)) {
            return false;
        }

        PostoDTO postoDTO = (PostoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, postoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostoDTO{" +
            "id=" + getId() +
            ", postoSigla='" + getPostoSigla() + "'" +
            ", descricao='" + getDescricao() + "'" +
            ", prioridade=" + getPrioridade() +
            ", orgao=" + getOrgao() +
            ", codSigpes=" + getCodSigpes() +
            "}";
    }
}
