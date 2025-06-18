package com.ecursos.myapp.service.mapper;

import static com.ecursos.myapp.domain.CursoAsserts.*;
import static com.ecursos.myapp.domain.CursoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CursoMapperTest {

    private CursoMapper cursoMapper;

    @BeforeEach
    void setUp() {
        cursoMapper = new CursoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCursoSample1();
        var actual = cursoMapper.toEntity(cursoMapper.toDto(expected));
        assertCursoAllPropertiesEquals(expected, actual);
    }
}
