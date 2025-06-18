package com.ecursos.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.repository.MilitarRepository;
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
 * Spring Data Elasticsearch repository for the {@link Militar} entity.
 */
public interface MilitarSearchRepository extends ElasticsearchRepository<Militar, Long>, MilitarSearchRepositoryInternal {}

interface MilitarSearchRepositoryInternal {
    Page<Militar> search(String query, Pageable pageable);

    Page<Militar> search(Query query);

    @Async
    void index(Militar entity);

    @Async
    void deleteFromIndexById(Long id);
}

class MilitarSearchRepositoryInternalImpl implements MilitarSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final MilitarRepository repository;

    MilitarSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, MilitarRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Militar> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Militar> search(Query query) {
        SearchHits<Militar> searchHits = elasticsearchTemplate.search(query, Militar.class);
        List<Militar> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Militar entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Militar.class);
    }
}
