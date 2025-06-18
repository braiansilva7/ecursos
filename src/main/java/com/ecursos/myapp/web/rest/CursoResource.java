package com.ecursos.myapp.web.rest;

import com.ecursos.myapp.repository.CursoRepository;
import com.ecursos.myapp.service.CursoQueryService;
import com.ecursos.myapp.service.CursoService;
import com.ecursos.myapp.service.criteria.CursoCriteria;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.web.rest.errors.BadRequestAlertException;
import com.ecursos.myapp.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ecursos.myapp.domain.Curso}.
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoResource {

    private static final Logger LOG = LoggerFactory.getLogger(CursoResource.class);

    private static final String ENTITY_NAME = "curso";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CursoService cursoService;

    private final CursoRepository cursoRepository;

    private final CursoQueryService cursoQueryService;

    public CursoResource(CursoService cursoService, CursoRepository cursoRepository, CursoQueryService cursoQueryService) {
        this.cursoService = cursoService;
        this.cursoRepository = cursoRepository;
        this.cursoQueryService = cursoQueryService;
    }

    /**
     * {@code POST  /cursos} : Create a new curso.
     *
     * @param cursoDTO the cursoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cursoDTO, or with status {@code 400 (Bad Request)} if the curso has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CursoDTO> createCurso(@Valid @RequestBody CursoDTO cursoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Curso : {}", cursoDTO);
        if (cursoDTO.getId() != null) {
            throw new BadRequestAlertException("A new curso cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cursoDTO = cursoService.save(cursoDTO);
        return ResponseEntity.created(new URI("/api/cursos/" + cursoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cursoDTO.getCursoNome().toString()))
            .body(cursoDTO);
    }

    /**
     * {@code PUT  /cursos/:id} : Updates an existing curso.
     *
     * @param id the id of the cursoDTO to save.
     * @param cursoDTO the cursoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cursoDTO,
     * or with status {@code 400 (Bad Request)} if the cursoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cursoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> updateCurso(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CursoDTO cursoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Curso : {}, {}", id, cursoDTO);
        if (cursoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cursoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cursoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cursoDTO = cursoService.update(cursoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cursoDTO.getCursoNome().toString()))
            .body(cursoDTO);
    }

    /**
     * {@code PATCH  /cursos/:id} : Partial updates given fields of an existing curso, field will ignore if it is null
     *
     * @param id the id of the cursoDTO to save.
     * @param cursoDTO the cursoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cursoDTO,
     * or with status {@code 400 (Bad Request)} if the cursoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cursoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cursoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CursoDTO> partialUpdateCurso(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CursoDTO cursoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Curso partially : {}, {}", id, cursoDTO);
        if (cursoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cursoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cursoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CursoDTO> result = cursoService.partialUpdate(cursoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cursoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cursos} : get all the cursos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cursos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CursoDTO>> getAllCursos(
        CursoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Cursos by criteria: {}", criteria);

        Page<CursoDTO> page = cursoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("all")
    public ResponseEntity<List<CursoDTO>> getAllCursos(CursoCriteria criteria) {
        List<CursoDTO> cursos = cursoQueryService.findByCriteriaAll(criteria);

        return ResponseEntity.ok().body(cursos);
    }

    /**
     * {@code GET  /cursos/count} : count all the cursos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCursos(CursoCriteria criteria) {
        LOG.debug("REST request to count Cursos by criteria: {}", criteria);
        return ResponseEntity.ok().body(cursoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cursos/:id} : get the "id" curso.
     *
     * @param id the id of the cursoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cursoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> getCurso(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Curso : {}", id);
        Optional<CursoDTO> cursoDTO = cursoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cursoDTO);
    }

    /**
     * {@code DELETE  /cursos/:id} : delete the "id" curso.
     *
     * @param id the id of the cursoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurso(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Curso : {}", id);
        cursoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /cursos/_search?query=:query} : search for the curso corresponding
     * to the query.
     *
     * @param query the query of the curso search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CursoDTO>> searchCursos(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Cursos for query {}", query);
        try {
            Page<CursoDTO> page = cursoService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
