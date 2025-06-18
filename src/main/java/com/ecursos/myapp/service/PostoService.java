package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.repository.PostoRepository;
import com.ecursos.myapp.repository.search.PostoSearchRepository;
import com.ecursos.myapp.service.dto.PostoDTO;
import com.ecursos.myapp.service.mapper.PostoMapper;
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
 * Service Implementation for managing {@link com.ecursos.myapp.domain.Posto}.
 */
@Service
@Transactional
public class PostoService {

    private static final Logger LOG = LoggerFactory.getLogger(PostoService.class);

    private final PostoRepository postoRepository;

    private final PostoMapper postoMapper;

    private final PostoSearchRepository postoSearchRepository;

    public PostoService(PostoRepository postoRepository, PostoMapper postoMapper, PostoSearchRepository postoSearchRepository) {
        this.postoRepository = postoRepository;
        this.postoMapper = postoMapper;
        this.postoSearchRepository = postoSearchRepository;
    }

    @PostConstruct
    public void reindexAllPosto() {
        postoSearchRepository.deleteAll();
        List<Posto> allPosto = postoRepository.findAll();
        postoSearchRepository.saveAll(allPosto);
        
        LOG.debug("Reindexação completa de todos os POSTOS no Elasticsearch");
    }

    /**
     * Save a posto.
     *
     * @param postoDTO the entity to save.
     * @return the persisted entity.
     */
    public PostoDTO save(PostoDTO postoDTO) {
        LOG.debug("Request to save Posto : {}", postoDTO);
        Posto posto = postoMapper.toEntity(postoDTO);
        posto = postoRepository.save(posto);
        postoSearchRepository.save(posto);
        return postoMapper.toDto(posto);
    }

    /**
     * Update a posto.
     *
     * @param postoDTO the entity to save.
     * @return the persisted entity.
     */
    public PostoDTO update(PostoDTO postoDTO) {
        LOG.debug("Request to update Posto : {}", postoDTO);
        Posto posto = postoMapper.toEntity(postoDTO);
        posto = postoRepository.save(posto);
        postoSearchRepository.save(posto);
        return postoMapper.toDto(posto);
    }

    /**
     * Partially update a posto.
     *
     * @param postoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PostoDTO> partialUpdate(PostoDTO postoDTO) {
        LOG.debug("Request to partially update Posto : {}", postoDTO);

        return postoRepository
            .findById(postoDTO.getId())
            .map(existingPosto -> {
                postoMapper.partialUpdate(existingPosto, postoDTO);

                return existingPosto;
            })
            .map(postoRepository::save)
            .map(savedPosto -> {
                postoSearchRepository.save(savedPosto);
                return savedPosto;
            })
            .map(postoMapper::toDto);
    }

    /**
     * Get one posto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PostoDTO> findOne(Long id) {
        LOG.debug("Request to get Posto : {}", id);
        return postoRepository.findById(id).map(postoMapper::toDto);
    }

    /**
     * Delete the posto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Posto : {}", id);
        postoRepository.deleteById(id);
        postoSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the posto corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PostoDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Postos for query {}", query);
        return postoSearchRepository.search(query, pageable).map(postoMapper::toDto);
    }

    public Optional<PostoDTO> findByCodSigpes(Integer codSigpes) {
        return postoRepository.findByCodSigpes(codSigpes).map(postoMapper::toDto);
    }
}
