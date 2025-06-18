package com.ecursos.myapp.service.mapper;

import com.ecursos.myapp.domain.Curso;
import com.ecursos.myapp.domain.Tipo;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.service.dto.TipoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Curso} and its DTO {@link CursoDTO}.
 */
@Mapper(componentModel = "spring")
public interface CursoMapper extends EntityMapper<CursoDTO, Curso> {
    @Mapping(target = "tipo", source = "tipo", qualifiedByName = "tipoCategoria")
    CursoDTO toDto(Curso s);

    @Named("tipoCategoria")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoria", source = "categoria")
    TipoDTO toDtoTipoCategoria(Tipo tipo);
}
