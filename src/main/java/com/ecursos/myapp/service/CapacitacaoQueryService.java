package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.*; // for static metamodels
import com.ecursos.myapp.domain.Capacitacao;
import com.ecursos.myapp.repository.CapacitacaoRepository;
import com.ecursos.myapp.repository.search.CapacitacaoSearchRepository;
import com.ecursos.myapp.service.criteria.CapacitacaoCriteria;
import com.ecursos.myapp.service.dto.CapacitacaoDTO;
import com.ecursos.myapp.service.mapper.CapacitacaoMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ecursos.myapp.security.SecurityUtils;
import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * Service for executing complex queries for {@link Capacitacao} entities in the database.
 * The main input is a {@link CapacitacaoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CapacitacaoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CapacitacaoQueryService extends QueryService<Capacitacao> {

    private static final Logger LOG = LoggerFactory.getLogger(CapacitacaoQueryService.class);

    private final CapacitacaoRepository capacitacaoRepository;

    private final CapacitacaoMapper capacitacaoMapper;

    private final CapacitacaoSearchRepository capacitacaoSearchRepository;

    public CapacitacaoQueryService(
        CapacitacaoRepository capacitacaoRepository,
        CapacitacaoMapper capacitacaoMapper,
        CapacitacaoSearchRepository capacitacaoSearchRepository
    ) {
        this.capacitacaoRepository = capacitacaoRepository;
        this.capacitacaoMapper = capacitacaoMapper;
        this.capacitacaoSearchRepository = capacitacaoSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CapacitacaoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CapacitacaoDTO> findByCriteria(CapacitacaoCriteria criteria, Pageable page, Authentication authentication) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        
        String saram = null;

        authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            saram = SecurityUtils.getFabNrOrdem(authentication);
            if (saram != null) {
                LOG.debug("SARAM II : {}", saram);
            }
        }

        final String finalSaram = saram; // Torna saram effectively final

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Encontrar a role específica
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority) // Extrai o nome da role
                .filter("ROLE_CCABR_ELO_EXECUTIVO_AVANCADO"::equals) // Filtra pela role exata
                .findFirst() // Pega o primeiro resultado encontrado (ou o único)
                .orElse(null); // Se não encontrar, retorna null

        LOG.debug("ROLE ELE EXECUTIVO : {}", role);

        if (finalSaram != null && role == null) {
            // Criar uma especificação para buscar os imóveis relacionados à sigla
            final Specification<Capacitacao> specification = (root, query, criteriaBuilder) ->
                finalSaram != null ? criteriaBuilder.equal(root.get("militar").get("saram"), finalSaram) : criteriaBuilder.conjunction();

            return capacitacaoRepository.findAll(specification, page).map(capacitacaoMapper::toDto);
        }

        final Specification<Capacitacao> specification = createSpecification(criteria);
        return capacitacaoRepository.findAll(specification, page).map(capacitacaoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<CapacitacaoDTO> findByCriteriaAll(CapacitacaoCriteria criteria, Authentication authentication) {
        LOG.debug("find by criteria without pagination : {}", criteria);
       
        // Obter saram de uma forma que garanta que seja final
        final String saram = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(SecurityUtils::getFabNrOrdem)
            .orElse(null);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Encontrar a role específica
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority) // Extrai o nome da role
                .filter("ROLE_CCABR_ELO_EXECUTIVO_AVANCADO"::equals) // Filtra pela role exata
                .findFirst() // Pega o primeiro resultado encontrado (ou o único)
                .orElse(null); // Se não encontrar, retorna null

        LOG.debug("ROLE ELO EXECUTIVO FIND ALL : {}", role);
        
        if (saram != null && role == null) {
            LOG.debug("SARAM : {}", saram);
            final Specification<Capacitacao> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("militar").get("saram"), saram);

            return capacitacaoRepository.findAll(specification).stream()
                .map(capacitacaoMapper::toDto)
                .collect(Collectors.toList());
        }


        final Specification<Capacitacao> specification = createSpecification(criteria);
        // Remover a ordenação e listar todos
        return capacitacaoRepository.findAll(specification).stream()
                .map(capacitacaoMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CapacitacaoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Capacitacao> specification = createSpecification(criteria);
        return capacitacaoRepository.count(specification);
    }


    /**
     * Function to convert {@link CapacitacaoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Capacitacao> createSpecification(CapacitacaoCriteria criteria) {
        Specification<Capacitacao> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Capacitacao_.id));
            }
            if (criteria.getCapacitacaoStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getCapacitacaoStatus(), Capacitacao_.capacitacaoStatus));
            }
            if (criteria.getSigpes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSigpes(), Capacitacao_.sigpes));
            }
            if (criteria.getMilitarId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getMilitarId(), root -> root.join(Capacitacao_.militar, JoinType.LEFT).get(Militar_.id))
                );
            }
            if (criteria.getTurmaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTurmaId(), root -> root.join(Capacitacao_.turma, JoinType.LEFT).get(Turma_.id))
                );
            }
        }
        return specification;
    }
}
