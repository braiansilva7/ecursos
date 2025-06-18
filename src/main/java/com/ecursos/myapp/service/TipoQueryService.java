package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.*; // for static metamodels
import com.ecursos.myapp.domain.Tipo;
import com.ecursos.myapp.repository.TipoRepository;
import com.ecursos.myapp.repository.search.TipoSearchRepository;
import com.ecursos.myapp.service.criteria.TipoCriteria;
import com.ecursos.myapp.service.dto.TipoDTO;
import com.ecursos.myapp.service.mapper.TipoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Tipo} entities in the database.
 * The main input is a {@link TipoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TipoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TipoQueryService extends QueryService<Tipo> {

    private static final Logger LOG = LoggerFactory.getLogger(TipoQueryService.class);

    private final TipoRepository tipoRepository;

    private final TipoMapper tipoMapper;

    private final TipoSearchRepository tipoSearchRepository;

    public TipoQueryService(TipoRepository tipoRepository, TipoMapper tipoMapper, TipoSearchRepository tipoSearchRepository) {
        this.tipoRepository = tipoRepository;
        this.tipoMapper = tipoMapper;
        this.tipoSearchRepository = tipoSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TipoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TipoDTO> findByCriteria(TipoCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tipo> specification = createSpecification(criteria);
        return tipoRepository.findAll(specification, page).map(tipoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TipoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Tipo> specification = createSpecification(criteria);
        return tipoRepository.count(specification);
    }

    /**
     * Function to convert {@link TipoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Tipo> createSpecification(TipoCriteria criteria) {
        Specification<Tipo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Tipo_.id));
            }
            if (criteria.getCategoria() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCategoria(), Tipo_.categoria));
            }
        }
        return specification;
    }
}
