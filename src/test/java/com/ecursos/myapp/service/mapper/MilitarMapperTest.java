package com.ecursos.myapp.service.mapper;

import static com.ecursos.myapp.domain.MilitarAsserts.*;
import static com.ecursos.myapp.domain.MilitarTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MilitarMapperTest {

    private MilitarMapper militarMapper;

    @BeforeEach
    void setUp() {
        militarMapper = new MilitarMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMilitarSample1();
        var actual = militarMapper.toEntity(militarMapper.toDto(expected));
        assertMilitarAllPropertiesEquals(expected, actual);
    }
}
