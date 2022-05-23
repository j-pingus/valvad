package com.valvad.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.valvad.IntegrationTest;
import com.valvad.domain.Part;
import com.valvad.repository.PartRepository;
import com.valvad.repository.search.PartSearchRepository;
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
 * Integration tests for the {@link PartResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PartResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/parts";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PartRepository partRepository;

    /**
     * This repository is mocked in the com.valvad.repository.search test package.
     *
     * @see com.valvad.repository.search.PartSearchRepositoryMockConfiguration
     */
    @Autowired
    private PartSearchRepository mockPartSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartMockMvc;

    private Part part;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createEntity(EntityManager em) {
        Part part = new Part().description(DEFAULT_DESCRIPTION).number(DEFAULT_NUMBER);
        return part;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Part createUpdatedEntity(EntityManager em) {
        Part part = new Part().description(UPDATED_DESCRIPTION).number(UPDATED_NUMBER);
        return part;
    }

    @BeforeEach
    public void initTest() {
        part = createEntity(em);
    }

    @Test
    @Transactional
    void createPart() throws Exception {
        int databaseSizeBeforeCreate = partRepository.findAll().size();
        // Create the Part
        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isCreated());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeCreate + 1);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPart.getNumber()).isEqualTo(DEFAULT_NUMBER);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(1)).save(testPart);
    }

    @Test
    @Transactional
    void createPartWithExistingId() throws Exception {
        // Create the Part with an existing ID
        part.setId(1L);

        int databaseSizeBeforeCreate = partRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeCreate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = partRepository.findAll().size();
        // set the field null
        part.setDescription(null);

        // Create the Part, which fails.

        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isBadRequest());

        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = partRepository.findAll().size();
        // set the field null
        part.setNumber(null);

        // Create the Part, which fails.

        restPartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isBadRequest());

        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParts() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get all the partList
        restPartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)));
    }

    @Test
    @Transactional
    void getPart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        // Get the part
        restPartMockMvc
            .perform(get(ENTITY_API_URL_ID, part.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(part.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingPart() throws Exception {
        // Get the part
        restPartMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part
        Part updatedPart = partRepository.findById(part.getId()).get();
        // Disconnect from session so that the updates on updatedPart are not directly saved in db
        em.detach(updatedPart);
        updatedPart.description(UPDATED_DESCRIPTION).number(UPDATED_NUMBER);

        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPart.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPart.getNumber()).isEqualTo(UPDATED_NUMBER);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository).save(testPart);
    }

    @Test
    @Transactional
    void putNonExistingPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, part.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void putWithIdMismatchPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void partialUpdatePartWithPatch() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPart.getNumber()).isEqualTo(DEFAULT_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdatePartWithPatch() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeUpdate = partRepository.findAll().size();

        // Update the part using partial update
        Part partialUpdatedPart = new Part();
        partialUpdatedPart.setId(part.getId());

        partialUpdatedPart.description(UPDATED_DESCRIPTION).number(UPDATED_NUMBER);

        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPart))
            )
            .andExpect(status().isOk());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);
        Part testPart = partList.get(partList.size() - 1);
        assertThat(testPart.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPart.getNumber()).isEqualTo(UPDATED_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, part.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(part))
            )
            .andExpect(status().isBadRequest());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPart() throws Exception {
        int databaseSizeBeforeUpdate = partRepository.findAll().size();
        part.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(part)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Part in the database
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(0)).save(part);
    }

    @Test
    @Transactional
    void deletePart() throws Exception {
        // Initialize the database
        partRepository.saveAndFlush(part);

        int databaseSizeBeforeDelete = partRepository.findAll().size();

        // Delete the part
        restPartMockMvc
            .perform(delete(ENTITY_API_URL_ID, part.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Part> partList = partRepository.findAll();
        assertThat(partList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Part in Elasticsearch
        verify(mockPartSearchRepository, times(1)).deleteById(part.getId());
    }

    @Test
    @Transactional
    void searchPart() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        partRepository.saveAndFlush(part);
        when(mockPartSearchRepository.search("id:" + part.getId())).thenReturn(Stream.of(part));

        // Search the part
        restPartMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + part.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(part.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)));
    }
}
