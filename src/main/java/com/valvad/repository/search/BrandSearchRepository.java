package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Brand;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Brand} entity.
 */
public interface BrandSearchRepository extends ElasticsearchRepository<Brand, Long>, BrandSearchRepositoryInternal {}

interface BrandSearchRepositoryInternal {
    Stream<Brand> search(String query);
}

class BrandSearchRepositoryInternalImpl implements BrandSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    BrandSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Brand> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Brand.class).map(SearchHit::getContent).stream();
    }
}
