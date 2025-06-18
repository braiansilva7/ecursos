package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.Curso;
import com.ecursos.myapp.repository.CursoRepository;
import com.ecursos.myapp.repository.search.CursoSearchRepository;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.service.mapper.CursoMapper;
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
 * Service Implementation for managing {@link com.ecursos.myapp.domain.Curso}.
 */
@Service
@Transactional
public class CursoService {

    private static final Logger LOG = LoggerFactory.getLogger(CursoService.class);

    private final CursoRepository cursoRepository;

    private final CursoMapper cursoMapper;

    private final CursoSearchRepository cursoSearchRepository;

    public CursoService(CursoRepository cursoRepository, CursoMapper cursoMapper, CursoSearchRepository cursoSearchRepository) {
        this.cursoRepository = cursoRepository;
        this.cursoMapper = cursoMapper;
        this.cursoSearchRepository = cursoSearchRepository;
    }

    @PostConstruct
    public void reindexAllCurso() {
        cursoSearchRepository.deleteAll();
        List<Curso> allCurso = cursoRepository.findAll();
        cursoSearchRepository.saveAll(allCurso);

        LOG.debug("Reindexação completa de todos os CURSOS no Elasticsearch");
    }

    /**
     * Save a curso.
     *
     * @param cursoDTO the entity to save.
     * @return the persisted entity.
     */
    public CursoDTO save(CursoDTO cursoDTO) {
        LOG.debug("Request to save Curso : {}", cursoDTO);
        Curso curso = cursoMapper.toEntity(cursoDTO);
        curso = cursoRepository.save(curso);
        cursoSearchRepository.save(curso);
        return cursoMapper.toDto(curso);
    }

    /**
     * Update a curso.
     *
     * @param cursoDTO the entity to save.
     * @return the persisted entity.
     */
    public CursoDTO update(CursoDTO cursoDTO) {
        LOG.debug("Request to update Curso : {}", cursoDTO);
        Curso curso = cursoMapper.toEntity(cursoDTO);
        curso = cursoRepository.save(curso);
        cursoSearchRepository.save(curso);
        return cursoMapper.toDto(curso);
    }

    /**
     * Partially update a curso.
     *
     * @param cursoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CursoDTO> partialUpdate(CursoDTO cursoDTO) {
        LOG.debug("Request to partially update Curso : {}", cursoDTO);

        return cursoRepository
            .findById(cursoDTO.getId())
            .map(existingCurso -> {
                cursoMapper.partialUpdate(existingCurso, cursoDTO);

                return existingCurso;
            })
            .map(cursoRepository::save)
            .map(savedCurso -> {
                cursoSearchRepository.save(savedCurso);
                return savedCurso;
            })
            .map(cursoMapper::toDto);
    }

    /**
     * Get all the cursos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CursoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return cursoRepository.findAllWithEagerRelationships(pageable).map(cursoMapper::toDto);
    }

    /**
     * Get one curso by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CursoDTO> findOne(Long id) {
        LOG.debug("Request to get Curso : {}", id);
        return cursoRepository.findOneWithEagerRelationships(id).map(cursoMapper::toDto);
    }

    /**
     * Delete the curso by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Curso : {}", id);
        cursoRepository.deleteById(id);
        cursoSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the curso corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CursoDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Cursos for query {}", query);
        return cursoSearchRepository.search(query, pageable).map(cursoMapper::toDto);
    }
}
