package com.ecursos.myapp.web.rest;

import static com.ecursos.myapp.domain.MilitarAsserts.*;
import static com.ecursos.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecursos.myapp.IntegrationTest;
import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.domain.Posto;
import com.ecursos.myapp.domain.enumeration.ForcaEnum;
import com.ecursos.myapp.domain.enumeration.StatusMilitarEnum;
import com.ecursos.myapp.repository.MilitarRepository;
import com.ecursos.myapp.repository.search.MilitarSearchRepository;
import com.ecursos.myapp.service.MilitarService;
import com.ecursos.myapp.service.dto.MilitarDTO;
import com.ecursos.myapp.service.mapper.MilitarMapper;
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
 * Integration tests for the {@link MilitarResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MilitarResourceIT {

    private static final String DEFAULT_SARAM = "AAAAAAAAAA";
    private static final String UPDATED_SARAM = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_COMPLETO = "AAAAAAAAAA";
    private static final String UPDATED_NOME_COMPLETO = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_GUERRA = "AAAAAAAAAA";
    private static final String UPDATED_NOME_GUERRA = "BBBBBBBBBB";

    private static final String DEFAULT_OM = "AAAAAAAAAA";
    private static final String UPDATED_OM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONE = "BBBBBBBBBB";

    private static final StatusMilitarEnum DEFAULT_STATUS_MILITAR = StatusMilitarEnum.ATIVA;
    private static final StatusMilitarEnum UPDATED_STATUS_MILITAR = StatusMilitarEnum.INATIVO;

    private static final ForcaEnum DEFAULT_FORCA = ForcaEnum.MARINHA_DO_BRASIL;
    private static final ForcaEnum UPDATED_FORCA = ForcaEnum.EXERCITO_BRASILEIRO;

    private static final String DEFAULT_NR_ANTIGUIDADE = "AAAAAAAAAA";
    private static final String UPDATED_NR_ANTIGUIDADE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ULTIMA_PROMOCAO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ULTIMA_PROMOCAO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ULTIMA_PROMOCAO = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_CPF = "AAAAAAAAAA";
    private static final String UPDATED_CPF = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/militars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/militars/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MilitarRepository militarRepository;

    @Mock
    private MilitarRepository militarRepositoryMock;

    @Autowired
    private MilitarMapper militarMapper;

    @Mock
    private MilitarService militarServiceMock;

    @Autowired
    private MilitarSearchRepository militarSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMilitarMockMvc;

    private Militar militar;

    private Militar insertedMilitar;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Militar createEntity(EntityManager em) {
        Militar militar = new Militar()
            .saram(DEFAULT_SARAM)
            .nomeCompleto(DEFAULT_NOME_COMPLETO)
            .nomeGuerra(DEFAULT_NOME_GUERRA)
            .om(DEFAULT_OM)
            .telefone(DEFAULT_TELEFONE)
            .statusMilitar(DEFAULT_STATUS_MILITAR)
            .forca(DEFAULT_FORCA)
            .nrAntiguidade(DEFAULT_NR_ANTIGUIDADE)
            .ultimaPromocao(DEFAULT_ULTIMA_PROMOCAO)
            .cpf(DEFAULT_CPF)
            .email(DEFAULT_EMAIL);
        // Add required entity
        Posto posto;
        if (TestUtil.findAll(em, Posto.class).isEmpty()) {
            posto = PostoResourceIT.createEntity();
            em.persist(posto);
            em.flush();
        } else {
            posto = TestUtil.findAll(em, Posto.class).get(0);
        }
        militar.setPosto(posto);
        return militar;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Militar createUpdatedEntity(EntityManager em) {
        Militar updatedMilitar = new Militar()
            .saram(UPDATED_SARAM)
            .nomeCompleto(UPDATED_NOME_COMPLETO)
            .nomeGuerra(UPDATED_NOME_GUERRA)
            .om(UPDATED_OM)
            .telefone(UPDATED_TELEFONE)
            .statusMilitar(UPDATED_STATUS_MILITAR)
            .forca(UPDATED_FORCA)
            .nrAntiguidade(UPDATED_NR_ANTIGUIDADE)
            .ultimaPromocao(UPDATED_ULTIMA_PROMOCAO)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL);
        // Add required entity
        Posto posto;
        if (TestUtil.findAll(em, Posto.class).isEmpty()) {
            posto = PostoResourceIT.createUpdatedEntity();
            em.persist(posto);
            em.flush();
        } else {
            posto = TestUtil.findAll(em, Posto.class).get(0);
        }
        updatedMilitar.setPosto(posto);
        return updatedMilitar;
    }

    @BeforeEach
    public void initTest() {
        militar = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedMilitar != null) {
            militarRepository.delete(insertedMilitar);
            militarSearchRepository.delete(insertedMilitar);
            insertedMilitar = null;
        }
    }

    @Test
    @Transactional
    void createMilitar() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);
        var returnedMilitarDTO = om.readValue(
            restMilitarMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MilitarDTO.class
        );

        // Validate the Militar in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMilitar = militarMapper.toEntity(returnedMilitarDTO);
        assertMilitarUpdatableFieldsEquals(returnedMilitar, getPersistedMilitar(returnedMilitar));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMilitar = returnedMilitar;
    }

    @Test
    @Transactional
    void createMilitarWithExistingId() throws Exception {
        // Create the Militar with an existing ID
        militar.setId(1L);
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSaramIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // set the field null
        militar.setSaram(null);

        // Create the Militar, which fails.
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNomeCompletoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // set the field null
        militar.setNomeCompleto(null);

        // Create the Militar, which fails.
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNomeGuerraIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // set the field null
        militar.setNomeGuerra(null);

        // Create the Militar, which fails.
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // set the field null
        militar.setOm(null);

        // Create the Militar, which fails.
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusMilitarIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // set the field null
        militar.setStatusMilitar(null);

        // Create the Militar, which fails.
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkForcaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        // set the field null
        militar.setForca(null);

        // Create the Militar, which fails.
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        restMilitarMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMilitars() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList
        restMilitarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(militar.getId().intValue())))
            .andExpect(jsonPath("$.[*].saram").value(hasItem(DEFAULT_SARAM)))
            .andExpect(jsonPath("$.[*].nomeCompleto").value(hasItem(DEFAULT_NOME_COMPLETO)))
            .andExpect(jsonPath("$.[*].nomeGuerra").value(hasItem(DEFAULT_NOME_GUERRA)))
            .andExpect(jsonPath("$.[*].om").value(hasItem(DEFAULT_OM)))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)))
            .andExpect(jsonPath("$.[*].statusMilitar").value(hasItem(DEFAULT_STATUS_MILITAR.toString())))
            .andExpect(jsonPath("$.[*].forca").value(hasItem(DEFAULT_FORCA.toString())))
            .andExpect(jsonPath("$.[*].nrAntiguidade").value(hasItem(DEFAULT_NR_ANTIGUIDADE)))
            .andExpect(jsonPath("$.[*].ultimaPromocao").value(hasItem(DEFAULT_ULTIMA_PROMOCAO.toString())))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMilitarsWithEagerRelationshipsIsEnabled() throws Exception {
        when(militarServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMilitarMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(militarServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMilitarsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(militarServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMilitarMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(militarRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMilitar() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get the militar
        restMilitarMockMvc
            .perform(get(ENTITY_API_URL_ID, militar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(militar.getId().intValue()))
            .andExpect(jsonPath("$.saram").value(DEFAULT_SARAM))
            .andExpect(jsonPath("$.nomeCompleto").value(DEFAULT_NOME_COMPLETO))
            .andExpect(jsonPath("$.nomeGuerra").value(DEFAULT_NOME_GUERRA))
            .andExpect(jsonPath("$.om").value(DEFAULT_OM))
            .andExpect(jsonPath("$.telefone").value(DEFAULT_TELEFONE))
            .andExpect(jsonPath("$.statusMilitar").value(DEFAULT_STATUS_MILITAR.toString()))
            .andExpect(jsonPath("$.forca").value(DEFAULT_FORCA.toString()))
            .andExpect(jsonPath("$.nrAntiguidade").value(DEFAULT_NR_ANTIGUIDADE))
            .andExpect(jsonPath("$.ultimaPromocao").value(DEFAULT_ULTIMA_PROMOCAO.toString()))
            .andExpect(jsonPath("$.cpf").value(DEFAULT_CPF))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getMilitarsByIdFiltering() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        Long id = militar.getId();

        defaultMilitarFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMilitarFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMilitarFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMilitarsBySaramIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where saram equals to
        defaultMilitarFiltering("saram.equals=" + DEFAULT_SARAM, "saram.equals=" + UPDATED_SARAM);
    }

    @Test
    @Transactional
    void getAllMilitarsBySaramIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where saram in
        defaultMilitarFiltering("saram.in=" + DEFAULT_SARAM + "," + UPDATED_SARAM, "saram.in=" + UPDATED_SARAM);
    }

    @Test
    @Transactional
    void getAllMilitarsBySaramIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where saram is not null
        defaultMilitarFiltering("saram.specified=true", "saram.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsBySaramContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where saram contains
        defaultMilitarFiltering("saram.contains=" + DEFAULT_SARAM, "saram.contains=" + UPDATED_SARAM);
    }

    @Test
    @Transactional
    void getAllMilitarsBySaramNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where saram does not contain
        defaultMilitarFiltering("saram.doesNotContain=" + UPDATED_SARAM, "saram.doesNotContain=" + DEFAULT_SARAM);
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeCompletoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeCompleto equals to
        defaultMilitarFiltering("nomeCompleto.equals=" + DEFAULT_NOME_COMPLETO, "nomeCompleto.equals=" + UPDATED_NOME_COMPLETO);
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeCompletoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeCompleto in
        defaultMilitarFiltering(
            "nomeCompleto.in=" + DEFAULT_NOME_COMPLETO + "," + UPDATED_NOME_COMPLETO,
            "nomeCompleto.in=" + UPDATED_NOME_COMPLETO
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeCompletoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeCompleto is not null
        defaultMilitarFiltering("nomeCompleto.specified=true", "nomeCompleto.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeCompletoContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeCompleto contains
        defaultMilitarFiltering("nomeCompleto.contains=" + DEFAULT_NOME_COMPLETO, "nomeCompleto.contains=" + UPDATED_NOME_COMPLETO);
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeCompletoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeCompleto does not contain
        defaultMilitarFiltering(
            "nomeCompleto.doesNotContain=" + UPDATED_NOME_COMPLETO,
            "nomeCompleto.doesNotContain=" + DEFAULT_NOME_COMPLETO
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeGuerraIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeGuerra equals to
        defaultMilitarFiltering("nomeGuerra.equals=" + DEFAULT_NOME_GUERRA, "nomeGuerra.equals=" + UPDATED_NOME_GUERRA);
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeGuerraIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeGuerra in
        defaultMilitarFiltering("nomeGuerra.in=" + DEFAULT_NOME_GUERRA + "," + UPDATED_NOME_GUERRA, "nomeGuerra.in=" + UPDATED_NOME_GUERRA);
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeGuerraIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeGuerra is not null
        defaultMilitarFiltering("nomeGuerra.specified=true", "nomeGuerra.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeGuerraContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeGuerra contains
        defaultMilitarFiltering("nomeGuerra.contains=" + DEFAULT_NOME_GUERRA, "nomeGuerra.contains=" + UPDATED_NOME_GUERRA);
    }

    @Test
    @Transactional
    void getAllMilitarsByNomeGuerraNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nomeGuerra does not contain
        defaultMilitarFiltering("nomeGuerra.doesNotContain=" + UPDATED_NOME_GUERRA, "nomeGuerra.doesNotContain=" + DEFAULT_NOME_GUERRA);
    }

    @Test
    @Transactional
    void getAllMilitarsByOmIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where om equals to
        defaultMilitarFiltering("om.equals=" + DEFAULT_OM, "om.equals=" + UPDATED_OM);
    }

    @Test
    @Transactional
    void getAllMilitarsByOmIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where om in
        defaultMilitarFiltering("om.in=" + DEFAULT_OM + "," + UPDATED_OM, "om.in=" + UPDATED_OM);
    }

    @Test
    @Transactional
    void getAllMilitarsByOmIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where om is not null
        defaultMilitarFiltering("om.specified=true", "om.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByOmContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where om contains
        defaultMilitarFiltering("om.contains=" + DEFAULT_OM, "om.contains=" + UPDATED_OM);
    }

    @Test
    @Transactional
    void getAllMilitarsByOmNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where om does not contain
        defaultMilitarFiltering("om.doesNotContain=" + UPDATED_OM, "om.doesNotContain=" + DEFAULT_OM);
    }

    @Test
    @Transactional
    void getAllMilitarsByTelefoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where telefone equals to
        defaultMilitarFiltering("telefone.equals=" + DEFAULT_TELEFONE, "telefone.equals=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllMilitarsByTelefoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where telefone in
        defaultMilitarFiltering("telefone.in=" + DEFAULT_TELEFONE + "," + UPDATED_TELEFONE, "telefone.in=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllMilitarsByTelefoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where telefone is not null
        defaultMilitarFiltering("telefone.specified=true", "telefone.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByTelefoneContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where telefone contains
        defaultMilitarFiltering("telefone.contains=" + DEFAULT_TELEFONE, "telefone.contains=" + UPDATED_TELEFONE);
    }

    @Test
    @Transactional
    void getAllMilitarsByTelefoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where telefone does not contain
        defaultMilitarFiltering("telefone.doesNotContain=" + UPDATED_TELEFONE, "telefone.doesNotContain=" + DEFAULT_TELEFONE);
    }

    @Test
    @Transactional
    void getAllMilitarsByStatusMilitarIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where statusMilitar equals to
        defaultMilitarFiltering("statusMilitar.equals=" + DEFAULT_STATUS_MILITAR, "statusMilitar.equals=" + UPDATED_STATUS_MILITAR);
    }

    @Test
    @Transactional
    void getAllMilitarsByStatusMilitarIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where statusMilitar in
        defaultMilitarFiltering(
            "statusMilitar.in=" + DEFAULT_STATUS_MILITAR + "," + UPDATED_STATUS_MILITAR,
            "statusMilitar.in=" + UPDATED_STATUS_MILITAR
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByStatusMilitarIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where statusMilitar is not null
        defaultMilitarFiltering("statusMilitar.specified=true", "statusMilitar.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByForcaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where forca equals to
        defaultMilitarFiltering("forca.equals=" + DEFAULT_FORCA, "forca.equals=" + UPDATED_FORCA);
    }

    @Test
    @Transactional
    void getAllMilitarsByForcaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where forca in
        defaultMilitarFiltering("forca.in=" + DEFAULT_FORCA + "," + UPDATED_FORCA, "forca.in=" + UPDATED_FORCA);
    }

    @Test
    @Transactional
    void getAllMilitarsByForcaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where forca is not null
        defaultMilitarFiltering("forca.specified=true", "forca.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByNrAntiguidadeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nrAntiguidade equals to
        defaultMilitarFiltering("nrAntiguidade.equals=" + DEFAULT_NR_ANTIGUIDADE, "nrAntiguidade.equals=" + UPDATED_NR_ANTIGUIDADE);
    }

    @Test
    @Transactional
    void getAllMilitarsByNrAntiguidadeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nrAntiguidade in
        defaultMilitarFiltering(
            "nrAntiguidade.in=" + DEFAULT_NR_ANTIGUIDADE + "," + UPDATED_NR_ANTIGUIDADE,
            "nrAntiguidade.in=" + UPDATED_NR_ANTIGUIDADE
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByNrAntiguidadeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nrAntiguidade is not null
        defaultMilitarFiltering("nrAntiguidade.specified=true", "nrAntiguidade.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByNrAntiguidadeContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nrAntiguidade contains
        defaultMilitarFiltering("nrAntiguidade.contains=" + DEFAULT_NR_ANTIGUIDADE, "nrAntiguidade.contains=" + UPDATED_NR_ANTIGUIDADE);
    }

    @Test
    @Transactional
    void getAllMilitarsByNrAntiguidadeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where nrAntiguidade does not contain
        defaultMilitarFiltering(
            "nrAntiguidade.doesNotContain=" + UPDATED_NR_ANTIGUIDADE,
            "nrAntiguidade.doesNotContain=" + DEFAULT_NR_ANTIGUIDADE
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao equals to
        defaultMilitarFiltering("ultimaPromocao.equals=" + DEFAULT_ULTIMA_PROMOCAO, "ultimaPromocao.equals=" + UPDATED_ULTIMA_PROMOCAO);
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao in
        defaultMilitarFiltering(
            "ultimaPromocao.in=" + DEFAULT_ULTIMA_PROMOCAO + "," + UPDATED_ULTIMA_PROMOCAO,
            "ultimaPromocao.in=" + UPDATED_ULTIMA_PROMOCAO
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao is not null
        defaultMilitarFiltering("ultimaPromocao.specified=true", "ultimaPromocao.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao is greater than or equal to
        defaultMilitarFiltering(
            "ultimaPromocao.greaterThanOrEqual=" + DEFAULT_ULTIMA_PROMOCAO,
            "ultimaPromocao.greaterThanOrEqual=" + UPDATED_ULTIMA_PROMOCAO
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao is less than or equal to
        defaultMilitarFiltering(
            "ultimaPromocao.lessThanOrEqual=" + DEFAULT_ULTIMA_PROMOCAO,
            "ultimaPromocao.lessThanOrEqual=" + SMALLER_ULTIMA_PROMOCAO
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao is less than
        defaultMilitarFiltering("ultimaPromocao.lessThan=" + UPDATED_ULTIMA_PROMOCAO, "ultimaPromocao.lessThan=" + DEFAULT_ULTIMA_PROMOCAO);
    }

    @Test
    @Transactional
    void getAllMilitarsByUltimaPromocaoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where ultimaPromocao is greater than
        defaultMilitarFiltering(
            "ultimaPromocao.greaterThan=" + SMALLER_ULTIMA_PROMOCAO,
            "ultimaPromocao.greaterThan=" + DEFAULT_ULTIMA_PROMOCAO
        );
    }

    @Test
    @Transactional
    void getAllMilitarsByCpfIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where cpf equals to
        defaultMilitarFiltering("cpf.equals=" + DEFAULT_CPF, "cpf.equals=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllMilitarsByCpfIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where cpf in
        defaultMilitarFiltering("cpf.in=" + DEFAULT_CPF + "," + UPDATED_CPF, "cpf.in=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllMilitarsByCpfIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where cpf is not null
        defaultMilitarFiltering("cpf.specified=true", "cpf.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByCpfContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where cpf contains
        defaultMilitarFiltering("cpf.contains=" + DEFAULT_CPF, "cpf.contains=" + UPDATED_CPF);
    }

    @Test
    @Transactional
    void getAllMilitarsByCpfNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where cpf does not contain
        defaultMilitarFiltering("cpf.doesNotContain=" + UPDATED_CPF, "cpf.doesNotContain=" + DEFAULT_CPF);
    }

    @Test
    @Transactional
    void getAllMilitarsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where email equals to
        defaultMilitarFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMilitarsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where email in
        defaultMilitarFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMilitarsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where email is not null
        defaultMilitarFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllMilitarsByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where email contains
        defaultMilitarFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllMilitarsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        // Get all the militarList where email does not contain
        defaultMilitarFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllMilitarsByPostoIsEqualToSomething() throws Exception {
        Posto posto;
        if (TestUtil.findAll(em, Posto.class).isEmpty()) {
            militarRepository.saveAndFlush(militar);
            posto = PostoResourceIT.createEntity();
        } else {
            posto = TestUtil.findAll(em, Posto.class).get(0);
        }
        em.persist(posto);
        em.flush();
        militar.setPosto(posto);
        militarRepository.saveAndFlush(militar);
        Long postoId = posto.getId();
        // Get all the militarList where posto equals to postoId
        defaultMilitarShouldBeFound("postoId.equals=" + postoId);

        // Get all the militarList where posto equals to (postoId + 1)
        defaultMilitarShouldNotBeFound("postoId.equals=" + (postoId + 1));
    }

    private void defaultMilitarFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMilitarShouldBeFound(shouldBeFound);
        defaultMilitarShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMilitarShouldBeFound(String filter) throws Exception {
        restMilitarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(militar.getId().intValue())))
            .andExpect(jsonPath("$.[*].saram").value(hasItem(DEFAULT_SARAM)))
            .andExpect(jsonPath("$.[*].nomeCompleto").value(hasItem(DEFAULT_NOME_COMPLETO)))
            .andExpect(jsonPath("$.[*].nomeGuerra").value(hasItem(DEFAULT_NOME_GUERRA)))
            .andExpect(jsonPath("$.[*].om").value(hasItem(DEFAULT_OM)))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)))
            .andExpect(jsonPath("$.[*].statusMilitar").value(hasItem(DEFAULT_STATUS_MILITAR.toString())))
            .andExpect(jsonPath("$.[*].forca").value(hasItem(DEFAULT_FORCA.toString())))
            .andExpect(jsonPath("$.[*].nrAntiguidade").value(hasItem(DEFAULT_NR_ANTIGUIDADE)))
            .andExpect(jsonPath("$.[*].ultimaPromocao").value(hasItem(DEFAULT_ULTIMA_PROMOCAO.toString())))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));

        // Check, that the count call also returns 1
        restMilitarMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMilitarShouldNotBeFound(String filter) throws Exception {
        restMilitarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMilitarMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMilitar() throws Exception {
        // Get the militar
        restMilitarMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMilitar() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        militarSearchRepository.save(militar);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());

        // Update the militar
        Militar updatedMilitar = militarRepository.findById(militar.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMilitar are not directly saved in db
        em.detach(updatedMilitar);
        updatedMilitar
            .saram(UPDATED_SARAM)
            .nomeCompleto(UPDATED_NOME_COMPLETO)
            .nomeGuerra(UPDATED_NOME_GUERRA)
            .om(UPDATED_OM)
            .telefone(UPDATED_TELEFONE)
            .statusMilitar(UPDATED_STATUS_MILITAR)
            .forca(UPDATED_FORCA)
            .nrAntiguidade(UPDATED_NR_ANTIGUIDADE)
            .ultimaPromocao(UPDATED_ULTIMA_PROMOCAO)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL);
        MilitarDTO militarDTO = militarMapper.toDto(updatedMilitar);

        restMilitarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, militarDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(militarDTO))
            )
            .andExpect(status().isOk());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMilitarToMatchAllProperties(updatedMilitar);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Militar> militarSearchList = Streamable.of(militarSearchRepository.findAll()).toList();
                Militar testMilitarSearch = militarSearchList.get(searchDatabaseSizeAfter - 1);

                assertMilitarAllPropertiesEquals(testMilitarSearch, updatedMilitar);
            });
    }

    @Test
    @Transactional
    void putNonExistingMilitar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        militar.setId(longCount.incrementAndGet());

        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMilitarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, militarDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(militarDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMilitar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        militar.setId(longCount.incrementAndGet());

        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMilitarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(militarDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMilitar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        militar.setId(longCount.incrementAndGet());

        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMilitarMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(militarDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMilitarWithPatch() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the militar using partial update
        Militar partialUpdatedMilitar = new Militar();
        partialUpdatedMilitar.setId(militar.getId());

        partialUpdatedMilitar.saram(UPDATED_SARAM).nomeCompleto(UPDATED_NOME_COMPLETO).nomeGuerra(UPDATED_NOME_GUERRA);

        restMilitarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMilitar.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMilitar))
            )
            .andExpect(status().isOk());

        // Validate the Militar in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMilitarUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMilitar, militar), getPersistedMilitar(militar));
    }

    @Test
    @Transactional
    void fullUpdateMilitarWithPatch() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the militar using partial update
        Militar partialUpdatedMilitar = new Militar();
        partialUpdatedMilitar.setId(militar.getId());

        partialUpdatedMilitar
            .saram(UPDATED_SARAM)
            .nomeCompleto(UPDATED_NOME_COMPLETO)
            .nomeGuerra(UPDATED_NOME_GUERRA)
            .om(UPDATED_OM)
            .telefone(UPDATED_TELEFONE)
            .statusMilitar(UPDATED_STATUS_MILITAR)
            .forca(UPDATED_FORCA)
            .nrAntiguidade(UPDATED_NR_ANTIGUIDADE)
            .ultimaPromocao(UPDATED_ULTIMA_PROMOCAO)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL);

        restMilitarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMilitar.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMilitar))
            )
            .andExpect(status().isOk());

        // Validate the Militar in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMilitarUpdatableFieldsEquals(partialUpdatedMilitar, getPersistedMilitar(partialUpdatedMilitar));
    }

    @Test
    @Transactional
    void patchNonExistingMilitar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        militar.setId(longCount.incrementAndGet());

        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMilitarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, militarDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(militarDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMilitar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        militar.setId(longCount.incrementAndGet());

        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMilitarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(militarDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMilitar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        militar.setId(longCount.incrementAndGet());

        // Create the Militar
        MilitarDTO militarDTO = militarMapper.toDto(militar);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMilitarMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(militarDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Militar in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMilitar() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);
        militarRepository.save(militar);
        militarSearchRepository.save(militar);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the militar
        restMilitarMockMvc
            .perform(delete(ENTITY_API_URL_ID, militar.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(militarSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMilitar() throws Exception {
        // Initialize the database
        insertedMilitar = militarRepository.saveAndFlush(militar);
        militarSearchRepository.save(militar);

        // Search the militar
        restMilitarMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + militar.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(militar.getId().intValue())))
            .andExpect(jsonPath("$.[*].saram").value(hasItem(DEFAULT_SARAM)))
            .andExpect(jsonPath("$.[*].nomeCompleto").value(hasItem(DEFAULT_NOME_COMPLETO)))
            .andExpect(jsonPath("$.[*].nomeGuerra").value(hasItem(DEFAULT_NOME_GUERRA)))
            .andExpect(jsonPath("$.[*].om").value(hasItem(DEFAULT_OM)))
            .andExpect(jsonPath("$.[*].telefone").value(hasItem(DEFAULT_TELEFONE)))
            .andExpect(jsonPath("$.[*].statusMilitar").value(hasItem(DEFAULT_STATUS_MILITAR.toString())))
            .andExpect(jsonPath("$.[*].forca").value(hasItem(DEFAULT_FORCA.toString())))
            .andExpect(jsonPath("$.[*].nrAntiguidade").value(hasItem(DEFAULT_NR_ANTIGUIDADE)))
            .andExpect(jsonPath("$.[*].ultimaPromocao").value(hasItem(DEFAULT_ULTIMA_PROMOCAO.toString())))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    protected long getRepositoryCount() {
        return militarRepository.count();
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

    protected Militar getPersistedMilitar(Militar militar) {
        return militarRepository.findById(militar.getId()).orElseThrow();
    }

    protected void assertPersistedMilitarToMatchAllProperties(Militar expectedMilitar) {
        assertMilitarAllPropertiesEquals(expectedMilitar, getPersistedMilitar(expectedMilitar));
    }

    protected void assertPersistedMilitarToMatchUpdatableProperties(Militar expectedMilitar) {
        assertMilitarAllUpdatablePropertiesEquals(expectedMilitar, getPersistedMilitar(expectedMilitar));
    }
}
