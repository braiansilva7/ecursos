package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.*; // for static metamodels
import com.ecursos.myapp.domain.Curso;
import com.ecursos.myapp.repository.CursoRepository;
import com.ecursos.myapp.repository.search.CursoSearchRepository;
import com.ecursos.myapp.service.criteria.CursoCriteria;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.service.mapper.CursoMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Service for executing complex queries for {@link Curso} entities in the database.
 * The main input is a {@link CursoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CursoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CursoQueryService extends QueryService<Curso> {

    private static final Logger LOG = LoggerFactory.getLogger(CursoQueryService.class);

    private final CursoRepository cursoRepository;

    private final CursoMapper cursoMapper;

    private final CursoSearchRepository cursoSearchRepository;

    public CursoQueryService(CursoRepository cursoRepository, CursoMapper cursoMapper, CursoSearchRepository cursoSearchRepository) {
        this.cursoRepository = cursoRepository;
        this.cursoMapper = cursoMapper;
        this.cursoSearchRepository = cursoSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CursoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CursoDTO> findByCriteria(CursoCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Curso> specification = createSpecification(criteria);
        return cursoRepository.findAll(specification, page).map(cursoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<CursoDTO> findByCriteriaAll(CursoCriteria criteria) {
        final Specification<Curso> specification = createSpecification(criteria);
        // Ordenar por nomeGuerra em ordem ascendente
        Sort sort = Sort.by(Sort.Direction.ASC, "cursoNome");
        return cursoRepository.findAll(specification, sort).stream()
                .map(cursoMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CursoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Curso> specification = createSpecification(criteria);
        return cursoRepository.count(specification);
    }

    /**
     * Function to convert {@link CursoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Curso> createSpecification(CursoCriteria criteria) {
        Specification<Curso> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Curso_.id));
            }
            if (criteria.getCursoNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCursoNome(), Curso_.cursoNome));
            }
            if (criteria.getCursoSigla() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCursoSigla(), Curso_.cursoSigla));
            }
            if (criteria.getEmpresa() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmpresa(), Curso_.empresa));
            }
            if (criteria.getTipoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTipoId(), root -> root.join(Curso_.tipo, JoinType.LEFT).get(Tipo_.id))
                );
            }
        }
        return specification;
    }
}
