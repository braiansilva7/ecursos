package com.ecursos.myapp.web.rest;

import static com.ecursos.myapp.domain.PostoAsserts.*;
import static com.ecursos.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecursos.myapp.IntegrationTest;
import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.repository.PostoRepository;
import com.ecursos.myapp.repository.search.PostoSearchRepository;
import com.ecursos.myapp.service.dto.PostoDTO;
import com.ecursos.myapp.service.mapper.PostoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PostoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PostoResourceIT {

    private static final String DEFAULT_POSTO_SIGLA = "AAAAAAAAAA";
    private static final String UPDATED_POSTO_SIGLA = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRIORIDADE = 1;
    private static final Integer UPDATED_PRIORIDADE = 2;
    private static final Integer SMALLER_PRIORIDADE = 1 - 1;

    private static final Integer DEFAULT_ORGAO = 1;
    private static final Integer UPDATED_ORGAO = 2;
    private static final Integer SMALLER_ORGAO = 1 - 1;

    private static final Integer DEFAULT_COD_SIGPES = 1;
    private static final Integer UPDATED_COD_SIGPES = 2;
    private static final Integer SMALLER_COD_SIGPES = 1 - 1;

    private static final String ENTITY_API_URL = "/api/postos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/postos/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PostoRepository postoRepository;

    @Autowired
    private PostoMapper postoMapper;

    @Autowired
    private PostoSearchRepository postoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostoMockMvc;

    private Posto posto;

    private Posto insertedPosto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posto createEntity() {
        return new Posto()
            .postoSigla(DEFAULT_POSTO_SIGLA)
            .descricao(DEFAULT_DESCRICAO)
            .prioridade(DEFAULT_PRIORIDADE)
            .orgao(DEFAULT_ORGAO)
            .codSigpes(DEFAULT_COD_SIGPES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posto createUpdatedEntity() {
        return new Posto()
            .postoSigla(UPDATED_POSTO_SIGLA)
            .descricao(UPDATED_DESCRICAO)
            .prioridade(UPDATED_PRIORIDADE)
            .orgao(UPDATED_ORGAO)
            .codSigpes(UPDATED_COD_SIGPES);
    }

    @BeforeEach
    public void initTest() {
        posto = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPosto != null) {
            postoRepository.delete(insertedPosto);
            postoSearchRepository.delete(insertedPosto);
            insertedPosto = null;
        }
    }

    @Test
    @Transactional
    void createPosto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);
        var returnedPostoDTO = om.readValue(
            restPostoMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PostoDTO.class
        );

        // Validate the Posto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPosto = postoMapper.toEntity(returnedPostoDTO);
        assertPostoUpdatableFieldsEquals(returnedPosto, getPersistedPosto(returnedPosto));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPosto = returnedPosto;
    }

    @Test
    @Transactional
    void createPostoWithExistingId() throws Exception {
        // Create the Posto with an existing ID
        posto.setId(1L);
        PostoDTO postoDTO = postoMapper.toDto(posto);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPostoSiglaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        // set the field null
        posto.setPostoSigla(null);

        // Create the Posto, which fails.
        PostoDTO postoDTO = postoMapper.toDto(posto);

        restPostoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDescricaoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        // set the field null
        posto.setDescricao(null);

        // Create the Posto, which fails.
        PostoDTO postoDTO = postoMapper.toDto(posto);

        restPostoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPrioridadeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        // set the field null
        posto.setPrioridade(null);

        // Create the Posto, which fails.
        PostoDTO postoDTO = postoMapper.toDto(posto);

        restPostoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOrgaoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        // set the field null
        posto.setOrgao(null);

        // Create the Posto, which fails.
        PostoDTO postoDTO = postoMapper.toDto(posto);

        restPostoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPostos() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList
        restPostoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(posto.getId().intValue())))
            .andExpect(jsonPath("$.[*].postoSigla").value(hasItem(DEFAULT_POSTO_SIGLA)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)))
            .andExpect(jsonPath("$.[*].prioridade").value(hasItem(DEFAULT_PRIORIDADE)))
            .andExpect(jsonPath("$.[*].orgao").value(hasItem(DEFAULT_ORGAO)))
            .andExpect(jsonPath("$.[*].codSigpes").value(hasItem(DEFAULT_COD_SIGPES)));
    }

    @Test
    @Transactional
    void getPosto() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get the posto
        restPostoMockMvc
            .perform(get(ENTITY_API_URL_ID, posto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(posto.getId().intValue()))
            .andExpect(jsonPath("$.postoSigla").value(DEFAULT_POSTO_SIGLA))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO))
            .andExpect(jsonPath("$.prioridade").value(DEFAULT_PRIORIDADE))
            .andExpect(jsonPath("$.orgao").value(DEFAULT_ORGAO))
            .andExpect(jsonPath("$.codSigpes").value(DEFAULT_COD_SIGPES));
    }

    @Test
    @Transactional
    void getPostosByIdFiltering() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        Long id = posto.getId();

        defaultPostoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPostoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPostoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostosByPostoSiglaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where postoSigla equals to
        defaultPostoFiltering("postoSigla.equals=" + DEFAULT_POSTO_SIGLA, "postoSigla.equals=" + UPDATED_POSTO_SIGLA);
    }

    @Test
    @Transactional
    void getAllPostosByPostoSiglaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where postoSigla in
        defaultPostoFiltering("postoSigla.in=" + DEFAULT_POSTO_SIGLA + "," + UPDATED_POSTO_SIGLA, "postoSigla.in=" + UPDATED_POSTO_SIGLA);
    }

    @Test
    @Transactional
    void getAllPostosByPostoSiglaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where postoSigla is not null
        defaultPostoFiltering("postoSigla.specified=true", "postoSigla.specified=false");
    }

    @Test
    @Transactional
    void getAllPostosByPostoSiglaContainsSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where postoSigla contains
        defaultPostoFiltering("postoSigla.contains=" + DEFAULT_POSTO_SIGLA, "postoSigla.contains=" + UPDATED_POSTO_SIGLA);
    }

    @Test
    @Transactional
    void getAllPostosByPostoSiglaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where postoSigla does not contain
        defaultPostoFiltering("postoSigla.doesNotContain=" + UPDATED_POSTO_SIGLA, "postoSigla.doesNotContain=" + DEFAULT_POSTO_SIGLA);
    }

    @Test
    @Transactional
    void getAllPostosByDescricaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where descricao equals to
        defaultPostoFiltering("descricao.equals=" + DEFAULT_DESCRICAO, "descricao.equals=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllPostosByDescricaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where descricao in
        defaultPostoFiltering("descricao.in=" + DEFAULT_DESCRICAO + "," + UPDATED_DESCRICAO, "descricao.in=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllPostosByDescricaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where descricao is not null
        defaultPostoFiltering("descricao.specified=true", "descricao.specified=false");
    }

    @Test
    @Transactional
    void getAllPostosByDescricaoContainsSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where descricao contains
        defaultPostoFiltering("descricao.contains=" + DEFAULT_DESCRICAO, "descricao.contains=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllPostosByDescricaoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where descricao does not contain
        defaultPostoFiltering("descricao.doesNotContain=" + UPDATED_DESCRICAO, "descricao.doesNotContain=" + DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade equals to
        defaultPostoFiltering("prioridade.equals=" + DEFAULT_PRIORIDADE, "prioridade.equals=" + UPDATED_PRIORIDADE);
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade in
        defaultPostoFiltering("prioridade.in=" + DEFAULT_PRIORIDADE + "," + UPDATED_PRIORIDADE, "prioridade.in=" + UPDATED_PRIORIDADE);
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade is not null
        defaultPostoFiltering("prioridade.specified=true", "prioridade.specified=false");
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade is greater than or equal to
        defaultPostoFiltering("prioridade.greaterThanOrEqual=" + DEFAULT_PRIORIDADE, "prioridade.greaterThanOrEqual=" + UPDATED_PRIORIDADE);
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade is less than or equal to
        defaultPostoFiltering("prioridade.lessThanOrEqual=" + DEFAULT_PRIORIDADE, "prioridade.lessThanOrEqual=" + SMALLER_PRIORIDADE);
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade is less than
        defaultPostoFiltering("prioridade.lessThan=" + UPDATED_PRIORIDADE, "prioridade.lessThan=" + DEFAULT_PRIORIDADE);
    }

    @Test
    @Transactional
    void getAllPostosByPrioridadeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where prioridade is greater than
        defaultPostoFiltering("prioridade.greaterThan=" + SMALLER_PRIORIDADE, "prioridade.greaterThan=" + DEFAULT_PRIORIDADE);
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao equals to
        defaultPostoFiltering("orgao.equals=" + DEFAULT_ORGAO, "orgao.equals=" + UPDATED_ORGAO);
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao in
        defaultPostoFiltering("orgao.in=" + DEFAULT_ORGAO + "," + UPDATED_ORGAO, "orgao.in=" + UPDATED_ORGAO);
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao is not null
        defaultPostoFiltering("orgao.specified=true", "orgao.specified=false");
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao is greater than or equal to
        defaultPostoFiltering("orgao.greaterThanOrEqual=" + DEFAULT_ORGAO, "orgao.greaterThanOrEqual=" + UPDATED_ORGAO);
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao is less than or equal to
        defaultPostoFiltering("orgao.lessThanOrEqual=" + DEFAULT_ORGAO, "orgao.lessThanOrEqual=" + SMALLER_ORGAO);
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao is less than
        defaultPostoFiltering("orgao.lessThan=" + UPDATED_ORGAO, "orgao.lessThan=" + DEFAULT_ORGAO);
    }

    @Test
    @Transactional
    void getAllPostosByOrgaoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where orgao is greater than
        defaultPostoFiltering("orgao.greaterThan=" + SMALLER_ORGAO, "orgao.greaterThan=" + DEFAULT_ORGAO);
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes equals to
        defaultPostoFiltering("codSigpes.equals=" + DEFAULT_COD_SIGPES, "codSigpes.equals=" + UPDATED_COD_SIGPES);
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes in
        defaultPostoFiltering("codSigpes.in=" + DEFAULT_COD_SIGPES + "," + UPDATED_COD_SIGPES, "codSigpes.in=" + UPDATED_COD_SIGPES);
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes is not null
        defaultPostoFiltering("codSigpes.specified=true", "codSigpes.specified=false");
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes is greater than or equal to
        defaultPostoFiltering("codSigpes.greaterThanOrEqual=" + DEFAULT_COD_SIGPES, "codSigpes.greaterThanOrEqual=" + UPDATED_COD_SIGPES);
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes is less than or equal to
        defaultPostoFiltering("codSigpes.lessThanOrEqual=" + DEFAULT_COD_SIGPES, "codSigpes.lessThanOrEqual=" + SMALLER_COD_SIGPES);
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes is less than
        defaultPostoFiltering("codSigpes.lessThan=" + UPDATED_COD_SIGPES, "codSigpes.lessThan=" + DEFAULT_COD_SIGPES);
    }

    @Test
    @Transactional
    void getAllPostosByCodSigpesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        // Get all the postoList where codSigpes is greater than
        defaultPostoFiltering("codSigpes.greaterThan=" + SMALLER_COD_SIGPES, "codSigpes.greaterThan=" + DEFAULT_COD_SIGPES);
    }

    private void defaultPostoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPostoShouldBeFound(shouldBeFound);
        defaultPostoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostoShouldBeFound(String filter) throws Exception {
        restPostoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(posto.getId().intValue())))
            .andExpect(jsonPath("$.[*].postoSigla").value(hasItem(DEFAULT_POSTO_SIGLA)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)))
            .andExpect(jsonPath("$.[*].prioridade").value(hasItem(DEFAULT_PRIORIDADE)))
            .andExpect(jsonPath("$.[*].orgao").value(hasItem(DEFAULT_ORGAO)))
            .andExpect(jsonPath("$.[*].codSigpes").value(hasItem(DEFAULT_COD_SIGPES)));

        // Check, that the count call also returns 1
        restPostoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostoShouldNotBeFound(String filter) throws Exception {
        restPostoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPosto() throws Exception {
        // Get the posto
        restPostoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPosto() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        postoSearchRepository.save(posto);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());

        // Update the posto
        Posto updatedPosto = postoRepository.findById(posto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPosto are not directly saved in db
        em.detach(updatedPosto);
        updatedPosto
            .postoSigla(UPDATED_POSTO_SIGLA)
            .descricao(UPDATED_DESCRICAO)
            .prioridade(UPDATED_PRIORIDADE)
            .orgao(UPDATED_ORGAO)
            .codSigpes(UPDATED_COD_SIGPES);
        PostoDTO postoDTO = postoMapper.toDto(updatedPosto);

        restPostoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPostoToMatchAllProperties(updatedPosto);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Posto> postoSearchList = Streamable.of(postoSearchRepository.findAll()).toList();
                Posto testPostoSearch = postoSearchList.get(searchDatabaseSizeAfter - 1);

                assertPostoAllPropertiesEquals(testPostoSearch, updatedPosto);
            });
    }

    @Test
    @Transactional
    void putNonExistingPosto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        posto.setId(longCount.incrementAndGet());

        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, postoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPosto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        posto.setId(longCount.incrementAndGet());

        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(postoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPosto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        posto.setId(longCount.incrementAndGet());

        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostoMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePostoWithPatch() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posto using partial update
        Posto partialUpdatedPosto = new Posto();
        partialUpdatedPosto.setId(posto.getId());

        partialUpdatedPosto.postoSigla(UPDATED_POSTO_SIGLA).descricao(UPDATED_DESCRICAO);

        restPostoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosto.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPosto))
            )
            .andExpect(status().isOk());

        // Validate the Posto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPosto, posto), getPersistedPosto(posto));
    }

    @Test
    @Transactional
    void fullUpdatePostoWithPatch() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the posto using partial update
        Posto partialUpdatedPosto = new Posto();
        partialUpdatedPosto.setId(posto.getId());

        partialUpdatedPosto
            .postoSigla(UPDATED_POSTO_SIGLA)
            .descricao(UPDATED_DESCRICAO)
            .prioridade(UPDATED_PRIORIDADE)
            .orgao(UPDATED_ORGAO)
            .codSigpes(UPDATED_COD_SIGPES);

        restPostoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosto.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPosto))
            )
            .andExpect(status().isOk());

        // Validate the Posto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPostoUpdatableFieldsEquals(partialUpdatedPosto, getPersistedPosto(partialUpdatedPosto));
    }

    @Test
    @Transactional
    void patchNonExistingPosto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        posto.setId(longCount.incrementAndGet());

        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, postoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPosto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        posto.setId(longCount.incrementAndGet());

        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(postoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPosto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        posto.setId(longCount.incrementAndGet());

        // Create the Posto
        PostoDTO postoDTO = postoMapper.toDto(posto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPostoMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(postoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Posto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePosto() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);
        postoRepository.save(posto);
        postoSearchRepository.save(posto);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the posto
        restPostoMockMvc
            .perform(delete(ENTITY_API_URL_ID, posto.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(postoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPosto() throws Exception {
        // Initialize the database
        insertedPosto = postoRepository.saveAndFlush(posto);
        postoSearchRepository.save(posto);

        // Search the posto
        restPostoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + posto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(posto.getId().intValue())))
            .andExpect(jsonPath("$.[*].postoSigla").value(hasItem(DEFAULT_POSTO_SIGLA)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)))
            .andExpect(jsonPath("$.[*].prioridade").value(hasItem(DEFAULT_PRIORIDADE)))
            .andExpect(jsonPath("$.[*].orgao").value(hasItem(DEFAULT_ORGAO)))
            .andExpect(jsonPath("$.[*].codSigpes").value(hasItem(DEFAULT_COD_SIGPES)));
    }

    protected long getRepositoryCount() {
        return postoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Posto getPersistedPosto(Posto posto) {
        return postoRepository.findById(posto.getId()).orElseThrow();
    }

    protected void assertPersistedPostoToMatchAllProperties(Posto expectedPosto) {
        assertPostoAllPropertiesEquals(expectedPosto, getPersistedPosto(expectedPosto));
    }

    protected void assertPersistedPostoToMatchUpdatableProperties(Posto expectedPosto) {
        assertPostoAllUpdatablePropertiesEquals(expectedPosto, getPersistedPosto(expectedPosto));
    }
}
