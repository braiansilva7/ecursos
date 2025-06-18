package com.ecursos.myapp.service.mapper;

import static com.ecursos.myapp.domain.PostoAsserts.*;
import static com.ecursos.myapp.domain.PostoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostoMapperTest {

    private PostoMapper postoMapper;

    @BeforeEach
    void setUp() {
        postoMapper = new PostoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPostoSample1();
        var actual = postoMapper.toEntity(postoMapper.toDto(expected));
        assertPostoAllPropertiesEquals(expected, actual);
    }
}
