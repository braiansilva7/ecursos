package com.ecursos.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ecursos.myapp.domain.Tipo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TipoDTO implements Serializable {

    private Long id;

    @NotNull
    private String categoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoDTO)) {
            return false;
        }

        TipoDTO tipoDTO = (TipoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tipoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TipoDTO{" +
            "id=" + getId() +
            ", categoria='" + getCategoria() + "'" +
            "}";
    }
}
