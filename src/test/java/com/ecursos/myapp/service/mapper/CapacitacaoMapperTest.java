package com.ecursos.myapp.service.mapper;

import static com.ecursos.myapp.domain.CapacitacaoAsserts.*;
import static com.ecursos.myapp.domain.CapacitacaoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CapacitacaoMapperTest {

    private CapacitacaoMapper capacitacaoMapper;

    @BeforeEach
    void setUp() {
        capacitacaoMapper = new CapacitacaoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCapacitacaoSample1();
        var actual = capacitacaoMapper.toEntity(capacitacaoMapper.toDto(expected));
        assertCapacitacaoAllPropertiesEquals(expected, actual);
    }
}
