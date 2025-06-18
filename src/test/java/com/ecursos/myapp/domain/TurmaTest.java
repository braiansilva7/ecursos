package com.ecursos.myapp.domain;

import static com.ecursos.myapp.domain.CursoTestSamples.*;
import static com.ecursos.myapp.domain.TurmaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TurmaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Turma.class);
        Turma turma1 = getTurmaSample1();
        Turma turma2 = new Turma();
        assertThat(turma1).isNotEqualTo(turma2);

        turma2.setId(turma1.getId());
        assertThat(turma1).isEqualTo(turma2);

        turma2 = getTurmaSample2();
        assertThat(turma1).isNotEqualTo(turma2);
    }

    @Test
    void cursoTest() {
        Turma turma = getTurmaRandomSampleGenerator();
        Curso cursoBack = getCursoRandomSampleGenerator();

        turma.setCurso(cursoBack);
        assertThat(turma.getCurso()).isEqualTo(cursoBack);

        turma.curso(null);
        assertThat(turma.getCurso()).isNull();
    }
}
