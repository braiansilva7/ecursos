package com.ecursos.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CapacitacaoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapacitacaoDTO.class);
        CapacitacaoDTO capacitacaoDTO1 = new CapacitacaoDTO();
        capacitacaoDTO1.setId(1L);
        CapacitacaoDTO capacitacaoDTO2 = new CapacitacaoDTO();
        assertThat(capacitacaoDTO1).isNotEqualTo(capacitacaoDTO2);
        capacitacaoDTO2.setId(capacitacaoDTO1.getId());
        assertThat(capacitacaoDTO1).isEqualTo(capacitacaoDTO2);
        capacitacaoDTO2.setId(2L);
        assertThat(capacitacaoDTO1).isNotEqualTo(capacitacaoDTO2);
        capacitacaoDTO1.setId(null);
        assertThat(capacitacaoDTO1).isNotEqualTo(capacitacaoDTO2);
    }
}
