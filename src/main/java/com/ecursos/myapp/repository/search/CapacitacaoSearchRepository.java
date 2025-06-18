package com.ecursos.myapp.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.ecursos.myapp.domain.Capacitacao;
import com.ecursos.myapp.repository.CapacitacaoRepository;
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
 * Spring Data Elasticsearch repository for the {@link Capacitacao} entity.
 */
public interface CapacitacaoSearchRepository extends ElasticsearchRepository<Capacitacao, Long>, CapacitacaoSearchRepositoryInternal {}

interface CapacitacaoSearchRepositoryInternal {
    Page<Capacitacao> search(String query, Pageable pageable);

    Page<Capacitacao> search(Query query);

    @Async
    void index(Capacitacao entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CapacitacaoSearchRepositoryInternalImpl implements CapacitacaoSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CapacitacaoRepository repository;

    CapacitacaoSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CapacitacaoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Capacitacao> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Capacitacao> search(Query query) {
        SearchHits<Capacitacao> searchHits = elasticsearchTemplate.search(query, Capacitacao.class);
        List<Capacitacao> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Capacitacao entity) {
        //repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
        deleteFromIndexById(entity.getId());
        
        repository.findOneWithEagerRelationships(entity.getId())
        .ifPresent(capacitacaoCompleta -> {
            elasticsearchTemplate.save(capacitacaoCompleta);
        });
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Capacitacao.class);
    }
}
