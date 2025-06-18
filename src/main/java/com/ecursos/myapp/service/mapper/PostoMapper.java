package com.ecursos.myapp.service.mapper;

import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.service.dto.PostoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Posto} and its DTO {@link PostoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostoMapper extends EntityMapper<PostoDTO, Posto> {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "postoSigla", source = "postoSigla")
    @Mapping(target = "descricao", source = "descricao")
    @Mapping(target = "prioridade", source = "prioridade")
    @Mapping(target = "orgao", source = "orgao")
    @Mapping(target = "codSigpes", source = "codSigpes")
    PostoDTO toDto(Posto posto);
}
