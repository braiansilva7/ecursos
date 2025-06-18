package com.ecursos.myapp.web.rest;

import static com.ecursos.myapp.domain.CursoAsserts.*;
import static com.ecursos.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecursos.myapp.IntegrationTest;
import com.ecursos.myapp.domain.Curso;
import com.ecursos.myapp.domain.Tipo;
import com.ecursos.myapp.repository.CursoRepository;
import com.ecursos.myapp.repository.search.CursoSearchRepository;
import com.ecursos.myapp.service.CursoService;
import com.ecursos.myapp.service.dto.CursoDTO;
import com.ecursos.myapp.service.mapper.CursoMapper;
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
 * Integration tests for the {@link CursoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CursoResourceIT {

    private static final String DEFAULT_CURSO_NOME = "AAAAAAAAAA";
    private static final String UPDATED_CURSO_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_CURSO_SIGLA = "AAAAAAAAAA";
    private static final String UPDATED_CURSO_SIGLA = "BBBBBBBBBB";

    private static final String DEFAULT_EMPRESA = "AAAAAAAAAA";
    private static final String UPDATED_EMPRESA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cursos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cursos/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CursoRepository cursoRepository;

    @Mock
    private CursoRepository cursoRepositoryMock;

    @Autowired
    private CursoMapper cursoMapper;

    @Mock
    private CursoService cursoServiceMock;

    @Autowired
    private CursoSearchRepository cursoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCursoMockMvc;

    private Curso curso;

    private Curso insertedCurso;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curso createEntity(EntityManager em) {
        Curso curso = new Curso().cursoNome(DEFAULT_CURSO_NOME).cursoSigla(DEFAULT_CURSO_SIGLA).empresa(DEFAULT_EMPRESA);
        // Add required entity
        Tipo tipo;
        if (TestUtil.findAll(em, Tipo.class).isEmpty()) {
            tipo = TipoResourceIT.createEntity();
            em.persist(tipo);
            em.flush();
        } else {
            tipo = TestUtil.findAll(em, Tipo.class).get(0);
        }
        curso.setTipo(tipo);
        return curso;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curso createUpdatedEntity(EntityManager em) {
        Curso updatedCurso = new Curso().cursoNome(UPDATED_CURSO_NOME).cursoSigla(UPDATED_CURSO_SIGLA).empresa(UPDATED_EMPRESA);
        // Add required entity
        Tipo tipo;
        if (TestUtil.findAll(em, Tipo.class).isEmpty()) {
            tipo = TipoResourceIT.createUpdatedEntity();
            em.persist(tipo);
            em.flush();
        } else {
            tipo = TestUtil.findAll(em, Tipo.class).get(0);
        }
        updatedCurso.setTipo(tipo);
        return updatedCurso;
    }

    @BeforeEach
    public void initTest() {
        curso = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCurso != null) {
            cursoRepository.delete(insertedCurso);
            cursoSearchRepository.delete(insertedCurso);
            insertedCurso = null;
        }
    }

    @Test
    @Transactional
    void createCurso() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);
        var returnedCursoDTO = om.readValue(
            restCursoMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cursoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CursoDTO.class
        );

        // Validate the Curso in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCurso = cursoMapper.toEntity(returnedCursoDTO);
        assertCursoUpdatableFieldsEquals(returnedCurso, getPersistedCurso(returnedCurso));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCurso = returnedCurso;
    }

    @Test
    @Transactional
    void createCursoWithExistingId() throws Exception {
        // Create the Curso with an existing ID
        curso.setId(1L);
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCursoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cursoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCursoNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        // set the field null
        curso.setCursoNome(null);

        // Create the Curso, which fails.
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        restCursoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cursoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCursoSiglaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        // set the field null
        curso.setCursoSigla(null);

        // Create the Curso, which fails.
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        restCursoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cursoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmpresaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        // set the field null
        curso.setEmpresa(null);

        // Create the Curso, which fails.
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        restCursoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cursoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCursos() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList
        restCursoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(curso.getId().intValue())))
            .andExpect(jsonPath("$.[*].cursoNome").value(hasItem(DEFAULT_CURSO_NOME)))
            .andExpect(jsonPath("$.[*].cursoSigla").value(hasItem(DEFAULT_CURSO_SIGLA)))
            .andExpect(jsonPath("$.[*].empresa").value(hasItem(DEFAULT_EMPRESA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCursosWithEagerRelationshipsIsEnabled() throws Exception {
        when(cursoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCursoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(cursoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCursosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cursoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCursoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(cursoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCurso() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get the curso
        restCursoMockMvc
            .perform(get(ENTITY_API_URL_ID, curso.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(curso.getId().intValue()))
            .andExpect(jsonPath("$.cursoNome").value(DEFAULT_CURSO_NOME))
            .andExpect(jsonPath("$.cursoSigla").value(DEFAULT_CURSO_SIGLA))
            .andExpect(jsonPath("$.empresa").value(DEFAULT_EMPRESA));
    }

    @Test
    @Transactional
    void getCursosByIdFiltering() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        Long id = curso.getId();

        defaultCursoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCursoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCursoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCursosByCursoNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoNome equals to
        defaultCursoFiltering("cursoNome.equals=" + DEFAULT_CURSO_NOME, "cursoNome.equals=" + UPDATED_CURSO_NOME);
    }

    @Test
    @Transactional
    void getAllCursosByCursoNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoNome in
        defaultCursoFiltering("cursoNome.in=" + DEFAULT_CURSO_NOME + "," + UPDATED_CURSO_NOME, "cursoNome.in=" + UPDATED_CURSO_NOME);
    }

    @Test
    @Transactional
    void getAllCursosByCursoNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoNome is not null
        defaultCursoFiltering("cursoNome.specified=true", "cursoNome.specified=false");
    }

    @Test
    @Transactional
    void getAllCursosByCursoNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoNome contains
        defaultCursoFiltering("cursoNome.contains=" + DEFAULT_CURSO_NOME, "cursoNome.contains=" + UPDATED_CURSO_NOME);
    }

    @Test
    @Transactional
    void getAllCursosByCursoNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoNome does not contain
        defaultCursoFiltering("cursoNome.doesNotContain=" + UPDATED_CURSO_NOME, "cursoNome.doesNotContain=" + DEFAULT_CURSO_NOME);
    }

    @Test
    @Transactional
    void getAllCursosByCursoSiglaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoSigla equals to
        defaultCursoFiltering("cursoSigla.equals=" + DEFAULT_CURSO_SIGLA, "cursoSigla.equals=" + UPDATED_CURSO_SIGLA);
    }

    @Test
    @Transactional
    void getAllCursosByCursoSiglaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoSigla in
        defaultCursoFiltering("cursoSigla.in=" + DEFAULT_CURSO_SIGLA + "," + UPDATED_CURSO_SIGLA, "cursoSigla.in=" + UPDATED_CURSO_SIGLA);
    }

    @Test
    @Transactional
    void getAllCursosByCursoSiglaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoSigla is not null
        defaultCursoFiltering("cursoSigla.specified=true", "cursoSigla.specified=false");
    }

    @Test
    @Transactional
    void getAllCursosByCursoSiglaContainsSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoSigla contains
        defaultCursoFiltering("cursoSigla.contains=" + DEFAULT_CURSO_SIGLA, "cursoSigla.contains=" + UPDATED_CURSO_SIGLA);
    }

    @Test
    @Transactional
    void getAllCursosByCursoSiglaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where cursoSigla does not contain
        defaultCursoFiltering("cursoSigla.doesNotContain=" + UPDATED_CURSO_SIGLA, "cursoSigla.doesNotContain=" + DEFAULT_CURSO_SIGLA);
    }

    @Test
    @Transactional
    void getAllCursosByEmpresaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where empresa equals to
        defaultCursoFiltering("empresa.equals=" + DEFAULT_EMPRESA, "empresa.equals=" + UPDATED_EMPRESA);
    }

    @Test
    @Transactional
    void getAllCursosByEmpresaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where empresa in
        defaultCursoFiltering("empresa.in=" + DEFAULT_EMPRESA + "," + UPDATED_EMPRESA, "empresa.in=" + UPDATED_EMPRESA);
    }

    @Test
    @Transactional
    void getAllCursosByEmpresaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where empresa is not null
        defaultCursoFiltering("empresa.specified=true", "empresa.specified=false");
    }

    @Test
    @Transactional
    void getAllCursosByEmpresaContainsSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where empresa contains
        defaultCursoFiltering("empresa.contains=" + DEFAULT_EMPRESA, "empresa.contains=" + UPDATED_EMPRESA);
    }

    @Test
    @Transactional
    void getAllCursosByEmpresaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        // Get all the cursoList where empresa does not contain
        defaultCursoFiltering("empresa.doesNotContain=" + UPDATED_EMPRESA, "empresa.doesNotContain=" + DEFAULT_EMPRESA);
    }

    @Test
    @Transactional
    void getAllCursosByTipoIsEqualToSomething() throws Exception {
        Tipo tipo;
        if (TestUtil.findAll(em, Tipo.class).isEmpty()) {
            cursoRepository.saveAndFlush(curso);
            tipo = TipoResourceIT.createEntity();
        } else {
            tipo = TestUtil.findAll(em, Tipo.class).get(0);
        }
        em.persist(tipo);
        em.flush();
        curso.setTipo(tipo);
        cursoRepository.saveAndFlush(curso);
        Long tipoId = tipo.getId();
        // Get all the cursoList where tipo equals to tipoId
        defaultCursoShouldBeFound("tipoId.equals=" + tipoId);

        // Get all the cursoList where tipo equals to (tipoId + 1)
        defaultCursoShouldNotBeFound("tipoId.equals=" + (tipoId + 1));
    }

    private void defaultCursoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCursoShouldBeFound(shouldBeFound);
        defaultCursoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCursoShouldBeFound(String filter) throws Exception {
        restCursoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(curso.getId().intValue())))
            .andExpect(jsonPath("$.[*].cursoNome").value(hasItem(DEFAULT_CURSO_NOME)))
            .andExpect(jsonPath("$.[*].cursoSigla").value(hasItem(DEFAULT_CURSO_SIGLA)))
            .andExpect(jsonPath("$.[*].empresa").value(hasItem(DEFAULT_EMPRESA)));

        // Check, that the count call also returns 1
        restCursoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCursoShouldNotBeFound(String filter) throws Exception {
        restCursoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCursoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCurso() throws Exception {
        // Get the curso
        restCursoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCurso() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        cursoSearchRepository.save(curso);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());

        // Update the curso
        Curso updatedCurso = cursoRepository.findById(curso.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCurso are not directly saved in db
        em.detach(updatedCurso);
        updatedCurso.cursoNome(UPDATED_CURSO_NOME).cursoSigla(UPDATED_CURSO_SIGLA).empresa(UPDATED_EMPRESA);
        CursoDTO cursoDTO = cursoMapper.toDto(updatedCurso);

        restCursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cursoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cursoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCursoToMatchAllProperties(updatedCurso);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Curso> cursoSearchList = Streamable.of(cursoSearchRepository.findAll()).toList();
                Curso testCursoSearch = cursoSearchList.get(searchDatabaseSizeAfter - 1);

                assertCursoAllPropertiesEquals(testCursoSearch, updatedCurso);
            });
    }

    @Test
    @Transactional
    void putNonExistingCurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        curso.setId(longCount.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cursoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cursoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        curso.setId(longCount.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cursoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        curso.setId(longCount.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cursoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCursoWithPatch() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the curso using partial update
        Curso partialUpdatedCurso = new Curso();
        partialUpdatedCurso.setId(curso.getId());

        partialUpdatedCurso.cursoSigla(UPDATED_CURSO_SIGLA);

        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCurso.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCurso))
            )
            .andExpect(status().isOk());

        // Validate the Curso in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCursoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCurso, curso), getPersistedCurso(curso));
    }

    @Test
    @Transactional
    void fullUpdateCursoWithPatch() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the curso using partial update
        Curso partialUpdatedCurso = new Curso();
        partialUpdatedCurso.setId(curso.getId());

        partialUpdatedCurso.cursoNome(UPDATED_CURSO_NOME).cursoSigla(UPDATED_CURSO_SIGLA).empresa(UPDATED_EMPRESA);

        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCurso.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCurso))
            )
            .andExpect(status().isOk());

        // Validate the Curso in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCursoUpdatableFieldsEquals(partialUpdatedCurso, getPersistedCurso(partialUpdatedCurso));
    }

    @Test
    @Transactional
    void patchNonExistingCurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        curso.setId(longCount.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cursoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cursoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        curso.setId(longCount.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cursoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        curso.setId(longCount.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cursoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Curso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCurso() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);
        cursoRepository.save(curso);
        cursoSearchRepository.save(curso);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the curso
        restCursoMockMvc
            .perform(delete(ENTITY_API_URL_ID, curso.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cursoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCurso() throws Exception {
        // Initialize the database
        insertedCurso = cursoRepository.saveAndFlush(curso);
        cursoSearchRepository.save(curso);

        // Search the curso
        restCursoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + curso.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(curso.getId().intValue())))
            .andExpect(jsonPath("$.[*].cursoNome").value(hasItem(DEFAULT_CURSO_NOME)))
            .andExpect(jsonPath("$.[*].cursoSigla").value(hasItem(DEFAULT_CURSO_SIGLA)))
            .andExpect(jsonPath("$.[*].empresa").value(hasItem(DEFAULT_EMPRESA)));
    }

    protected long getRepositoryCount() {
        return cursoRepository.count();
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

    protected Curso getPersistedCurso(Curso curso) {
        return cursoRepository.findById(curso.getId()).orElseThrow();
    }

    protected void assertPersistedCursoToMatchAllProperties(Curso expectedCurso) {
        assertCursoAllPropertiesEquals(expectedCurso, getPersistedCurso(expectedCurso));
    }

    protected void assertPersistedCursoToMatchUpdatableProperties(Curso expectedCurso) {
        assertCursoAllUpdatablePropertiesEquals(expectedCurso, getPersistedCurso(expectedCurso));
    }
}
