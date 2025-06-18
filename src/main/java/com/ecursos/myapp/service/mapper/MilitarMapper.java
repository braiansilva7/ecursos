package com.ecursos.myapp.service.mapper;

import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.service.dto.MilitarDTO;
import com.ecursos.myapp.service.dto.PostoDTO;
import org.mapstruct.*;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Militar} and its DTO {@link MilitarDTO}.
 */
@Mapper(componentModel = "spring", uses = {PostoMapper.class})
public interface MilitarMapper extends EntityMapper<MilitarDTO, Militar> {
    @Mapping(target = "saram", source = "saram") // Use o PostoMapper para preencher todos os detalhes do posto
    @Mapping(target = "posto", source = "posto") // Use o PostoMapper para preencher todos os detalhes do posto
    MilitarDTO toDto(Militar militar);

    List<Militar> toEntityList(List<MilitarDTO> militarDTOs); // Adicione este método

}
