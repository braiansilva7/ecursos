package com.ecursos.myapp.service.mapper;

import com.ecursos.myapp.domain.Curso;
import com.ecursos.myapp.domain.Turma;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.service.dto.TurmaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Turma} and its DTO {@link TurmaDTO}.
 */
@Mapper(componentModel = "spring")
public interface TurmaMapper extends EntityMapper<TurmaDTO, Turma> {
    @Mapping(target = "curso", source = "curso", qualifiedByName = "cursoCursoNome")
    TurmaDTO toDto(Turma s);

    @Named("cursoCursoNome")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "cursoNome", source = "cursoNome")
    CursoDTO toDtoCursoCursoNome(Curso curso);
}
