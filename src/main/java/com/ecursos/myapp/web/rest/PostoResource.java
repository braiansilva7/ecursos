package com.ecursos.myapp.web.rest;

import com.ecursos.myapp.repository.PostoRepository;
import com.ecursos.myapp.service.PostoQueryService;
import com.ecursos.myapp.service.PostoService;
import com.ecursos.myapp.service.criteria.PostoCriteria;
import com.ecursos.myapp.service.dto.PostoDTO;
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
 * REST controller for managing {@link com.ecursos.myapp.domain.Posto}.
 */
@RestController
@RequestMapping("/api/postos")
public class PostoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostoResource.class);

    private static final String ENTITY_NAME = "posto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostoService postoService;

    private final PostoRepository postoRepository;

    private final PostoQueryService postoQueryService;

    public PostoResource(PostoService postoService, PostoRepository postoRepository, PostoQueryService postoQueryService) {
        this.postoService = postoService;
        this.postoRepository = postoRepository;
        this.postoQueryService = postoQueryService;
    }

    /**
     * {@code POST  /postos} : Create a new posto.
     *
     * @param postoDTO the postoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new postoDTO, or with status {@code 400 (Bad Request)} if the posto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PostoDTO> createPosto(@Valid @RequestBody PostoDTO postoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Posto : {}", postoDTO);
        if (postoDTO.getId() != null) {
            throw new BadRequestAlertException("A new posto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        postoDTO = postoService.save(postoDTO);
        return ResponseEntity.created(new URI("/api/postos/" + postoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, postoDTO.getPostoSigla().toString()))
            .body(postoDTO);
    }

    /**
     * {@code PUT  /postos/:id} : Updates an existing posto.
     *
     * @param id the id of the postoDTO to save.
     * @param postoDTO the postoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postoDTO,
     * or with status {@code 400 (Bad Request)} if the postoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the postoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostoDTO> updatePosto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PostoDTO postoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Posto : {}, {}", id, postoDTO);
        if (postoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        postoDTO = postoService.update(postoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postoDTO.getPostoSigla().toString()))
            .body(postoDTO);
    }

    /**
     * {@code PATCH  /postos/:id} : Partial updates given fields of an existing posto, field will ignore if it is null
     *
     * @param id the id of the postoDTO to save.
     * @param postoDTO the postoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated postoDTO,
     * or with status {@code 400 (Bad Request)} if the postoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the postoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the postoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PostoDTO> partialUpdatePosto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PostoDTO postoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Posto partially : {}, {}", id, postoDTO);
        if (postoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, postoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!postoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PostoDTO> result = postoService.partialUpdate(postoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, postoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /postos} : get all the postos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PostoDTO>> getAllPostos(PostoCriteria criteria) {
        LOG.debug("REST request to get all Postos by criteria: {}", criteria);

        // Buscar todos os registros sem paginação
        List<PostoDTO> postos = postoQueryService.findByCriteria(criteria);
        
        return ResponseEntity.ok().body(postos);
    }

    /**
     * {@code GET  /postos/count} : count all the postos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPostos(PostoCriteria criteria) {
        LOG.debug("REST request to count Postos by criteria: {}", criteria);
        return ResponseEntity.ok().body(postoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /postos/:id} : get the "id" posto.
     *
     * @param id the id of the postoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the postoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostoDTO> getPosto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Posto : {}", id);
        Optional<PostoDTO> postoDTO = postoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(postoDTO);
    }

    /**
     * {@code DELETE  /postos/:id} : delete the "id" posto.
     *
     * @param id the id of the postoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Posto : {}", id);
        postoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /postos/_search?query=:query} : search for the posto corresponding
     * to the query.
     *
     * @param query the query of the posto search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PostoDTO>> searchPostos(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Postos for query {}", query);
        try {
            Page<PostoDTO> page = postoService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
