package com.ecursos.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoDTO.class);
        TipoDTO tipoDTO1 = new TipoDTO();
        tipoDTO1.setId(1L);
        TipoDTO tipoDTO2 = new TipoDTO();
        assertThat(tipoDTO1).isNotEqualTo(tipoDTO2);
        tipoDTO2.setId(tipoDTO1.getId());
        assertThat(tipoDTO1).isEqualTo(tipoDTO2);
        tipoDTO2.setId(2L);
        assertThat(tipoDTO1).isNotEqualTo(tipoDTO2);
        tipoDTO1.setId(null);
        assertThat(tipoDTO1).isNotEqualTo(tipoDTO2);
    }
}
