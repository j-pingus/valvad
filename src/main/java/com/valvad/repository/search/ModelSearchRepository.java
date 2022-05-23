package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Model;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Model} entity.
 */
public interface ModelSearchRepository extends ElasticsearchRepository<Model, Long>, ModelSearchRepositoryInternal {}

interface ModelSearchRepositoryInternal {
    Stream<Model> search(String query);
}

class ModelSearchRepositoryInternalImpl implements ModelSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    ModelSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Model> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Model.class).map(SearchHit::getContent).stream();
    }
}
