package com.valvad.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AdSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AdSearchRepositoryMockConfiguration {

    @MockBean
    private AdSearchRepository mockAdSearchRepository;
}
