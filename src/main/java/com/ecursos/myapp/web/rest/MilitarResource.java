package com.ecursos.myapp.web.rest;

import com.ecursos.myapp.repository.MilitarRepository;
import com.ecursos.myapp.service.MilitarQueryService;
import com.ecursos.myapp.service.MilitarService;
import com.ecursos.myapp.service.PostoService;
import com.ecursos.myapp.service.criteria.MilitarCriteria;
import com.ecursos.myapp.service.dto.MilitarDTO;
import com.ecursos.myapp.service.dto.PostoDTO;
import com.ecursos.myapp.domain.MilitarApi;
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
import com.ecursos.myapp.domain.enumeration.ForcaEnum;
import com.ecursos.myapp.domain.enumeration.StatusMilitarEnum;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import jakarta.servlet.http.HttpServletRequest;
import com.ecursos.myapp.service.MinIOService;
import java.util.concurrent.CompletableFuture;
import java.util.Collections;

/**
 * REST controller for managing {@link com.ecursos.myapp.domain.Militar}.
 */
@RestController
@RequestMapping("/api/militars")
public class MilitarResource {

    private static final Logger LOG = LoggerFactory.getLogger(MilitarResource.class);

    private static final String ENTITY_NAME = "militar";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${ecursos.webservice.api-fotos}")
    private String apiFotos;

    private static final String CCABR_PREFIX = "efetivo/";

    private final MilitarService militarService;

    private final MilitarRepository militarRepository;

    private final MilitarQueryService militarQueryService;

    private final PostoService postoService;

    private final MinIOService minIOService;

    public MilitarResource(MilitarService militarService, MilitarRepository militarRepository, MilitarQueryService militarQueryService, PostoService postoService, MinIOService minIOService) {
        this.militarService = militarService;
        this.militarRepository = militarRepository;
        this.militarQueryService = militarQueryService;
        this.postoService = postoService;
        this.minIOService = minIOService;
    }

    @GetMapping("/om")
    public ResponseEntity<List<MilitarDTO>> getAllMilitarsOM(MilitarCriteria criteria) {

        // Obter todos os militares da API e do banco de dados
        List<MilitarApi> militarResponses = militarService.getAllMilitarsOM();
        LOG.debug("REST request to get MilitarsOM by criteria: {}", militarResponses);

        // Buscar todos os militares cadastrados no banco de dados sem paginação
        List<MilitarDTO> militares = militarQueryService.findByCriteria(criteria, Pageable.unpaged()).getContent();
        LOG.debug("REST request to get MILITARES CADASTRADOS: {}", militares);

        // Mapear os militares existentes pelo SARAM para acesso rápido
        Map<String, MilitarDTO> militaresMap = militares.stream()
                .collect(Collectors.toMap(MilitarDTO::getSaram, Function.identity()));

        // Listas para atualização e inserção em lote
        List<MilitarDTO> militaresParaAtualizar = new ArrayList<>();
        List<MilitarDTO> militaresParaInserir = new ArrayList<>();

        // Processar os militares retornados pela API
        for (MilitarApi militarApi : militarResponses) {
            String saram = militarApi.getOrdem();
            MilitarDTO militarExistente = militaresMap.get(saram);

            try {
                // Fazer o download e salvar a foto do militar
                //militarService.downloadAndSavePhoto(saram);

                if (militarExistente != null) {
                    // Atualizar militar existente
                    MilitarDTO atualizadoDTO = convertMilitarApiToDTO(militarApi);
                    atualizadoDTO.setId(militarExistente.getId());
                    militaresParaAtualizar.add(atualizadoDTO);
                } else {
                    militarService.downloadAndSavePhoto(saram);
                    // Inserir novo militar
                    MilitarDTO novoDTO = convertMilitarApiToDTO(militarApi);
                    if (novoDTO.getId() != null) {
                        throw new BadRequestAlertException("A new militar cannot already have an ID", ENTITY_NAME, "idexists");
                    }
                    militaresParaInserir.add(novoDTO);
                }
            } catch (Exception e) {
                LOG.error("Falha ao baixar e salvar a foto para o SARAM {}: {}", saram, e.getMessage());
            }
        }

        // Executar atualizações e inserções em lote
        if (!militaresParaAtualizar.isEmpty()) {
            LOG.debug("Atualizando {} militares em lote.", militaresParaAtualizar.size());
            militarService.updateAll(militaresParaAtualizar);
        }

        if (!militaresParaInserir.isEmpty()) {
            LOG.debug("Inserindo {} novos militares em lote.", militaresParaInserir.size());
            militarService.saveAll(militaresParaInserir);
        }

        // Retornar a lista completa de militares atualizados após o processamento
        List<MilitarDTO> todosMilitares = militarQueryService.findByCriteria(criteria, Pageable.unpaged()).getContent();
        return ResponseEntity.ok().body(todosMilitares);
    }


    // Helper method to convert MilitarApi to MilitarDTO
    private MilitarDTO convertMilitarApiToDTO(MilitarApi militarApi) {
        MilitarDTO dto = new MilitarDTO();
        
        // Ajuste para usar o método getId da instância militarApi
        if (militarApi.getId() != null) { // Verifique se o ID não é nulo
            dto.setId(militarApi.getId()); // Certifique-se de usar setId para definir o ID
        }
        dto.setSaram(militarApi.getOrdem());
        dto.setNomeCompleto(militarApi.getPessoa());
        dto.setNomeGuerra(militarApi.getGuerra());
        dto.setOm(militarApi.getSgOrg());
        dto.setCpf(militarApi.getCpf());
        dto.setEmail(militarApi.getEmail());
        dto.setNrAntiguidade(militarApi.getNrAntiguidade());
        dto.setUltimaPromocao(militarApi.getUltimaPromocao());
        dto.setTelefone(militarApi.getTelefoneCel());

        // Determine statusMilitar and forca based on your business logic
        dto.setStatusMilitar(determineStatusMilitar(militarApi));
        dto.setForca(determineForca(militarApi));

        Integer codSigpes;
        try {
            codSigpes = Integer.parseInt(militarApi.getPosto());
        } catch (NumberFormatException e) {
            throw new BadRequestAlertException("Invalid Posto code: " + militarApi.getPosto(), "Militar", "invalidcode");
        }

        // Recuperar o PostoDTO com base em codSigpes
        PostoDTO postoDTO = postoService.findByCodSigpes(codSigpes)
            .orElseThrow(() -> new BadRequestAlertException("Posto not found for code: " + militarApi.getPosto(), "Militar", "postonotfound"));
        dto.setPosto(postoDTO);

        return dto;
    }

    // Example methods to determine StatusMilitarEnum and ForcaEnum
    private StatusMilitarEnum determineStatusMilitar(MilitarApi militarApi) {
        if(militarApi.getPerfil().equalsIgnoreCase("ATIVO")){
            return StatusMilitarEnum.ATIVA; // Placeholder value
        }
        if(militarApi.getPerfil().equalsIgnoreCase("INAT")){
            return StatusMilitarEnum.INATIVO; // Placeholder value
        }
        return StatusMilitarEnum.TRANSFERIDO;
        
    }

    private ForcaEnum determineForca(MilitarApi militarApi) {
        // Implement your logic here
        return ForcaEnum.FORCA_AEREA_BRASILEIRA; // Placeholder value
    }

   @GetMapping("/foto/{saram}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable("saram") String saram, HttpServletRequest request) {
        try {
            // Obtem a URL da API externa para a foto do militar
            String externalPhotoUrl = minIOService.downloadImage(null, CCABR_PREFIX + saram + ".jpg");

            // Faz uma requisição HTTP para a API externa
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.exchange(
                externalPhotoUrl,
                HttpMethod.GET,
                null,
                byte[].class
            );

            // Verifica se a API externa retornou a foto com sucesso
            if (response.getStatusCode() == HttpStatus.OK) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // Define o tipo de mídia
                return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
            } else {
                // Caso a API externa não tenha retornado a foto, retorna NOT FOUND
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("Foto não encontrada para o SARAM: " + saram).getBytes());
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Caso a API externa retorne 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Foto não encontrada para o SARAM: " + saram).getBytes());
        } catch (Exception e) {
            // Tratamento genérico para outros erros
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao acessar a API externa: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/foto/local/{saram}")
    public ResponseEntity<String> getPhotoPath(@PathVariable("saram") String saram) {
        try {
            String url = minIOService.downloadImage(null, CCABR_PREFIX + saram + ".jpg");
            LOG.debug("URL DE RETORNO {}.", url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar imagem");
        }
    }

    @GetMapping("/consulta/{saram}")
    public ResponseEntity<MilitarApi> getConsultaSaram(@PathVariable("saram") String saram) {
        Optional<MilitarApi> consultaMilitarSaram = militarService.getConsultaSaram(saram);
        LOG.debug("REST request to get ConsultaMilitarSaram by SARAM: {}", saram);
        
        return consultaMilitarSaram
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code POST  /militars} : Create a new militar.
     *
     * @param militarDTO the militarDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new militarDTO, or with status {@code 400 (Bad Request)} if the militar has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MilitarDTO> createMilitar(@Valid @RequestBody MilitarDTO militarDTO) throws URISyntaxException {
        LOG.debug("REST request to save Militar : {}", militarDTO);
        
        // Verifica se o militar com o mesmo SARAM já existe
        Optional<MilitarDTO> existingMilitar = militarService.findBySaram(militarDTO.getSaram());
        if (existingMilitar.isPresent()) {
            throw new BadRequestAlertException("O militar com o SARAM informado já está cadastrado", ENTITY_NAME, "saramexists");
        }

        if (militarDTO.getId() != null) {
            throw new BadRequestAlertException("A new militar cannot already have an ID", ENTITY_NAME, "idexists");
        }
        militarDTO = militarService.save(militarDTO);
        return ResponseEntity.created(new URI("/api/militars/" + militarDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, militarDTO.getNomeGuerra().toString()))
            .body(militarDTO);
    }

    /**
     * {@code PUT  /militars/:id} : Updates an existing militar.
     *
     * @param id the id of the militarDTO to save.
     * @param militarDTO the militarDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated militarDTO,
     * or with status {@code 400 (Bad Request)} if the militarDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the militarDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MilitarDTO> updateMilitar(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MilitarDTO militarDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Militar : {}, {}", id, militarDTO);
        if (militarDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, militarDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!militarRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        militarDTO = militarService.update(militarDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, militarDTO.getNomeGuerra().toString()))
            .body(militarDTO);
    }

    /**
     * {@code PATCH  /militars/:id} : Partial updates given fields of an existing militar, field will ignore if it is null
     *
     * @param id the id of the militarDTO to save.
     * @param militarDTO the militarDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated militarDTO,
     * or with status {@code 400 (Bad Request)} if the militarDTO is not valid,
     * or with status {@code 404 (Not Found)} if the militarDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the militarDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MilitarDTO> partialUpdateMilitar(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MilitarDTO militarDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Militar partially : {}, {}", id, militarDTO);
        if (militarDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, militarDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!militarRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MilitarDTO> result = militarService.partialUpdate(militarDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, militarDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /militars} : get all the militars.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of militars in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MilitarDTO>> getAllMilitars(
        MilitarCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Militars by criteria: {}", criteria);

        Page<MilitarDTO> page = militarQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

     @GetMapping("all")
    public ResponseEntity<List<MilitarDTO>> getAllMilitars(MilitarCriteria criteria) {
        LOG.debug("REST request to get all Militars by criteria: {}", criteria);

        // Ajuste para buscar todos os registros sem paginação
        List<MilitarDTO> militars = militarQueryService.findByCriteriaAll(criteria);

        return ResponseEntity.ok().body(militars);
    }

    /**
     * {@code GET  /militars/count} : count all the militars.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMilitars(MilitarCriteria criteria) {
        LOG.debug("REST request to count Militars by criteria: {}", criteria);
        return ResponseEntity.ok().body(militarQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /militars/:id} : get the "id" militar.
     *
     * @param id the id of the militarDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the militarDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MilitarDTO> getMilitar(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Militar : {}", id);
        Optional<MilitarDTO> militarDTO = militarService.findOne(id);
        return ResponseUtil.wrapOrNotFound(militarDTO);
    }

    /**
     * {@code DELETE  /militars/:id} : delete the "id" militar.
     *
     * @param id the id of the militarDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMilitar(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Militar : {}", id);

        String saram = militarService.findOne(id)
        .map(MilitarDTO::getSaram)
        .orElseThrow(() ->
            new BadRequestAlertException(
                "Militar não encontrado",
                ENTITY_NAME,
                "idnotfound"
            )
        );
        try {
            minIOService.deleteFile(null, CCABR_PREFIX + saram + ".jpg");
        } catch (Exception e) {
            LOG.error("Erro ao deletar imagem do MinIO para o SARAM {}: {}", saram, e.getMessage());
        }

        militarService.delete(id);

        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /militars/_search?query=:query} : search for the militar corresponding
     * to the query.
     *
     * @param query the query of the militar search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<MilitarDTO>> searchMilitars(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Militars for query {}", query);
        try {
            Page<MilitarDTO> page = militarService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
