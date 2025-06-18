package com.ecursos.myapp.domain;

import static com.ecursos.myapp.domain.CursoTestSamples.*;
import static com.ecursos.myapp.domain.TipoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CursoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Curso.class);
        Curso curso1 = getCursoSample1();
        Curso curso2 = new Curso();
        assertThat(curso1).isNotEqualTo(curso2);

        curso2.setId(curso1.getId());
        assertThat(curso1).isEqualTo(curso2);

        curso2 = getCursoSample2();
        assertThat(curso1).isNotEqualTo(curso2);
    }

    @Test
    void tipoTest() {
        Curso curso = getCursoRandomSampleGenerator();
        Tipo tipoBack = getTipoRandomSampleGenerator();

        curso.setTipo(tipoBack);
        assertThat(curso.getTipo()).isEqualTo(tipoBack);

        curso.tipo(null);
        assertThat(curso.getTipo()).isNull();
    }
}
