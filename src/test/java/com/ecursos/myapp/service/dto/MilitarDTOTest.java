package com.ecursos.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MilitarDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MilitarDTO.class);
        MilitarDTO militarDTO1 = new MilitarDTO();
        militarDTO1.setId(1L);
        MilitarDTO militarDTO2 = new MilitarDTO();
        assertThat(militarDTO1).isNotEqualTo(militarDTO2);
        militarDTO2.setId(militarDTO1.getId());
        assertThat(militarDTO1).isEqualTo(militarDTO2);
        militarDTO2.setId(2L);
        assertThat(militarDTO1).isNotEqualTo(militarDTO2);
        militarDTO1.setId(null);
        assertThat(militarDTO1).isNotEqualTo(militarDTO2);
    }
}
