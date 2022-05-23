package com.valvad.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CompatibilitySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CompatibilitySearchRepositoryMockConfiguration {

    @MockBean
    private CompatibilitySearchRepository mockCompatibilitySearchRepository;
}
