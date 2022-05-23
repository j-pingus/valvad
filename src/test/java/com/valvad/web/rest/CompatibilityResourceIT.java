package com.valvad.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.valvad.IntegrationTest;
import com.valvad.domain.Compatibility;
import com.valvad.repository.CompatibilityRepository;
import com.valvad.repository.search.CompatibilitySearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CompatibilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CompatibilityResourceIT {

    private static final String ENTITY_API_URL = "/api/compatibilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/compatibilities";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompatibilityRepository compatibilityRepository;

    /**
     * This repository is mocked in the com.valvad.repository.search test package.
     *
     * @see com.valvad.repository.search.CompatibilitySearchRepositoryMockConfiguration
     */
    @Autowired
    private CompatibilitySearchRepository mockCompatibilitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompatibilityMockMvc;

    private Compatibility compatibility;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Compatibility createEntity(EntityManager em) {
        Compatibility compatibility = new Compatibility();
        return compatibility;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Compatibility createUpdatedEntity(EntityManager em) {
        Compatibility compatibility = new Compatibility();
        return compatibility;
    }

    @BeforeEach
    public void initTest() {
        compatibility = createEntity(em);
    }

    @Test
    @Transactional
    void createCompatibility() throws Exception {
        int databaseSizeBeforeCreate = compatibilityRepository.findAll().size();
        // Create the Compatibility
        restCompatibilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compatibility)))
            .andExpect(status().isCreated());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeCreate + 1);
        Compatibility testCompatibility = compatibilityList.get(compatibilityList.size() - 1);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(1)).save(testCompatibility);
    }

    @Test
    @Transactional
    void createCompatibilityWithExistingId() throws Exception {
        // Create the Compatibility with an existing ID
        compatibility.setId(1L);

        int databaseSizeBeforeCreate = compatibilityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompatibilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compatibility)))
            .andExpect(status().isBadRequest());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeCreate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void getAllCompatibilities() throws Exception {
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);

        // Get all the compatibilityList
        restCompatibilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compatibility.getId().intValue())));
    }

    @Test
    @Transactional
    void getCompatibility() throws Exception {
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);

        // Get the compatibility
        restCompatibilityMockMvc
            .perform(get(ENTITY_API_URL_ID, compatibility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(compatibility.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingCompatibility() throws Exception {
        // Get the compatibility
        restCompatibilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCompatibility() throws Exception {
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);

        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();

        // Update the compatibility
        Compatibility updatedCompatibility = compatibilityRepository.findById(compatibility.getId()).get();
        // Disconnect from session so that the updates on updatedCompatibility are not directly saved in db
        em.detach(updatedCompatibility);

        restCompatibilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCompatibility.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCompatibility))
            )
            .andExpect(status().isOk());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);
        Compatibility testCompatibility = compatibilityList.get(compatibilityList.size() - 1);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository).save(testCompatibility);
    }

    @Test
    @Transactional
    void putNonExistingCompatibility() throws Exception {
        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();
        compatibility.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompatibilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compatibility.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compatibility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompatibility() throws Exception {
        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();
        compatibility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompatibilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compatibility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompatibility() throws Exception {
        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();
        compatibility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompatibilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compatibility)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void partialUpdateCompatibilityWithPatch() throws Exception {
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);

        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();

        // Update the compatibility using partial update
        Compatibility partialUpdatedCompatibility = new Compatibility();
        partialUpdatedCompatibility.setId(compatibility.getId());

        restCompatibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompatibility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompatibility))
            )
            .andExpect(status().isOk());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);
        Compatibility testCompatibility = compatibilityList.get(compatibilityList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateCompatibilityWithPatch() throws Exception {
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);

        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();

        // Update the compatibility using partial update
        Compatibility partialUpdatedCompatibility = new Compatibility();
        partialUpdatedCompatibility.setId(compatibility.getId());

        restCompatibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompatibility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompatibility))
            )
            .andExpect(status().isOk());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);
        Compatibility testCompatibility = compatibilityList.get(compatibilityList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingCompatibility() throws Exception {
        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();
        compatibility.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompatibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, compatibility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(compatibility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompatibility() throws Exception {
        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();
        compatibility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompatibilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(compatibility))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompatibility() throws Exception {
        int databaseSizeBeforeUpdate = compatibilityRepository.findAll().size();
        compatibility.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompatibilityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(compatibility))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Compatibility in the database
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(0)).save(compatibility);
    }

    @Test
    @Transactional
    void deleteCompatibility() throws Exception {
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);

        int databaseSizeBeforeDelete = compatibilityRepository.findAll().size();

        // Delete the compatibility
        restCompatibilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, compatibility.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Compatibility> compatibilityList = compatibilityRepository.findAll();
        assertThat(compatibilityList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Compatibility in Elasticsearch
        verify(mockCompatibilitySearchRepository, times(1)).deleteById(compatibility.getId());
    }

    @Test
    @Transactional
    void searchCompatibility() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        compatibilityRepository.saveAndFlush(compatibility);
        when(mockCompatibilitySearchRepository.search("id:" + compatibility.getId())).thenReturn(Stream.of(compatibility));

        // Search the compatibility
        restCompatibilityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + compatibility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compatibility.getId().intValue())));
    }
}
