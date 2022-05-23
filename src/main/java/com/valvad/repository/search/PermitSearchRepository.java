package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Permit;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Permit} entity.
 */
public interface PermitSearchRepository extends ElasticsearchRepository<Permit, Long>, PermitSearchRepositoryInternal {}

interface PermitSearchRepositoryInternal {
    Stream<Permit> search(String query);
}

class PermitSearchRepositoryInternalImpl implements PermitSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    PermitSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Permit> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Permit.class).map(SearchHit::getContent).stream();
    }
}
