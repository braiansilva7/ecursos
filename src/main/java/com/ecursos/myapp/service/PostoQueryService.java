package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.*; // for static metamodels
import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.repository.PostoRepository;
import com.ecursos.myapp.repository.search.PostoSearchRepository;
import com.ecursos.myapp.service.criteria.PostoCriteria;
import com.ecursos.myapp.service.dto.PostoDTO;
import com.ecursos.myapp.service.mapper.PostoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import java.util.List;
/**
 * Service for executing complex queries for {@link Posto} entities in the database.
 * The main input is a {@link PostoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PostoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PostoQueryService extends QueryService<Posto> {

    private static final Logger LOG = LoggerFactory.getLogger(PostoQueryService.class);

    private final PostoRepository postoRepository;

    private final PostoMapper postoMapper;

    private final PostoSearchRepository postoSearchRepository;

    public PostoQueryService(PostoRepository postoRepository, PostoMapper postoMapper, PostoSearchRepository postoSearchRepository) {
        this.postoRepository = postoRepository;
        this.postoMapper = postoMapper;
        this.postoSearchRepository = postoSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PostoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PostoDTO> findByCriteria(PostoCriteria criteria) {
        List<Posto> postos = postoRepository.findAll(); // Ajuste conforme sua implementação real
        return postoMapper.toDto(postos); // Mapeia a lista de Posto para PostoDTO
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PostoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Posto> specification = createSpecification(criteria);
        return postoRepository.count(specification);
    }

    /**
     * Function to convert {@link PostoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Posto> createSpecification(PostoCriteria criteria) {
        Specification<Posto> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Posto_.id));
            }
            if (criteria.getPostoSigla() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPostoSigla(), Posto_.postoSigla));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), Posto_.descricao));
            }
            if (criteria.getPrioridade() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrioridade(), Posto_.prioridade));
            }
            if (criteria.getOrgao() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrgao(), Posto_.orgao));
            }
            if (criteria.getCodSigpes() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCodSigpes(), Posto_.codSigpes));
            }
        }
        return specification;
    }
}
