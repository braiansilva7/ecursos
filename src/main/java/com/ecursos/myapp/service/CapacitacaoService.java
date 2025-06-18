package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.Capacitacao;
import com.ecursos.myapp.repository.CapacitacaoRepository;
import com.ecursos.myapp.repository.search.CapacitacaoSearchRepository;
import com.ecursos.myapp.service.dto.CapacitacaoDTO;
import com.ecursos.myapp.service.mapper.CapacitacaoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import jakarta.annotation.PostConstruct;
import com.ecursos.myapp.web.rest.errors.BadRequestAlertException;

/**
 * Service Implementation for managing {@link com.ecursos.myapp.domain.Capacitacao}.
 */
@Service
@Transactional
public class CapacitacaoService {

    private static final Logger LOG = LoggerFactory.getLogger(CapacitacaoService.class);

    private static final String ENTITY_NAME = "capacitacao Service";

    private final CapacitacaoRepository capacitacaoRepository;

    private final CapacitacaoMapper capacitacaoMapper;

    private final CapacitacaoSearchRepository capacitacaoSearchRepository;

    public CapacitacaoService(
        CapacitacaoRepository capacitacaoRepository,
        CapacitacaoMapper capacitacaoMapper,
        CapacitacaoSearchRepository capacitacaoSearchRepository
    ) {
        this.capacitacaoRepository = capacitacaoRepository;
        this.capacitacaoMapper = capacitacaoMapper;
        this.capacitacaoSearchRepository = capacitacaoSearchRepository;
    }

    @PostConstruct
    public void reindexAllCapacitacao() {
        capacitacaoSearchRepository.deleteAll();
        
        List<Capacitacao> allCapacitacao = capacitacaoRepository.findAllWithEagerRelationships();

        capacitacaoSearchRepository.saveAll(allCapacitacao);
        
        LOG.debug("Reindexação completa de todos as CAPACITAÇÕES no Elasticsearch");
    }

    /**
     * Save a capacitacao.
     *
     * @param capacitacaoDTO the entity to save.
     * @return the persisted entity.
     */
    public CapacitacaoDTO save(CapacitacaoDTO capacitacaoDTO) {

        Long militarId = capacitacaoDTO.getMilitar().getId();
        Long turmaId   = capacitacaoDTO.getTurma().getId();
        if (capacitacaoRepository.existsByMilitarIdAndTurmaId(militarId, turmaId)) {
            String nomeGuerra = capacitacaoDTO.getMilitar().getNomeGuerra();
            String mensagem   = " O Militar " + nomeGuerra + " já está inscrito nesta turma";
    
            throw new BadRequestAlertException(
                mensagem,        
                ENTITY_NAME,
                mensagem         
            );
        }

        LOG.debug("Request to save Capacitacao : {}", capacitacaoDTO);
        Capacitacao capacitacao = capacitacaoMapper.toEntity(capacitacaoDTO);
        capacitacao = capacitacaoRepository.save(capacitacao);
        capacitacaoSearchRepository.index(capacitacao);
        return capacitacaoMapper.toDto(capacitacao);
    }

    /**
     * Update a capacitacao.
     *
     * @param capacitacaoDTO the entity to save.
     * @return the persisted entity.
     */
    public CapacitacaoDTO update(CapacitacaoDTO capacitacaoDTO) {
        LOG.debug("Request to update Capacitacao : {}", capacitacaoDTO);
        Capacitacao capacitacao = capacitacaoMapper.toEntity(capacitacaoDTO);
        capacitacao = capacitacaoRepository.save(capacitacao);
        capacitacaoSearchRepository.index(capacitacao);
        return capacitacaoMapper.toDto(capacitacao);
    }

    /**
     * Partially update a capacitacao.
     *
     * @param capacitacaoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CapacitacaoDTO> partialUpdate(CapacitacaoDTO capacitacaoDTO) {
        LOG.debug("Request to partially update Capacitacao : {}", capacitacaoDTO);

        return capacitacaoRepository
            .findById(capacitacaoDTO.getId())
            .map(existingCapacitacao -> {
                capacitacaoMapper.partialUpdate(existingCapacitacao, capacitacaoDTO);

                return existingCapacitacao;
            })
            .map(capacitacaoRepository::save)
            .map(savedCapacitacao -> {
                capacitacaoSearchRepository.index(savedCapacitacao);
                return savedCapacitacao;
            })
            .map(capacitacaoMapper::toDto);
    }

    /**
     * Get all the capacitacaos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CapacitacaoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return capacitacaoRepository.findAllWithEagerRelationships(pageable).map(capacitacaoMapper::toDto);
    }

    /**
     * Get one capacitacao by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CapacitacaoDTO> findOne(Long id) {
        LOG.debug("Request to get Capacitacao : {}", id);
        return capacitacaoRepository.findOneWithEagerRelationships(id).map(capacitacaoMapper::toDto);
    }

    /**
     * Delete the capacitacao by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Capacitacao : {}", id);
        capacitacaoRepository.deleteById(id);
        capacitacaoSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the capacitacao corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CapacitacaoDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Capacitacaos for query {}", query);
        return capacitacaoSearchRepository.search(query, pageable).map(capacitacaoMapper::toDto);
    }
}
