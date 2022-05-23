package com.valvad.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.valvad.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ad.class);
        Ad ad1 = new Ad();
        ad1.setId(1L);
        Ad ad2 = new Ad();
        ad2.setId(ad1.getId());
        assertThat(ad1).isEqualTo(ad2);
        ad2.setId(2L);
        assertThat(ad1).isNotEqualTo(ad2);
        ad1.setId(null);
        assertThat(ad1).isNotEqualTo(ad2);
    }
}
