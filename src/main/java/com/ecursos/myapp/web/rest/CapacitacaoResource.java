package com.ecursos.myapp.web.rest;

import com.ecursos.myapp.repository.CapacitacaoRepository;
import com.ecursos.myapp.service.CapacitacaoQueryService;
import com.ecursos.myapp.service.CapacitacaoService;
import com.ecursos.myapp.service.criteria.CapacitacaoCriteria;
import com.ecursos.myapp.service.dto.CapacitacaoDTO;
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
import org.springframework.security.core.Authentication;

/**
 * REST controller for managing {@link com.ecursos.myapp.domain.Capacitacao}.
 */
@RestController
@RequestMapping("/api/capacitacaos")
public class CapacitacaoResource {

    private static final Logger LOG = LoggerFactory.getLogger(CapacitacaoResource.class);

    private static final String ENTITY_NAME = "capacitacao";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CapacitacaoService capacitacaoService;

    private final CapacitacaoRepository capacitacaoRepository;

    private final CapacitacaoQueryService capacitacaoQueryService;

    public CapacitacaoResource(
        CapacitacaoService capacitacaoService,
        CapacitacaoRepository capacitacaoRepository,
        CapacitacaoQueryService capacitacaoQueryService
    ) {
        this.capacitacaoService = capacitacaoService;
        this.capacitacaoRepository = capacitacaoRepository;
        this.capacitacaoQueryService = capacitacaoQueryService;
    }

    /**
     * {@code POST  /capacitacaos} : Create a new capacitacao.
     *
     * @param capacitacaoDTO the capacitacaoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new capacitacaoDTO, or with status {@code 400 (Bad Request)} if the capacitacao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CapacitacaoDTO> createCapacitacao(@Valid @RequestBody CapacitacaoDTO capacitacaoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Capacitacao : {}", capacitacaoDTO);
        if (capacitacaoDTO.getId() != null) {
            throw new BadRequestAlertException("A new capacitacao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        capacitacaoDTO = capacitacaoService.save(capacitacaoDTO);
        return ResponseEntity.created(new URI("/api/capacitacaos/" + capacitacaoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, capacitacaoDTO.getMilitar().getNomeGuerra().toString()))
            .body(capacitacaoDTO);
    }

    /**
     * {@code PUT  /capacitacaos/:id} : Updates an existing capacitacao.
     *
     * @param id the id of the capacitacaoDTO to save.
     * @param capacitacaoDTO the capacitacaoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capacitacaoDTO,
     * or with status {@code 400 (Bad Request)} if the capacitacaoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the capacitacaoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CapacitacaoDTO> updateCapacitacao(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CapacitacaoDTO capacitacaoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Capacitacao : {}, {}", id, capacitacaoDTO);
        if (capacitacaoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, capacitacaoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!capacitacaoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        capacitacaoDTO = capacitacaoService.update(capacitacaoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, capacitacaoDTO.getMilitar().getNomeGuerra().toString()))
            .body(capacitacaoDTO);
    }

    /**
     * {@code PATCH  /capacitacaos/:id} : Partial updates given fields of an existing capacitacao, field will ignore if it is null
     *
     * @param id the id of the capacitacaoDTO to save.
     * @param capacitacaoDTO the capacitacaoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capacitacaoDTO,
     * or with status {@code 400 (Bad Request)} if the capacitacaoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the capacitacaoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the capacitacaoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CapacitacaoDTO> partialUpdateCapacitacao(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CapacitacaoDTO capacitacaoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Capacitacao partially : {}, {}", id, capacitacaoDTO);
        if (capacitacaoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, capacitacaoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!capacitacaoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CapacitacaoDTO> result = capacitacaoService.partialUpdate(capacitacaoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, capacitacaoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /capacitacaos} : get all the capacitacaos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of capacitacaos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CapacitacaoDTO>> getAllCapacitacaos(
        CapacitacaoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        Authentication authentication
    ) {
        LOG.debug("REST request to get Capacitacaos by criteria: {}", criteria);

        Page<CapacitacaoDTO> page = capacitacaoQueryService.findByCriteria(criteria, pageable, authentication);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("all")
    public ResponseEntity<List<CapacitacaoDTO>> getAllCapacitacaos(CapacitacaoCriteria criteria, Authentication authentication) {
        LOG.debug("REST request to get Capacitacaos by criteria: {}", criteria);

        List<CapacitacaoDTO> capacitacaoAll = capacitacaoQueryService.findByCriteriaAll(criteria, authentication);

        return ResponseEntity.ok().body(capacitacaoAll); 
    }

    /**
     * {@code GET  /capacitacaos/count} : count all the capacitacaos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCapacitacaos(CapacitacaoCriteria criteria) {
        LOG.debug("REST request to count Capacitacaos by criteria: {}", criteria);
        return ResponseEntity.ok().body(capacitacaoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /capacitacaos/:id} : get the "id" capacitacao.
     *
     * @param id the id of the capacitacaoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the capacitacaoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CapacitacaoDTO> getCapacitacao(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Capacitacao : {}", id);
        Optional<CapacitacaoDTO> capacitacaoDTO = capacitacaoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(capacitacaoDTO);
    }

    /**
     * {@code DELETE  /capacitacaos/:id} : delete the "id" capacitacao.
     *
     * @param id the id of the capacitacaoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCapacitacao(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Capacitacao : {}", id);
        capacitacaoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /capacitacaos/_search?query=:query} : search for the capacitacao corresponding
     * to the query.
     *
     * @param query the query of the capacitacao search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CapacitacaoDTO>> searchCapacitacaos(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Capacitacaos for query {}", query);
        try {
            Page<CapacitacaoDTO> page = capacitacaoService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
