package com.ecursos.myapp.web.rest;

import static com.ecursos.myapp.domain.TipoAsserts.*;
import static com.ecursos.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecursos.myapp.IntegrationTest;
import com.ecursos.myapp.domain.Tipo;
import com.ecursos.myapp.repository.TipoRepository;
import com.ecursos.myapp.repository.search.TipoSearchRepository;
import com.ecursos.myapp.service.dto.TipoDTO;
import com.ecursos.myapp.service.mapper.TipoMapper;
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
 * Integration tests for the {@link TipoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TipoResourceIT {

    private static final String DEFAULT_CATEGORIA = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORIA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tipos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/tipos/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private TipoMapper tipoMapper;

    @Autowired
    private TipoSearchRepository tipoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTipoMockMvc;

    private Tipo tipo;

    private Tipo insertedTipo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tipo createEntity() {
        return new Tipo().categoria(DEFAULT_CATEGORIA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tipo createUpdatedEntity() {
        return new Tipo().categoria(UPDATED_CATEGORIA);
    }

    @BeforeEach
    public void initTest() {
        tipo = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTipo != null) {
            tipoRepository.delete(insertedTipo);
            tipoSearchRepository.delete(insertedTipo);
            insertedTipo = null;
        }
    }

    @Test
    @Transactional
    void createTipo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);
        var returnedTipoDTO = om.readValue(
            restTipoMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TipoDTO.class
        );

        // Validate the Tipo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTipo = tipoMapper.toEntity(returnedTipoDTO);
        assertTipoUpdatableFieldsEquals(returnedTipo, getPersistedTipo(returnedTipo));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTipo = returnedTipo;
    }

    @Test
    @Transactional
    void createTipoWithExistingId() throws Exception {
        // Create the Tipo with an existing ID
        tipo.setId(1L);
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTipoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCategoriaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        // set the field null
        tipo.setCategoria(null);

        // Create the Tipo, which fails.
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        restTipoMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTipos() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get all the tipoList
        restTipoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipo.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoria").value(hasItem(DEFAULT_CATEGORIA)));
    }

    @Test
    @Transactional
    void getTipo() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get the tipo
        restTipoMockMvc
            .perform(get(ENTITY_API_URL_ID, tipo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tipo.getId().intValue()))
            .andExpect(jsonPath("$.categoria").value(DEFAULT_CATEGORIA));
    }

    @Test
    @Transactional
    void getTiposByIdFiltering() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        Long id = tipo.getId();

        defaultTipoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTipoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTipoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTiposByCategoriaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get all the tipoList where categoria equals to
        defaultTipoFiltering("categoria.equals=" + DEFAULT_CATEGORIA, "categoria.equals=" + UPDATED_CATEGORIA);
    }

    @Test
    @Transactional
    void getAllTiposByCategoriaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get all the tipoList where categoria in
        defaultTipoFiltering("categoria.in=" + DEFAULT_CATEGORIA + "," + UPDATED_CATEGORIA, "categoria.in=" + UPDATED_CATEGORIA);
    }

    @Test
    @Transactional
    void getAllTiposByCategoriaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get all the tipoList where categoria is not null
        defaultTipoFiltering("categoria.specified=true", "categoria.specified=false");
    }

    @Test
    @Transactional
    void getAllTiposByCategoriaContainsSomething() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get all the tipoList where categoria contains
        defaultTipoFiltering("categoria.contains=" + DEFAULT_CATEGORIA, "categoria.contains=" + UPDATED_CATEGORIA);
    }

    @Test
    @Transactional
    void getAllTiposByCategoriaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        // Get all the tipoList where categoria does not contain
        defaultTipoFiltering("categoria.doesNotContain=" + UPDATED_CATEGORIA, "categoria.doesNotContain=" + DEFAULT_CATEGORIA);
    }

    private void defaultTipoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTipoShouldBeFound(shouldBeFound);
        defaultTipoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTipoShouldBeFound(String filter) throws Exception {
        restTipoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipo.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoria").value(hasItem(DEFAULT_CATEGORIA)));

        // Check, that the count call also returns 1
        restTipoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTipoShouldNotBeFound(String filter) throws Exception {
        restTipoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTipoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTipo() throws Exception {
        // Get the tipo
        restTipoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTipo() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        tipoSearchRepository.save(tipo);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());

        // Update the tipo
        Tipo updatedTipo = tipoRepository.findById(tipo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTipo are not directly saved in db
        em.detach(updatedTipo);
        updatedTipo.categoria(UPDATED_CATEGORIA);
        TipoDTO tipoDTO = tipoMapper.toDto(updatedTipo);

        restTipoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tipoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tipoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTipoToMatchAllProperties(updatedTipo);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tipo> tipoSearchList = Streamable.of(tipoSearchRepository.findAll()).toList();
                Tipo testTipoSearch = tipoSearchList.get(searchDatabaseSizeAfter - 1);

                assertTipoAllPropertiesEquals(testTipoSearch, updatedTipo);
            });
    }

    @Test
    @Transactional
    void putNonExistingTipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        tipo.setId(longCount.incrementAndGet());

        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tipoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        tipo.setId(longCount.incrementAndGet());

        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        tipo.setId(longCount.incrementAndGet());

        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tipoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTipoWithPatch() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tipo using partial update
        Tipo partialUpdatedTipo = new Tipo();
        partialUpdatedTipo.setId(tipo.getId());

        restTipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTipo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTipo))
            )
            .andExpect(status().isOk());

        // Validate the Tipo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTipoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTipo, tipo), getPersistedTipo(tipo));
    }

    @Test
    @Transactional
    void fullUpdateTipoWithPatch() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tipo using partial update
        Tipo partialUpdatedTipo = new Tipo();
        partialUpdatedTipo.setId(tipo.getId());

        partialUpdatedTipo.categoria(UPDATED_CATEGORIA);

        restTipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTipo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTipo))
            )
            .andExpect(status().isOk());

        // Validate the Tipo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTipoUpdatableFieldsEquals(partialUpdatedTipo, getPersistedTipo(partialUpdatedTipo));
    }

    @Test
    @Transactional
    void patchNonExistingTipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        tipo.setId(longCount.incrementAndGet());

        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tipoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        tipo.setId(longCount.incrementAndGet());

        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tipoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTipo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        tipo.setId(longCount.incrementAndGet());

        // Create the Tipo
        TipoDTO tipoDTO = tipoMapper.toDto(tipo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tipoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tipo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTipo() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);
        tipoRepository.save(tipo);
        tipoSearchRepository.save(tipo);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tipo
        restTipoMockMvc
            .perform(delete(ENTITY_API_URL_ID, tipo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tipoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTipo() throws Exception {
        // Initialize the database
        insertedTipo = tipoRepository.saveAndFlush(tipo);
        tipoSearchRepository.save(tipo);

        // Search the tipo
        restTipoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tipo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipo.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoria").value(hasItem(DEFAULT_CATEGORIA)));
    }

    protected long getRepositoryCount() {
        return tipoRepository.count();
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

    protected Tipo getPersistedTipo(Tipo tipo) {
        return tipoRepository.findById(tipo.getId()).orElseThrow();
    }

    protected void assertPersistedTipoToMatchAllProperties(Tipo expectedTipo) {
        assertTipoAllPropertiesEquals(expectedTipo, getPersistedTipo(expectedTipo));
    }

    protected void assertPersistedTipoToMatchUpdatableProperties(Tipo expectedTipo) {
        assertTipoAllUpdatablePropertiesEquals(expectedTipo, getPersistedTipo(expectedTipo));
    }
}
