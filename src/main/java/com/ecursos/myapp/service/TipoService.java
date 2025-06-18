package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.Tipo;
import com.ecursos.myapp.repository.TipoRepository;
import com.ecursos.myapp.repository.search.TipoSearchRepository;
import com.ecursos.myapp.service.dto.TipoDTO;
import com.ecursos.myapp.service.mapper.TipoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import jakarta.annotation.PostConstruct;

/**
 * Service Implementation for managing {@link com.ecursos.myapp.domain.Tipo}.
 */
@Service
@Transactional
public class TipoService {

    private static final Logger LOG = LoggerFactory.getLogger(TipoService.class);

    private final TipoRepository tipoRepository;

    private final TipoMapper tipoMapper;

    private final TipoSearchRepository tipoSearchRepository;

    public TipoService(TipoRepository tipoRepository, TipoMapper tipoMapper, TipoSearchRepository tipoSearchRepository) {
        this.tipoRepository = tipoRepository;
        this.tipoMapper = tipoMapper;
        this.tipoSearchRepository = tipoSearchRepository;
    }

    @PostConstruct
    public void reindexAllTipo() {
        tipoSearchRepository.deleteAll();
        List<Tipo> allTipo = tipoRepository.findAll();
        tipoSearchRepository.saveAll(allTipo);
        
        LOG.debug("Reindexação completa de todos os TIPOS no Elasticsearch");
    }

    /**
     * Save a tipo.
     *
     * @param tipoDTO the entity to save.
     * @return the persisted entity.
     */
    public TipoDTO save(TipoDTO tipoDTO) {
        LOG.debug("Request to save Tipo : {}", tipoDTO);
        Tipo tipo = tipoMapper.toEntity(tipoDTO);
        tipo = tipoRepository.save(tipo);
        tipoSearchRepository.save(tipo);
        return tipoMapper.toDto(tipo);
    }

    /**
     * Update a tipo.
     *
     * @param tipoDTO the entity to save.
     * @return the persisted entity.
     */
    public TipoDTO update(TipoDTO tipoDTO) {
        LOG.debug("Request to update Tipo : {}", tipoDTO);
        Tipo tipo = tipoMapper.toEntity(tipoDTO);
        tipo = tipoRepository.save(tipo);
        tipoSearchRepository.save(tipo);
        return tipoMapper.toDto(tipo);
    }

    /**
     * Partially update a tipo.
     *
     * @param tipoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TipoDTO> partialUpdate(TipoDTO tipoDTO) {
        LOG.debug("Request to partially update Tipo : {}", tipoDTO);

        return tipoRepository
            .findById(tipoDTO.getId())
            .map(existingTipo -> {
                tipoMapper.partialUpdate(existingTipo, tipoDTO);

                return existingTipo;
            })
            .map(tipoRepository::save)
            .map(savedTipo -> {
                tipoSearchRepository.save(savedTipo);
                return savedTipo;
            })
            .map(tipoMapper::toDto);
    }

    /**
     * Get one tipo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TipoDTO> findOne(Long id) {
        LOG.debug("Request to get Tipo : {}", id);
        return tipoRepository.findById(id).map(tipoMapper::toDto);
    }

    /**
     * Delete the tipo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Tipo : {}", id);
        tipoRepository.deleteById(id);
        tipoSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the tipo corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TipoDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Tipos for query {}", query);
        return tipoSearchRepository.search(query, pageable).map(tipoMapper::toDto);
    }
}
