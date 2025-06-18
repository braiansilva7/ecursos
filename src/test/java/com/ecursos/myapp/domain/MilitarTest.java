package com.ecursos.myapp.domain;

import static com.ecursos.myapp.domain.MilitarTestSamples.*;
import static com.ecursos.myapp.domain.PostoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ecursos.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MilitarTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Militar.class);
        Militar militar1 = getMilitarSample1();
        Militar militar2 = new Militar();
        assertThat(militar1).isNotEqualTo(militar2);

        militar2.setId(militar1.getId());
        assertThat(militar1).isEqualTo(militar2);

        militar2 = getMilitarSample2();
        assertThat(militar1).isNotEqualTo(militar2);
    }

    @Test
    void postoTest() {
        Militar militar = getMilitarRandomSampleGenerator();
        Posto postoBack = getPostoRandomSampleGenerator();

        militar.setPosto(postoBack);
        assertThat(militar.getPosto()).isEqualTo(postoBack);

        militar.posto(null);
        assertThat(militar.getPosto()).isNull();
    }
}
