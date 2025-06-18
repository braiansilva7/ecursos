package com.ecursos.myapp.service.mapper;

import com.ecursos.myapp.domain.Capacitacao;
import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.domain.Turma;
import com.ecursos.myapp.service.dto.CapacitacaoDTO;
import com.ecursos.myapp.service.dto.MilitarDTO;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.service.dto.TurmaDTO;
import com.ecursos.myapp.service.dto.TipoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Capacitacao} and its DTO {@link CapacitacaoDTO}.
 */
@Mapper(componentModel = "spring", uses = {MilitarMapper.class, PostoMapper.class, CursoMapper.class, TipoMapper.class, TurmaMapper.class})
public interface CapacitacaoMapper extends EntityMapper<CapacitacaoDTO, Capacitacao> {
    @Mapping(target = "militar", source = "militar", qualifiedByName = "militarAll")
    @Mapping(target = "turma", source = "turma", qualifiedByName = "turmaId")
    @Mapping(target = "capacitacaoStatus", source = "capacitacaoStatus")
    @Mapping(target = "sigpes", source = "sigpes")
    CapacitacaoDTO toDto(Capacitacao s);

    @Named("militarAll")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomeCompleto", source = "nomeCompleto")
    @Mapping(target = "nomeGuerra", source = "nomeGuerra")
    @Mapping(target = "om", source = "om")
    @Mapping(target = "saram", source = "saram")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "posto", source = "posto")
    MilitarDTO toDtoMilitarNomeCompleto(Militar militar);

    @Named("turmaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "curso", source = "curso")
    @Mapping(target = "inicio", source = "inicio")
    @Mapping(target = "termino", source = "termino")
    @Mapping(target = "ano", source = "ano")
    @Mapping(target = "modalidade", source = "modalidade")
    @Mapping(target = "statusCurso", source = "statusCurso")
    @Mapping(target = "numeroBca", source = "numeroBca")
    @Mapping(target = "qtdVagas", source = "qtdVagas")
    TurmaDTO toDtoTurmaId(Turma turma);
}
