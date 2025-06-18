package com.ecursos.myapp.domain;

import static com.ecursos.myapp.domain.PostoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Posto.class);
        Posto posto1 = getPostoSample1();
        Posto posto2 = new Posto();
        assertThat(posto1).isNotEqualTo(posto2);

        posto2.setId(posto1.getId());
        assertThat(posto1).isEqualTo(posto2);

        posto2 = getPostoSample2();
        assertThat(posto1).isNotEqualTo(posto2);
    }
}
