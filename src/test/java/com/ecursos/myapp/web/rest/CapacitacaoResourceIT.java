package com.ecursos.myapp.web.rest;

import static com.ecursos.myapp.domain.CapacitacaoAsserts.*;
import static com.ecursos.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecursos.myapp.IntegrationTest;
import com.ecursos.myapp.domain.Capacitacao;
import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.domain.Turma;
import com.ecursos.myapp.domain.enumeration.StatusEnum;
import com.ecursos.myapp.repository.CapacitacaoRepository;
import com.ecursos.myapp.repository.search.CapacitacaoSearchRepository;
import com.ecursos.myapp.service.CapacitacaoService;
import com.ecursos.myapp.service.dto.CapacitacaoDTO;
import com.ecursos.myapp.service.mapper.CapacitacaoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CapacitacaoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CapacitacaoResourceIT {

    private static final StatusEnum DEFAULT_CAPACITACAO_STATUS = StatusEnum.CONCLUIDO;
    private static final StatusEnum UPDATED_CAPACITACAO_STATUS = StatusEnum.APROVADO;

    private static final String DEFAULT_SIGPES = "AAAAAAAAAA";
    private static final String UPDATED_SIGPES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/capacitacaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/capacitacaos/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CapacitacaoRepository capacitacaoRepository;

    @Mock
    private CapacitacaoRepository capacitacaoRepositoryMock;

    @Autowired
    private CapacitacaoMapper capacitacaoMapper;

    @Mock
    private CapacitacaoService capacitacaoServiceMock;

    @Autowired
    private CapacitacaoSearchRepository capacitacaoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCapacitacaoMockMvc;

    private Capacitacao capacitacao;

    private Capacitacao insertedCapacitacao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Capacitacao createEntity(EntityManager em) {
        Capacitacao capacitacao = new Capacitacao().capacitacaoStatus(DEFAULT_CAPACITACAO_STATUS).sigpes(DEFAULT_SIGPES);
        // Add required entity
        Turma turma;
        if (TestUtil.findAll(em, Turma.class).isEmpty()) {
            turma = TurmaResourceIT.createEntity(em);
            em.persist(turma);
            em.flush();
        } else {
            turma = TestUtil.findAll(em, Turma.class).get(0);
        }
        capacitacao.setTurma(turma);
        return capacitacao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Capacitacao createUpdatedEntity(EntityManager em) {
        Capacitacao updatedCapacitacao = new Capacitacao().capacitacaoStatus(UPDATED_CAPACITACAO_STATUS).sigpes(UPDATED_SIGPES);
        // Add required entity
        Turma turma;
        if (TestUtil.findAll(em, Turma.class).isEmpty()) {
            turma = TurmaResourceIT.createUpdatedEntity(em);
            em.persist(turma);
            em.flush();
        } else {
            turma = TestUtil.findAll(em, Turma.class).get(0);
        }
        updatedCapacitacao.setTurma(turma);
        return updatedCapacitacao;
    }

    @BeforeEach
    public void initTest() {
        capacitacao = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCapacitacao != null) {
            capacitacaoRepository.delete(insertedCapacitacao);
            capacitacaoSearchRepository.delete(insertedCapacitacao);
            insertedCapacitacao = null;
        }
    }

    @Test
    @Transactional
    void createCapacitacao() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);
        var returnedCapacitacaoDTO = om.readValue(
            restCapacitacaoMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capacitacaoDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CapacitacaoDTO.class
        );

        // Validate the Capacitacao in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCapacitacao = capacitacaoMapper.toEntity(returnedCapacitacaoDTO);
        assertCapacitacaoUpdatableFieldsEquals(returnedCapacitacao, getPersistedCapacitacao(returnedCapacitacao));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCapacitacao = returnedCapacitacao;
    }

    @Test
    @Transactional
    void createCapacitacaoWithExistingId() throws Exception {
        // Create the Capacitacao with an existing ID
        capacitacao.setId(1L);
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapacitacaoMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCapacitacaoStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        // set the field null
        capacitacao.setCapacitacaoStatus(null);

        // Create the Capacitacao, which fails.
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        restCapacitacaoMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCapacitacaos() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList
        restCapacitacaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capacitacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].capacitacaoStatus").value(hasItem(DEFAULT_CAPACITACAO_STATUS.toString())))
            .andExpect(jsonPath("$.[*].sigpes").value(hasItem(DEFAULT_SIGPES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCapacitacaosWithEagerRelationshipsIsEnabled() throws Exception {
        when(capacitacaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCapacitacaoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(capacitacaoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCapacitacaosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(capacitacaoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCapacitacaoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(capacitacaoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCapacitacao() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get the capacitacao
        restCapacitacaoMockMvc
            .perform(get(ENTITY_API_URL_ID, capacitacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(capacitacao.getId().intValue()))
            .andExpect(jsonPath("$.capacitacaoStatus").value(DEFAULT_CAPACITACAO_STATUS.toString()))
            .andExpect(jsonPath("$.sigpes").value(DEFAULT_SIGPES));
    }

    @Test
    @Transactional
    void getCapacitacaosByIdFiltering() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        Long id = capacitacao.getId();

        defaultCapacitacaoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCapacitacaoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCapacitacaoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCapacitacaosByCapacitacaoStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where capacitacaoStatus equals to
        defaultCapacitacaoFiltering(
            "capacitacaoStatus.equals=" + DEFAULT_CAPACITACAO_STATUS,
            "capacitacaoStatus.equals=" + UPDATED_CAPACITACAO_STATUS
        );
    }

    @Test
    @Transactional
    void getAllCapacitacaosByCapacitacaoStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where capacitacaoStatus in
        defaultCapacitacaoFiltering(
            "capacitacaoStatus.in=" + DEFAULT_CAPACITACAO_STATUS + "," + UPDATED_CAPACITACAO_STATUS,
            "capacitacaoStatus.in=" + UPDATED_CAPACITACAO_STATUS
        );
    }

    @Test
    @Transactional
    void getAllCapacitacaosByCapacitacaoStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where capacitacaoStatus is not null
        defaultCapacitacaoFiltering("capacitacaoStatus.specified=true", "capacitacaoStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllCapacitacaosBySigpesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where sigpes equals to
        defaultCapacitacaoFiltering("sigpes.equals=" + DEFAULT_SIGPES, "sigpes.equals=" + UPDATED_SIGPES);
    }

    @Test
    @Transactional
    void getAllCapacitacaosBySigpesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where sigpes in
        defaultCapacitacaoFiltering("sigpes.in=" + DEFAULT_SIGPES + "," + UPDATED_SIGPES, "sigpes.in=" + UPDATED_SIGPES);
    }

    @Test
    @Transactional
    void getAllCapacitacaosBySigpesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where sigpes is not null
        defaultCapacitacaoFiltering("sigpes.specified=true", "sigpes.specified=false");
    }

    @Test
    @Transactional
    void getAllCapacitacaosBySigpesContainsSomething() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where sigpes contains
        defaultCapacitacaoFiltering("sigpes.contains=" + DEFAULT_SIGPES, "sigpes.contains=" + UPDATED_SIGPES);
    }

    @Test
    @Transactional
    void getAllCapacitacaosBySigpesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        // Get all the capacitacaoList where sigpes does not contain
        defaultCapacitacaoFiltering("sigpes.doesNotContain=" + UPDATED_SIGPES, "sigpes.doesNotContain=" + DEFAULT_SIGPES);
    }

    @Test
    @Transactional
    void getAllCapacitacaosByMilitarIsEqualToSomething() throws Exception {
        Militar militar;
        if (TestUtil.findAll(em, Militar.class).isEmpty()) {
            capacitacaoRepository.saveAndFlush(capacitacao);
            militar = MilitarResourceIT.createEntity(em);
        } else {
            militar = TestUtil.findAll(em, Militar.class).get(0);
        }
        em.persist(militar);
        em.flush();
        capacitacao.setMilitar(militar);
        capacitacaoRepository.saveAndFlush(capacitacao);
        Long militarId = militar.getId();
        // Get all the capacitacaoList where militar equals to militarId
        defaultCapacitacaoShouldBeFound("militarId.equals=" + militarId);

        // Get all the capacitacaoList where militar equals to (militarId + 1)
        defaultCapacitacaoShouldNotBeFound("militarId.equals=" + (militarId + 1));
    }

    @Test
    @Transactional
    void getAllCapacitacaosByTurmaIsEqualToSomething() throws Exception {
        Turma turma;
        if (TestUtil.findAll(em, Turma.class).isEmpty()) {
            capacitacaoRepository.saveAndFlush(capacitacao);
            turma = TurmaResourceIT.createEntity(em);
        } else {
            turma = TestUtil.findAll(em, Turma.class).get(0);
        }
        em.persist(turma);
        em.flush();
        capacitacao.setTurma(turma);
        capacitacaoRepository.saveAndFlush(capacitacao);
        Long turmaId = turma.getId();
        // Get all the capacitacaoList where turma equals to turmaId
        defaultCapacitacaoShouldBeFound("turmaId.equals=" + turmaId);

        // Get all the capacitacaoList where turma equals to (turmaId + 1)
        defaultCapacitacaoShouldNotBeFound("turmaId.equals=" + (turmaId + 1));
    }

    private void defaultCapacitacaoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCapacitacaoShouldBeFound(shouldBeFound);
        defaultCapacitacaoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCapacitacaoShouldBeFound(String filter) throws Exception {
        restCapacitacaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capacitacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].capacitacaoStatus").value(hasItem(DEFAULT_CAPACITACAO_STATUS.toString())))
            .andExpect(jsonPath("$.[*].sigpes").value(hasItem(DEFAULT_SIGPES)));

        // Check, that the count call also returns 1
        restCapacitacaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCapacitacaoShouldNotBeFound(String filter) throws Exception {
        restCapacitacaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCapacitacaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCapacitacao() throws Exception {
        // Get the capacitacao
        restCapacitacaoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCapacitacao() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        capacitacaoSearchRepository.save(capacitacao);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());

        // Update the capacitacao
        Capacitacao updatedCapacitacao = capacitacaoRepository.findById(capacitacao.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCapacitacao are not directly saved in db
        em.detach(updatedCapacitacao);
        updatedCapacitacao.capacitacaoStatus(UPDATED_CAPACITACAO_STATUS).sigpes(UPDATED_SIGPES);
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(updatedCapacitacao);

        restCapacitacaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, capacitacaoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCapacitacaoToMatchAllProperties(updatedCapacitacao);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Capacitacao> capacitacaoSearchList = Streamable.of(capacitacaoSearchRepository.findAll()).toList();
                Capacitacao testCapacitacaoSearch = capacitacaoSearchList.get(searchDatabaseSizeAfter - 1);

                assertCapacitacaoAllPropertiesEquals(testCapacitacaoSearch, updatedCapacitacao);
            });
    }

    @Test
    @Transactional
    void putNonExistingCapacitacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        capacitacao.setId(longCount.incrementAndGet());

        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapacitacaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, capacitacaoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCapacitacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        capacitacao.setId(longCount.incrementAndGet());

        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapacitacaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCapacitacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        capacitacao.setId(longCount.incrementAndGet());

        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapacitacaoMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(capacitacaoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCapacitacaoWithPatch() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the capacitacao using partial update
        Capacitacao partialUpdatedCapacitacao = new Capacitacao();
        partialUpdatedCapacitacao.setId(capacitacao.getId());

        partialUpdatedCapacitacao.sigpes(UPDATED_SIGPES);

        restCapacitacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapacitacao.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCapacitacao))
            )
            .andExpect(status().isOk());

        // Validate the Capacitacao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCapacitacaoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCapacitacao, capacitacao),
            getPersistedCapacitacao(capacitacao)
        );
    }

    @Test
    @Transactional
    void fullUpdateCapacitacaoWithPatch() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the capacitacao using partial update
        Capacitacao partialUpdatedCapacitacao = new Capacitacao();
        partialUpdatedCapacitacao.setId(capacitacao.getId());

        partialUpdatedCapacitacao.capacitacaoStatus(UPDATED_CAPACITACAO_STATUS).sigpes(UPDATED_SIGPES);

        restCapacitacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCapacitacao.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCapacitacao))
            )
            .andExpect(status().isOk());

        // Validate the Capacitacao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCapacitacaoUpdatableFieldsEquals(partialUpdatedCapacitacao, getPersistedCapacitacao(partialUpdatedCapacitacao));
    }

    @Test
    @Transactional
    void patchNonExistingCapacitacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        capacitacao.setId(longCount.incrementAndGet());

        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapacitacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, capacitacaoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCapacitacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        capacitacao.setId(longCount.incrementAndGet());

        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapacitacaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCapacitacao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        capacitacao.setId(longCount.incrementAndGet());

        // Create the Capacitacao
        CapacitacaoDTO capacitacaoDTO = capacitacaoMapper.toDto(capacitacao);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCapacitacaoMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(capacitacaoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Capacitacao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCapacitacao() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);
        capacitacaoRepository.save(capacitacao);
        capacitacaoSearchRepository.save(capacitacao);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the capacitacao
        restCapacitacaoMockMvc
            .perform(delete(ENTITY_API_URL_ID, capacitacao.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(capacitacaoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCapacitacao() throws Exception {
        // Initialize the database
        insertedCapacitacao = capacitacaoRepository.saveAndFlush(capacitacao);
        capacitacaoSearchRepository.save(capacitacao);

        // Search the capacitacao
        restCapacitacaoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + capacitacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capacitacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].capacitacaoStatus").value(hasItem(DEFAULT_CAPACITACAO_STATUS.toString())))
            .andExpect(jsonPath("$.[*].sigpes").value(hasItem(DEFAULT_SIGPES)));
    }

    protected long getRepositoryCount() {
        return capacitacaoRepository.count();
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

    protected Capacitacao getPersistedCapacitacao(Capacitacao capacitacao) {
        return capacitacaoRepository.findById(capacitacao.getId()).orElseThrow();
    }

    protected void assertPersistedCapacitacaoToMatchAllProperties(Capacitacao expectedCapacitacao) {
        assertCapacitacaoAllPropertiesEquals(expectedCapacitacao, getPersistedCapacitacao(expectedCapacitacao));
    }

    protected void assertPersistedCapacitacaoToMatchUpdatableProperties(Capacitacao expectedCapacitacao) {
        assertCapacitacaoAllUpdatablePropertiesEquals(expectedCapacitacao, getPersistedCapacitacao(expectedCapacitacao));
    }
}
