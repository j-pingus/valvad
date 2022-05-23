package com.valvad.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.valvad.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CompatibilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Compatibility.class);
        Compatibility compatibility1 = new Compatibility();
        compatibility1.setId(1L);
        Compatibility compatibility2 = new Compatibility();
        compatibility2.setId(compatibility1.getId());
        assertThat(compatibility1).isEqualTo(compatibility2);
        compatibility2.setId(2L);
        assertThat(compatibility1).isNotEqualTo(compatibility2);
        compatibility1.setId(null);
        assertThat(compatibility1).isNotEqualTo(compatibility2);
    }
}
