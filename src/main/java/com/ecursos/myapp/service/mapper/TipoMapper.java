package com.ecursos.myapp.service.mapper;

import com.ecursos.myapp.domain.Tipo;
import com.ecursos.myapp.service.dto.TipoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tipo} and its DTO {@link TipoDTO}.
 */
@Mapper(componentModel = "spring")
public interface TipoMapper extends EntityMapper<TipoDTO, Tipo> {}
