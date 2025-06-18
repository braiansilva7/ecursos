package com.ecursos.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostoDTO.class);
        PostoDTO postoDTO1 = new PostoDTO();
        postoDTO1.setId(1L);
        PostoDTO postoDTO2 = new PostoDTO();
        assertThat(postoDTO1).isNotEqualTo(postoDTO2);
        postoDTO2.setId(postoDTO1.getId());
        assertThat(postoDTO1).isEqualTo(postoDTO2);
        postoDTO2.setId(2L);
        assertThat(postoDTO1).isNotEqualTo(postoDTO2);
        postoDTO1.setId(null);
        assertThat(postoDTO1).isNotEqualTo(postoDTO2);
    }
}
