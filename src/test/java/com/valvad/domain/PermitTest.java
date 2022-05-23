package com.valvad.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.valvad.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PermitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Permit.class);
        Permit permit1 = new Permit();
        permit1.setId(1L);
        Permit permit2 = new Permit();
        permit2.setId(permit1.getId());
        assertThat(permit1).isEqualTo(permit2);
        permit2.setId(2L);
        assertThat(permit1).isNotEqualTo(permit2);
        permit1.setId(null);
        assertThat(permit1).isNotEqualTo(permit2);
    }
}
