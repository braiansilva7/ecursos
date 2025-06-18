package com.ecursos.myapp.domain;

import static com.ecursos.myapp.domain.CapacitacaoTestSamples.*;
import static com.ecursos.myapp.domain.MilitarTestSamples.*;
import static com.ecursos.myapp.domain.TurmaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CapacitacaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Capacitacao.class);
        Capacitacao capacitacao1 = getCapacitacaoSample1();
        Capacitacao capacitacao2 = new Capacitacao();
        assertThat(capacitacao1).isNotEqualTo(capacitacao2);

        capacitacao2.setId(capacitacao1.getId());
        assertThat(capacitacao1).isEqualTo(capacitacao2);

        capacitacao2 = getCapacitacaoSample2();
        assertThat(capacitacao1).isNotEqualTo(capacitacao2);
    }

    @Test
    void militarTest() {
        Capacitacao capacitacao = getCapacitacaoRandomSampleGenerator();
        Militar militarBack = getMilitarRandomSampleGenerator();

        capacitacao.setMilitar(militarBack);
        assertThat(capacitacao.getMilitar()).isEqualTo(militarBack);

        capacitacao.militar(null);
        assertThat(capacitacao.getMilitar()).isNull();
    }

    @Test
    void turmaTest() {
        Capacitacao capacitacao = getCapacitacaoRandomSampleGenerator();
        Turma turmaBack = getTurmaRandomSampleGenerator();

        capacitacao.setTurma(turmaBack);
        assertThat(capacitacao.getTurma()).isEqualTo(turmaBack);

        capacitacao.turma(null);
        assertThat(capacitacao.getTurma()).isNull();
    }
}
