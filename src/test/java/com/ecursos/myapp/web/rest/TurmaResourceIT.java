package com.ecursos.myapp.web.rest;

import static com.ecursos.myapp.domain.TurmaAsserts.*;
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
import com.ecursos.myapp.domain.Turma;
import com.ecursos.myapp.domain.enumeration.ModalidadeEnum;
import com.ecursos.myapp.domain.enumeration.StatusCursoEnum;
import com.ecursos.myapp.repository.TurmaRepository;
import com.ecursos.myapp.repository.search.TurmaSearchRepository;
import com.ecursos.myapp.service.TurmaService;
import com.ecursos.myapp.service.dto.TurmaDTO;
import com.ecursos.myapp.service.mapper.TurmaMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TurmaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TurmaResourceIT {

    private static final LocalDate DEFAULT_INICIO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INICIO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_INICIO = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_TERMINO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TERMINO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TERMINO = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_ANO = 1;
    private static final Integer UPDATED_ANO = 2;
    private static final Integer SMALLER_ANO = 1 - 1;

    private static final StatusCursoEnum DEFAULT_STATUS_CURSO = StatusCursoEnum.AGUARDANDO_APROVACAO;
    private static final StatusCursoEnum UPDATED_STATUS_CURSO = StatusCursoEnum.AGUARDANDO_BCA_APROVACAO;

    private static final ModalidadeEnum DEFAULT_MODALIDADE = ModalidadeEnum.PRESENCIAL;
    private static final ModalidadeEnum UPDATED_MODALIDADE = ModalidadeEnum.ONLINE;

    private static final String DEFAULT_QTD_VAGAS = "AAAAAAAAAA";
    private static final String UPDATED_QTD_VAGAS = "BBBBBBBBBB";

    private static final String DEFAULT_NUMERO_BCA = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_BCA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/turmas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/turmas/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TurmaRepository turmaRepository;

    @Mock
    private TurmaRepository turmaRepositoryMock;

    @Autowired
    private TurmaMapper turmaMapper;

    @Mock
    private TurmaService turmaServiceMock;

    @Autowired
    private TurmaSearchRepository turmaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTurmaMockMvc;

    private Turma turma;

    private Turma insertedTurma;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Turma createEntity(EntityManager em) {
        Turma turma = new Turma()
            .inicio(DEFAULT_INICIO)
            .termino(DEFAULT_TERMINO)
            .ano(DEFAULT_ANO)
            .statusCurso(DEFAULT_STATUS_CURSO)
            .modalidade(DEFAULT_MODALIDADE)
            .qtdVagas(DEFAULT_QTD_VAGAS)
            .numeroBca(DEFAULT_NUMERO_BCA);
        // Add required entity
        Curso curso;
        if (TestUtil.findAll(em, Curso.class).isEmpty()) {
            curso = CursoResourceIT.createEntity(em);
            em.persist(curso);
            em.flush();
        } else {
            curso = TestUtil.findAll(em, Curso.class).get(0);
        }
        turma.setCurso(curso);
        return turma;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Turma createUpdatedEntity(EntityManager em) {
        Turma updatedTurma = new Turma()
            .inicio(UPDATED_INICIO)
            .termino(UPDATED_TERMINO)
            .ano(UPDATED_ANO)
            .statusCurso(UPDATED_STATUS_CURSO)
            .modalidade(UPDATED_MODALIDADE)
            .qtdVagas(UPDATED_QTD_VAGAS)
            .numeroBca(UPDATED_NUMERO_BCA);
        // Add required entity
        Curso curso;
        if (TestUtil.findAll(em, Curso.class).isEmpty()) {
            curso = CursoResourceIT.createUpdatedEntity(em);
            em.persist(curso);
            em.flush();
        } else {
            curso = TestUtil.findAll(em, Curso.class).get(0);
        }
        updatedTurma.setCurso(curso);
        return updatedTurma;
    }

    @BeforeEach
    public void initTest() {
        turma = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTurma != null) {
            turmaRepository.delete(insertedTurma);
            turmaSearchRepository.delete(insertedTurma);
            insertedTurma = null;
        }
    }

    @Test
    @Transactional
    void createTurma() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);
        var returnedTurmaDTO = om.readValue(
            restTurmaMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turmaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TurmaDTO.class
        );

        // Validate the Turma in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTurma = turmaMapper.toEntity(returnedTurmaDTO);
        assertTurmaUpdatableFieldsEquals(returnedTurma, getPersistedTurma(returnedTurma));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTurma = returnedTurma;
    }

    @Test
    @Transactional
    void createTurmaWithExistingId() throws Exception {
        // Create the Turma with an existing ID
        turma.setId(1L);
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTurmaMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turmaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusCursoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        // set the field null
        turma.setStatusCurso(null);

        // Create the Turma, which fails.
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        restTurmaMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turmaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkModalidadeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        // set the field null
        turma.setModalidade(null);

        // Create the Turma, which fails.
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        restTurmaMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turmaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTurmas() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList
        restTurmaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(turma.getId().intValue())))
            .andExpect(jsonPath("$.[*].inicio").value(hasItem(DEFAULT_INICIO.toString())))
            .andExpect(jsonPath("$.[*].termino").value(hasItem(DEFAULT_TERMINO.toString())))
            .andExpect(jsonPath("$.[*].ano").value(hasItem(DEFAULT_ANO)))
            .andExpect(jsonPath("$.[*].statusCurso").value(hasItem(DEFAULT_STATUS_CURSO.toString())))
            .andExpect(jsonPath("$.[*].modalidade").value(hasItem(DEFAULT_MODALIDADE.toString())))
            .andExpect(jsonPath("$.[*].qtdVagas").value(hasItem(DEFAULT_QTD_VAGAS)))
            .andExpect(jsonPath("$.[*].numeroBca").value(hasItem(DEFAULT_NUMERO_BCA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTurmasWithEagerRelationshipsIsEnabled() throws Exception {
        when(turmaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTurmaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(turmaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTurmasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(turmaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTurmaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(turmaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTurma() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get the turma
        restTurmaMockMvc
            .perform(get(ENTITY_API_URL_ID, turma.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(turma.getId().intValue()))
            .andExpect(jsonPath("$.inicio").value(DEFAULT_INICIO.toString()))
            .andExpect(jsonPath("$.termino").value(DEFAULT_TERMINO.toString()))
            .andExpect(jsonPath("$.ano").value(DEFAULT_ANO))
            .andExpect(jsonPath("$.statusCurso").value(DEFAULT_STATUS_CURSO.toString()))
            .andExpect(jsonPath("$.modalidade").value(DEFAULT_MODALIDADE.toString()))
            .andExpect(jsonPath("$.qtdVagas").value(DEFAULT_QTD_VAGAS))
            .andExpect(jsonPath("$.numeroBca").value(DEFAULT_NUMERO_BCA));
    }

    @Test
    @Transactional
    void getTurmasByIdFiltering() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        Long id = turma.getId();

        defaultTurmaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTurmaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTurmaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio equals to
        defaultTurmaFiltering("inicio.equals=" + DEFAULT_INICIO, "inicio.equals=" + UPDATED_INICIO);
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio in
        defaultTurmaFiltering("inicio.in=" + DEFAULT_INICIO + "," + UPDATED_INICIO, "inicio.in=" + UPDATED_INICIO);
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio is not null
        defaultTurmaFiltering("inicio.specified=true", "inicio.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio is greater than or equal to
        defaultTurmaFiltering("inicio.greaterThanOrEqual=" + DEFAULT_INICIO, "inicio.greaterThanOrEqual=" + UPDATED_INICIO);
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio is less than or equal to
        defaultTurmaFiltering("inicio.lessThanOrEqual=" + DEFAULT_INICIO, "inicio.lessThanOrEqual=" + SMALLER_INICIO);
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio is less than
        defaultTurmaFiltering("inicio.lessThan=" + UPDATED_INICIO, "inicio.lessThan=" + DEFAULT_INICIO);
    }

    @Test
    @Transactional
    void getAllTurmasByInicioIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where inicio is greater than
        defaultTurmaFiltering("inicio.greaterThan=" + SMALLER_INICIO, "inicio.greaterThan=" + DEFAULT_INICIO);
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino equals to
        defaultTurmaFiltering("termino.equals=" + DEFAULT_TERMINO, "termino.equals=" + UPDATED_TERMINO);
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino in
        defaultTurmaFiltering("termino.in=" + DEFAULT_TERMINO + "," + UPDATED_TERMINO, "termino.in=" + UPDATED_TERMINO);
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino is not null
        defaultTurmaFiltering("termino.specified=true", "termino.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino is greater than or equal to
        defaultTurmaFiltering("termino.greaterThanOrEqual=" + DEFAULT_TERMINO, "termino.greaterThanOrEqual=" + UPDATED_TERMINO);
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino is less than or equal to
        defaultTurmaFiltering("termino.lessThanOrEqual=" + DEFAULT_TERMINO, "termino.lessThanOrEqual=" + SMALLER_TERMINO);
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino is less than
        defaultTurmaFiltering("termino.lessThan=" + UPDATED_TERMINO, "termino.lessThan=" + DEFAULT_TERMINO);
    }

    @Test
    @Transactional
    void getAllTurmasByTerminoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where termino is greater than
        defaultTurmaFiltering("termino.greaterThan=" + SMALLER_TERMINO, "termino.greaterThan=" + DEFAULT_TERMINO);
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano equals to
        defaultTurmaFiltering("ano.equals=" + DEFAULT_ANO, "ano.equals=" + UPDATED_ANO);
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano in
        defaultTurmaFiltering("ano.in=" + DEFAULT_ANO + "," + UPDATED_ANO, "ano.in=" + UPDATED_ANO);
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano is not null
        defaultTurmaFiltering("ano.specified=true", "ano.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano is greater than or equal to
        defaultTurmaFiltering("ano.greaterThanOrEqual=" + DEFAULT_ANO, "ano.greaterThanOrEqual=" + UPDATED_ANO);
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano is less than or equal to
        defaultTurmaFiltering("ano.lessThanOrEqual=" + DEFAULT_ANO, "ano.lessThanOrEqual=" + SMALLER_ANO);
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano is less than
        defaultTurmaFiltering("ano.lessThan=" + UPDATED_ANO, "ano.lessThan=" + DEFAULT_ANO);
    }

    @Test
    @Transactional
    void getAllTurmasByAnoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where ano is greater than
        defaultTurmaFiltering("ano.greaterThan=" + SMALLER_ANO, "ano.greaterThan=" + DEFAULT_ANO);
    }

    @Test
    @Transactional
    void getAllTurmasByStatusCursoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where statusCurso equals to
        defaultTurmaFiltering("statusCurso.equals=" + DEFAULT_STATUS_CURSO, "statusCurso.equals=" + UPDATED_STATUS_CURSO);
    }

    @Test
    @Transactional
    void getAllTurmasByStatusCursoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where statusCurso in
        defaultTurmaFiltering(
            "statusCurso.in=" + DEFAULT_STATUS_CURSO + "," + UPDATED_STATUS_CURSO,
            "statusCurso.in=" + UPDATED_STATUS_CURSO
        );
    }

    @Test
    @Transactional
    void getAllTurmasByStatusCursoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where statusCurso is not null
        defaultTurmaFiltering("statusCurso.specified=true", "statusCurso.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByModalidadeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where modalidade equals to
        defaultTurmaFiltering("modalidade.equals=" + DEFAULT_MODALIDADE, "modalidade.equals=" + UPDATED_MODALIDADE);
    }

    @Test
    @Transactional
    void getAllTurmasByModalidadeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where modalidade in
        defaultTurmaFiltering("modalidade.in=" + DEFAULT_MODALIDADE + "," + UPDATED_MODALIDADE, "modalidade.in=" + UPDATED_MODALIDADE);
    }

    @Test
    @Transactional
    void getAllTurmasByModalidadeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where modalidade is not null
        defaultTurmaFiltering("modalidade.specified=true", "modalidade.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByQtdVagasIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where qtdVagas equals to
        defaultTurmaFiltering("qtdVagas.equals=" + DEFAULT_QTD_VAGAS, "qtdVagas.equals=" + UPDATED_QTD_VAGAS);
    }

    @Test
    @Transactional
    void getAllTurmasByQtdVagasIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where qtdVagas in
        defaultTurmaFiltering("qtdVagas.in=" + DEFAULT_QTD_VAGAS + "," + UPDATED_QTD_VAGAS, "qtdVagas.in=" + UPDATED_QTD_VAGAS);
    }

    @Test
    @Transactional
    void getAllTurmasByQtdVagasIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where qtdVagas is not null
        defaultTurmaFiltering("qtdVagas.specified=true", "qtdVagas.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByQtdVagasContainsSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where qtdVagas contains
        defaultTurmaFiltering("qtdVagas.contains=" + DEFAULT_QTD_VAGAS, "qtdVagas.contains=" + UPDATED_QTD_VAGAS);
    }

    @Test
    @Transactional
    void getAllTurmasByQtdVagasNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where qtdVagas does not contain
        defaultTurmaFiltering("qtdVagas.doesNotContain=" + UPDATED_QTD_VAGAS, "qtdVagas.doesNotContain=" + DEFAULT_QTD_VAGAS);
    }

    @Test
    @Transactional
    void getAllTurmasByNumeroBcaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where numeroBca equals to
        defaultTurmaFiltering("numeroBca.equals=" + DEFAULT_NUMERO_BCA, "numeroBca.equals=" + UPDATED_NUMERO_BCA);
    }

    @Test
    @Transactional
    void getAllTurmasByNumeroBcaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where numeroBca in
        defaultTurmaFiltering("numeroBca.in=" + DEFAULT_NUMERO_BCA + "," + UPDATED_NUMERO_BCA, "numeroBca.in=" + UPDATED_NUMERO_BCA);
    }

    @Test
    @Transactional
    void getAllTurmasByNumeroBcaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where numeroBca is not null
        defaultTurmaFiltering("numeroBca.specified=true", "numeroBca.specified=false");
    }

    @Test
    @Transactional
    void getAllTurmasByNumeroBcaContainsSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where numeroBca contains
        defaultTurmaFiltering("numeroBca.contains=" + DEFAULT_NUMERO_BCA, "numeroBca.contains=" + UPDATED_NUMERO_BCA);
    }

    @Test
    @Transactional
    void getAllTurmasByNumeroBcaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        // Get all the turmaList where numeroBca does not contain
        defaultTurmaFiltering("numeroBca.doesNotContain=" + UPDATED_NUMERO_BCA, "numeroBca.doesNotContain=" + DEFAULT_NUMERO_BCA);
    }

    @Test
    @Transactional
    void getAllTurmasByCursoIsEqualToSomething() throws Exception {
        Curso curso;
        if (TestUtil.findAll(em, Curso.class).isEmpty()) {
            turmaRepository.saveAndFlush(turma);
            curso = CursoResourceIT.createEntity(em);
        } else {
            curso = TestUtil.findAll(em, Curso.class).get(0);
        }
        em.persist(curso);
        em.flush();
        turma.setCurso(curso);
        turmaRepository.saveAndFlush(turma);
        Long cursoId = curso.getId();
        // Get all the turmaList where curso equals to cursoId
        defaultTurmaShouldBeFound("cursoId.equals=" + cursoId);

        // Get all the turmaList where curso equals to (cursoId + 1)
        defaultTurmaShouldNotBeFound("cursoId.equals=" + (cursoId + 1));
    }

    private void defaultTurmaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTurmaShouldBeFound(shouldBeFound);
        defaultTurmaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTurmaShouldBeFound(String filter) throws Exception {
        restTurmaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(turma.getId().intValue())))
            .andExpect(jsonPath("$.[*].inicio").value(hasItem(DEFAULT_INICIO.toString())))
            .andExpect(jsonPath("$.[*].termino").value(hasItem(DEFAULT_TERMINO.toString())))
            .andExpect(jsonPath("$.[*].ano").value(hasItem(DEFAULT_ANO)))
            .andExpect(jsonPath("$.[*].statusCurso").value(hasItem(DEFAULT_STATUS_CURSO.toString())))
            .andExpect(jsonPath("$.[*].modalidade").value(hasItem(DEFAULT_MODALIDADE.toString())))
            .andExpect(jsonPath("$.[*].qtdVagas").value(hasItem(DEFAULT_QTD_VAGAS)))
            .andExpect(jsonPath("$.[*].numeroBca").value(hasItem(DEFAULT_NUMERO_BCA)));

        // Check, that the count call also returns 1
        restTurmaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTurmaShouldNotBeFound(String filter) throws Exception {
        restTurmaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTurmaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTurma() throws Exception {
        // Get the turma
        restTurmaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTurma() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        turmaSearchRepository.save(turma);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());

        // Update the turma
        Turma updatedTurma = turmaRepository.findById(turma.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTurma are not directly saved in db
        em.detach(updatedTurma);
        updatedTurma
            .inicio(UPDATED_INICIO)
            .termino(UPDATED_TERMINO)
            .ano(UPDATED_ANO)
            .statusCurso(UPDATED_STATUS_CURSO)
            .modalidade(UPDATED_MODALIDADE)
            .qtdVagas(UPDATED_QTD_VAGAS)
            .numeroBca(UPDATED_NUMERO_BCA);
        TurmaDTO turmaDTO = turmaMapper.toDto(updatedTurma);

        restTurmaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, turmaDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turmaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTurmaToMatchAllProperties(updatedTurma);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Turma> turmaSearchList = Streamable.of(turmaSearchRepository.findAll()).toList();
                Turma testTurmaSearch = turmaSearchList.get(searchDatabaseSizeAfter - 1);

                assertTurmaAllPropertiesEquals(testTurmaSearch, updatedTurma);
            });
    }

    @Test
    @Transactional
    void putNonExistingTurma() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        turma.setId(longCount.incrementAndGet());

        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTurmaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, turmaDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turmaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTurma() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        turma.setId(longCount.incrementAndGet());

        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurmaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(turmaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTurma() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        turma.setId(longCount.incrementAndGet());

        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurmaMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(turmaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTurmaWithPatch() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turma using partial update
        Turma partialUpdatedTurma = new Turma();
        partialUpdatedTurma.setId(turma.getId());

        partialUpdatedTurma.termino(UPDATED_TERMINO).ano(UPDATED_ANO).qtdVagas(UPDATED_QTD_VAGAS).numeroBca(UPDATED_NUMERO_BCA);

        restTurmaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTurma.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTurma))
            )
            .andExpect(status().isOk());

        // Validate the Turma in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTurmaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTurma, turma), getPersistedTurma(turma));
    }

    @Test
    @Transactional
    void fullUpdateTurmaWithPatch() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the turma using partial update
        Turma partialUpdatedTurma = new Turma();
        partialUpdatedTurma.setId(turma.getId());

        partialUpdatedTurma
            .inicio(UPDATED_INICIO)
            .termino(UPDATED_TERMINO)
            .ano(UPDATED_ANO)
            .statusCurso(UPDATED_STATUS_CURSO)
            .modalidade(UPDATED_MODALIDADE)
            .qtdVagas(UPDATED_QTD_VAGAS)
            .numeroBca(UPDATED_NUMERO_BCA);

        restTurmaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTurma.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTurma))
            )
            .andExpect(status().isOk());

        // Validate the Turma in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTurmaUpdatableFieldsEquals(partialUpdatedTurma, getPersistedTurma(partialUpdatedTurma));
    }

    @Test
    @Transactional
    void patchNonExistingTurma() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        turma.setId(longCount.incrementAndGet());

        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTurmaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, turmaDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(turmaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTurma() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        turma.setId(longCount.incrementAndGet());

        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurmaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(turmaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTurma() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        turma.setId(longCount.incrementAndGet());

        // Create the Turma
        TurmaDTO turmaDTO = turmaMapper.toDto(turma);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTurmaMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(turmaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Turma in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTurma() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);
        turmaRepository.save(turma);
        turmaSearchRepository.save(turma);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the turma
        restTurmaMockMvc
            .perform(delete(ENTITY_API_URL_ID, turma.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(turmaSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTurma() throws Exception {
        // Initialize the database
        insertedTurma = turmaRepository.saveAndFlush(turma);
        turmaSearchRepository.save(turma);

        // Search the turma
        restTurmaMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + turma.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(turma.getId().intValue())))
            .andExpect(jsonPath("$.[*].inicio").value(hasItem(DEFAULT_INICIO.toString())))
            .andExpect(jsonPath("$.[*].termino").value(hasItem(DEFAULT_TERMINO.toString())))
            .andExpect(jsonPath("$.[*].ano").value(hasItem(DEFAULT_ANO)))
            .andExpect(jsonPath("$.[*].statusCurso").value(hasItem(DEFAULT_STATUS_CURSO.toString())))
            .andExpect(jsonPath("$.[*].modalidade").value(hasItem(DEFAULT_MODALIDADE.toString())))
            .andExpect(jsonPath("$.[*].qtdVagas").value(hasItem(DEFAULT_QTD_VAGAS)))
            .andExpect(jsonPath("$.[*].numeroBca").value(hasItem(DEFAULT_NUMERO_BCA)));
    }

    protected long getRepositoryCount() {
        return turmaRepository.count();
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

    protected Turma getPersistedTurma(Turma turma) {
        return turmaRepository.findById(turma.getId()).orElseThrow();
    }

    protected void assertPersistedTurmaToMatchAllProperties(Turma expectedTurma) {
        assertTurmaAllPropertiesEquals(expectedTurma, getPersistedTurma(expectedTurma));
    }

    protected void assertPersistedTurmaToMatchUpdatableProperties(Turma expectedTurma) {
        assertTurmaAllUpdatablePropertiesEquals(expectedTurma, getPersistedTurma(expectedTurma));
    }
}
