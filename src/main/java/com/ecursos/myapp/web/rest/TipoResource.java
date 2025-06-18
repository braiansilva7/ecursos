package com.ecursos.myapp.web.rest;

import com.ecursos.myapp.repository.TipoRepository;
import com.ecursos.myapp.service.TipoQueryService;
import com.ecursos.myapp.service.TipoService;
import com.ecursos.myapp.service.criteria.TipoCriteria;
import com.ecursos.myapp.service.dto.TipoDTO;
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
 * REST controller for managing {@link com.ecursos.myapp.domain.Tipo}.
 */
@RestController
@RequestMapping("/api/tipos")
public class TipoResource {

    private static final Logger LOG = LoggerFactory.getLogger(TipoResource.class);

    private static final String ENTITY_NAME = "tipo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TipoService tipoService;

    private final TipoRepository tipoRepository;

    private final TipoQueryService tipoQueryService;

    public TipoResource(TipoService tipoService, TipoRepository tipoRepository, TipoQueryService tipoQueryService) {
        this.tipoService = tipoService;
        this.tipoRepository = tipoRepository;
        this.tipoQueryService = tipoQueryService;
    }

    /**
     * {@code POST  /tipos} : Create a new tipo.
     *
     * @param tipoDTO the tipoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tipoDTO, or with status {@code 400 (Bad Request)} if the tipo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TipoDTO> createTipo(@Valid @RequestBody TipoDTO tipoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Tipo : {}", tipoDTO);
        if (tipoDTO.getId() != null) {
            throw new BadRequestAlertException("A new tipo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tipoDTO = tipoService.save(tipoDTO);
        return ResponseEntity.created(new URI("/api/tipos/" + tipoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tipoDTO.getCategoria().toString()))
            .body(tipoDTO);
    }

    /**
     * {@code PUT  /tipos/:id} : Updates an existing tipo.
     *
     * @param id the id of the tipoDTO to save.
     * @param tipoDTO the tipoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoDTO,
     * or with status {@code 400 (Bad Request)} if the tipoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tipoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TipoDTO> updateTipo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TipoDTO tipoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Tipo : {}, {}", id, tipoDTO);
        if (tipoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tipoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tipoDTO = tipoService.update(tipoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tipoDTO.getCategoria().toString()))
            .body(tipoDTO);
    }

    /**
     * {@code PATCH  /tipos/:id} : Partial updates given fields of an existing tipo, field will ignore if it is null
     *
     * @param id the id of the tipoDTO to save.
     * @param tipoDTO the tipoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoDTO,
     * or with status {@code 400 (Bad Request)} if the tipoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tipoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tipoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TipoDTO> partialUpdateTipo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TipoDTO tipoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Tipo partially : {}, {}", id, tipoDTO);
        if (tipoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tipoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TipoDTO> result = tipoService.partialUpdate(tipoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tipoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tipos} : get all the tipos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tipos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TipoDTO>> getAllTipos(
        TipoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Tipos by criteria: {}", criteria);

        Page<TipoDTO> page = tipoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tipos/count} : count all the tipos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTipos(TipoCriteria criteria) {
        LOG.debug("REST request to count Tipos by criteria: {}", criteria);
        return ResponseEntity.ok().body(tipoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tipos/:id} : get the "id" tipo.
     *
     * @param id the id of the tipoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tipoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoDTO> getTipo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Tipo : {}", id);
        Optional<TipoDTO> tipoDTO = tipoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tipoDTO);
    }

    /**
     * {@code DELETE  /tipos/:id} : delete the "id" tipo.
     *
     * @param id the id of the tipoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Tipo : {}", id);
        tipoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /tipos/_search?query=:query} : search for the tipo corresponding
     * to the query.
     *
     * @param query the query of the tipo search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TipoDTO>> searchTipos(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Tipos for query {}", query);
        try {
            Page<TipoDTO> page = tipoService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
