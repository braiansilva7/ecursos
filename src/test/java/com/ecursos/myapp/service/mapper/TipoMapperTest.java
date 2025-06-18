package com.ecursos.myapp.service.mapper;

import static com.ecursos.myapp.domain.TipoAsserts.*;
import static com.ecursos.myapp.domain.TipoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TipoMapperTest {

    private TipoMapper tipoMapper;

    @BeforeEach
    void setUp() {
        tipoMapper = new TipoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTipoSample1();
        var actual = tipoMapper.toEntity(tipoMapper.toDto(expected));
        assertTipoAllPropertiesEquals(expected, actual);
    }
}
