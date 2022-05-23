package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Ad;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Ad} entity.
 */
public interface AdSearchRepository extends ElasticsearchRepository<Ad, Long>, AdSearchRepositoryInternal {}

interface AdSearchRepositoryInternal {
    Stream<Ad> search(String query);
}

class AdSearchRepositoryInternalImpl implements AdSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AdSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Ad> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Ad.class).map(SearchHit::getContent).stream();
    }
}
