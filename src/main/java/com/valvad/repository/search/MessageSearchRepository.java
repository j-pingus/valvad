package com.valvad.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.valvad.domain.Message;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Message} entity.
 */
public interface MessageSearchRepository extends ElasticsearchRepository<Message, Long>, MessageSearchRepositoryInternal {}

interface MessageSearchRepositoryInternal {
    Stream<Message> search(String query);
}

class MessageSearchRepositoryInternalImpl implements MessageSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    MessageSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Message> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Message.class).map(SearchHit::getContent).stream();
    }
}
