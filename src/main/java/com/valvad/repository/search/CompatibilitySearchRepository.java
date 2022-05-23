package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Compatibility;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Compatibility} entity.
 */
public interface CompatibilitySearchRepository
    extends ElasticsearchRepository<Compatibility, Long>, CompatibilitySearchRepositoryInternal {}

interface CompatibilitySearchRepositoryInternal {
    Stream<Compatibility> search(String query);
}

class CompatibilitySearchRepositoryInternalImpl implements CompatibilitySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    CompatibilitySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Compatibility> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Compatibility.class).map(SearchHit::getContent).stream();
    }
}
