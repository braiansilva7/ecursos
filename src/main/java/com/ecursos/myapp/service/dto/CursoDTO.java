package com.ecursos.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ecursos.myapp.domain.Curso} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CursoDTO implements Serializable {

    private Long id;

    @NotNull
    private String cursoNome;

    @NotNull
    private String cursoSigla;

    @NotNull
    private String empresa;

    @NotNull
    private TipoDTO tipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public String getCursoSigla() {
        return cursoSigla;
    }

    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public TipoDTO getTipo() {
        return tipo;
    }

    public void setTipo(TipoDTO tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CursoDTO)) {
            return false;
        }

        CursoDTO cursoDTO = (CursoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cursoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CursoDTO{" +
            "id=" + getId() +
            ", cursoNome='" + getCursoNome() + "'" +
            ", cursoSigla='" + getCursoSigla() + "'" +
            ", empresa='" + getEmpresa() + "'" +
            ", tipo=" + getTipo() +
            "}";
    }
}
