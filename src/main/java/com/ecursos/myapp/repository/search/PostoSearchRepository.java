package com.ecursos.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.repository.PostoRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Posto} entity.
 */
public interface PostoSearchRepository extends ElasticsearchRepository<Posto, Long>, PostoSearchRepositoryInternal {}

interface PostoSearchRepositoryInternal {
    Page<Posto> search(String query, Pageable pageable);

    Page<Posto> search(Query query);

    @Async
    void index(Posto entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PostoSearchRepositoryInternalImpl implements PostoSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PostoRepository repository;

    PostoSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PostoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Posto> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Posto> search(Query query) {
        SearchHits<Posto> searchHits = elasticsearchTemplate.search(query, Posto.class);
        List<Posto> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Posto entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Posto.class);
    }
}
