package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.*; // for static metamodels
import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.repository.MilitarRepository;
import com.ecursos.myapp.repository.search.MilitarSearchRepository;
import com.ecursos.myapp.service.criteria.MilitarCriteria;
import com.ecursos.myapp.service.dto.MilitarDTO;
import com.ecursos.myapp.service.mapper.MilitarMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for executing complex queries for {@link Militar} entities in the database.
 * The main input is a {@link MilitarCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MilitarDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MilitarQueryService extends QueryService<Militar> {

    private static final Logger LOG = LoggerFactory.getLogger(MilitarQueryService.class);

    private final MilitarRepository militarRepository;

    private final MilitarMapper militarMapper;

    private final MilitarSearchRepository militarSearchRepository;

    public MilitarQueryService(
        MilitarRepository militarRepository,
        MilitarMapper militarMapper,
        MilitarSearchRepository militarSearchRepository
    ) {
        this.militarRepository = militarRepository;
        this.militarMapper = militarMapper;
        this.militarSearchRepository = militarSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link MilitarDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MilitarDTO> findByCriteria(MilitarCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Militar> specification = createSpecification(criteria);

        // Define sorting order:
        // 1. First by orgão (organization)
        // 2. Then by posto priority within the orgão
        // 3. Then by status (ATIVA before INATIVO)
        // 4. Then by promotion date (oldest first)
        // 5. Finally by antiguidade number
        Sort sort = Sort.by("posto.orgao").ascending()
                .and(Sort.by("posto.prioridade").ascending())
                .and(Sort.by("statusMilitar").ascending())  // ATIVA virá antes de INATIVO alfabeticamente
                .and(Sort.by("ultimaPromocao").ascending())
                .and(Sort.by("nrAntiguidade").ascending());

        // Criar um novo Pageable com a ordenação correta
        Pageable effectivePageable = page.isPaged() 
            ? PageRequest.of(page.getPageNumber(), page.getPageSize(), sort)
            : PageRequest.of(0, Integer.MAX_VALUE, sort);

        return militarRepository.findAll(specification, effectivePageable).map(militarMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<MilitarDTO> findByCriteriaAll(MilitarCriteria criteria) {
        LOG.debug("find by criteria without pagination : {}", criteria);
        final Specification<Militar> specification = createSpecification(criteria);

        // Ordenar por nomeCompleto em ordem ascendente
        Sort sort = Sort.by(Sort.Direction.ASC, "nomeCompleto");
        
        return militarRepository.findAll(specification, sort).stream()
                .map(militarMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MilitarCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Militar> specification = createSpecification(criteria);
        return militarRepository.count(specification);
    }

    /**
     * Function to convert {@link MilitarCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Militar> createSpecification(MilitarCriteria criteria) {
        Specification<Militar> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Militar_.id));
            }
            if (criteria.getSaram() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSaram(), Militar_.saram));
            }
            if (criteria.getNomeCompleto() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomeCompleto(), Militar_.nomeCompleto));
            }
            if (criteria.getNomeGuerra() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomeGuerra(), Militar_.nomeGuerra));
            }
            if (criteria.getOm() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOm(), Militar_.om));
            }
            if (criteria.getTelefone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTelefone(), Militar_.telefone));
            }
            if (criteria.getStatusMilitar() != null) {
                specification = specification.and(buildSpecification(criteria.getStatusMilitar(), Militar_.statusMilitar));
            }
            if (criteria.getForca() != null) {
                specification = specification.and(buildSpecification(criteria.getForca(), Militar_.forca));
            }
            if (criteria.getNrAntiguidade() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNrAntiguidade(), Militar_.nrAntiguidade));
            }
            if (criteria.getUltimaPromocao() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUltimaPromocao(), Militar_.ultimaPromocao));
            }
            if (criteria.getCpf() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCpf(), Militar_.cpf));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Militar_.email));
            }
            if (criteria.getPostoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPostoId(), root -> root.join(Militar_.posto, JoinType.LEFT).get(Posto_.id))
                );
            }
        }
        return specification;
    }
}
