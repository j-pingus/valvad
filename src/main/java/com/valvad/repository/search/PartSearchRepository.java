package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Part;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Part} entity.
 */
public interface PartSearchRepository extends ElasticsearchRepository<Part, Long>, PartSearchRepositoryInternal {}

interface PartSearchRepositoryInternal {
    Stream<Part> search(String query);
}

class PartSearchRepositoryInternalImpl implements PartSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    PartSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Part> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Part.class).map(SearchHit::getContent).stream();
    }
}
