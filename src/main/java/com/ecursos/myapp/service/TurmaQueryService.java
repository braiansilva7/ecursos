package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.*; // for static metamodels
import com.ecursos.myapp.domain.Turma;
import com.ecursos.myapp.repository.TurmaRepository;
import com.ecursos.myapp.repository.search.TurmaSearchRepository;
import com.ecursos.myapp.service.criteria.TurmaCriteria;
import com.ecursos.myapp.service.dto.TurmaDTO;
import com.ecursos.myapp.service.mapper.TurmaMapper;
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
 * Service for executing complex queries for {@link Turma} entities in the database.
 * The main input is a {@link TurmaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TurmaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TurmaQueryService extends QueryService<Turma> {

    private static final Logger LOG = LoggerFactory.getLogger(TurmaQueryService.class);

    private final TurmaRepository turmaRepository;

    private final TurmaMapper turmaMapper;

    private final TurmaSearchRepository turmaSearchRepository;

    public TurmaQueryService(TurmaRepository turmaRepository, TurmaMapper turmaMapper, TurmaSearchRepository turmaSearchRepository) {
        this.turmaRepository = turmaRepository;
        this.turmaMapper = turmaMapper;
        this.turmaSearchRepository = turmaSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TurmaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TurmaDTO> findByCriteria(TurmaCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Turma> specification = createSpecification(criteria);
        return turmaRepository.findAll(specification, page).map(turmaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<TurmaDTO> findByCriteriaAll(TurmaCriteria criteria) {
        final Specification<Turma> specification = createSpecification(criteria);
        // Ordenar por nomeGuerra em ordem ascendente
        Sort sort = Sort.by(Sort.Direction.ASC, "curso");
        return turmaRepository.findAll(specification, sort).stream()
                .map(turmaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TurmaCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Turma> specification = createSpecification(criteria);
        return turmaRepository.count(specification);
    }

    /**
     * Function to convert {@link TurmaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Turma> createSpecification(TurmaCriteria criteria) {
        Specification<Turma> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Turma_.id));
            }
            if (criteria.getInicio() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getInicio(), Turma_.inicio));
            }
            if (criteria.getTermino() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTermino(), Turma_.termino));
            }
            if (criteria.getAno() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAno(), Turma_.ano));
            }
            if (criteria.getStatusCurso() != null) {
                specification = specification.and(buildSpecification(criteria.getStatusCurso(), Turma_.statusCurso));
            }
            if (criteria.getModalidade() != null) {
                specification = specification.and(buildSpecification(criteria.getModalidade(), Turma_.modalidade));
            }
            if (criteria.getQtdVagas() != null) {
                specification = specification.and(buildStringSpecification(criteria.getQtdVagas(), Turma_.qtdVagas));
            }
            if (criteria.getNumeroBca() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNumeroBca(), Turma_.numeroBca));
            }
            if (criteria.getCursoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCursoId(), root -> root.join(Turma_.curso, JoinType.LEFT).get(Curso_.id))
                );
            }
        }
        return specification;
    }
}
