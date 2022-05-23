package com.valvad.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.valvad.IntegrationTest;
import com.valvad.domain.Permit;
import com.valvad.domain.enumeration.Right;
import com.valvad.domain.enumeration.Subject;
import com.valvad.repository.PermitRepository;
import com.valvad.repository.search.PermitSearchRepository;
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
 * Integration tests for the {@link PermitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PermitResourceIT {

    private static final Subject DEFAULT_SUBJECT = Subject.PART;
    private static final Subject UPDATED_SUBJECT = Subject.MODEL;

    private static final Long DEFAULT_SUBJECT_ID = 1L;
    private static final Long UPDATED_SUBJECT_ID = 2L;

    private static final Right DEFAULT_RIGHT = Right.WRITE;
    private static final Right UPDATED_RIGHT = Right.CREATOR;

    private static final String ENTITY_API_URL = "/api/permits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/permits";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PermitRepository permitRepository;

    /**
     * This repository is mocked in the com.valvad.repository.search test package.
     *
     * @see com.valvad.repository.search.PermitSearchRepositoryMockConfiguration
     */
    @Autowired
    private PermitSearchRepository mockPermitSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPermitMockMvc;

    private Permit permit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permit createEntity(EntityManager em) {
        Permit permit = new Permit().subject(DEFAULT_SUBJECT).subjectId(DEFAULT_SUBJECT_ID).right(DEFAULT_RIGHT);
        return permit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permit createUpdatedEntity(EntityManager em) {
        Permit permit = new Permit().subject(UPDATED_SUBJECT).subjectId(UPDATED_SUBJECT_ID).right(UPDATED_RIGHT);
        return permit;
    }

    @BeforeEach
    public void initTest() {
        permit = createEntity(em);
    }

    @Test
    @Transactional
    void createPermit() throws Exception {
        int databaseSizeBeforeCreate = permitRepository.findAll().size();
        // Create the Permit
        restPermitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isCreated());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeCreate + 1);
        Permit testPermit = permitList.get(permitList.size() - 1);
        assertThat(testPermit.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testPermit.getSubjectId()).isEqualTo(DEFAULT_SUBJECT_ID);
        assertThat(testPermit.getRight()).isEqualTo(DEFAULT_RIGHT);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(1)).save(testPermit);
    }

    @Test
    @Transactional
    void createPermitWithExistingId() throws Exception {
        // Create the Permit with an existing ID
        permit.setId(1L);

        int databaseSizeBeforeCreate = permitRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPermitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isBadRequest());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeCreate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = permitRepository.findAll().size();
        // set the field null
        permit.setSubject(null);

        // Create the Permit, which fails.

        restPermitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isBadRequest());

        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubjectIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = permitRepository.findAll().size();
        // set the field null
        permit.setSubjectId(null);

        // Create the Permit, which fails.

        restPermitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isBadRequest());

        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRightIsRequired() throws Exception {
        int databaseSizeBeforeTest = permitRepository.findAll().size();
        // set the field null
        permit.setRight(null);

        // Create the Permit, which fails.

        restPermitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isBadRequest());

        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPermits() throws Exception {
        // Initialize the database
        permitRepository.saveAndFlush(permit);

        // Get all the permitList
        restPermitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(permit.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].subjectId").value(hasItem(DEFAULT_SUBJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].right").value(hasItem(DEFAULT_RIGHT.toString())));
    }

    @Test
    @Transactional
    void getPermit() throws Exception {
        // Initialize the database
        permitRepository.saveAndFlush(permit);

        // Get the permit
        restPermitMockMvc
            .perform(get(ENTITY_API_URL_ID, permit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(permit.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT.toString()))
            .andExpect(jsonPath("$.subjectId").value(DEFAULT_SUBJECT_ID.intValue()))
            .andExpect(jsonPath("$.right").value(DEFAULT_RIGHT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPermit() throws Exception {
        // Get the permit
        restPermitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPermit() throws Exception {
        // Initialize the database
        permitRepository.saveAndFlush(permit);

        int databaseSizeBeforeUpdate = permitRepository.findAll().size();

        // Update the permit
        Permit updatedPermit = permitRepository.findById(permit.getId()).get();
        // Disconnect from session so that the updates on updatedPermit are not directly saved in db
        em.detach(updatedPermit);
        updatedPermit.subject(UPDATED_SUBJECT).subjectId(UPDATED_SUBJECT_ID).right(UPDATED_RIGHT);

        restPermitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPermit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPermit))
            )
            .andExpect(status().isOk());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);
        Permit testPermit = permitList.get(permitList.size() - 1);
        assertThat(testPermit.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testPermit.getSubjectId()).isEqualTo(UPDATED_SUBJECT_ID);
        assertThat(testPermit.getRight()).isEqualTo(UPDATED_RIGHT);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository).save(testPermit);
    }

    @Test
    @Transactional
    void putNonExistingPermit() throws Exception {
        int databaseSizeBeforeUpdate = permitRepository.findAll().size();
        permit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPermitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, permit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(permit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void putWithIdMismatchPermit() throws Exception {
        int databaseSizeBeforeUpdate = permitRepository.findAll().size();
        permit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(permit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPermit() throws Exception {
        int databaseSizeBeforeUpdate = permitRepository.findAll().size();
        permit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void partialUpdatePermitWithPatch() throws Exception {
        // Initialize the database
        permitRepository.saveAndFlush(permit);

        int databaseSizeBeforeUpdate = permitRepository.findAll().size();

        // Update the permit using partial update
        Permit partialUpdatedPermit = new Permit();
        partialUpdatedPermit.setId(permit.getId());

        partialUpdatedPermit.subject(UPDATED_SUBJECT).right(UPDATED_RIGHT);

        restPermitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPermit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPermit))
            )
            .andExpect(status().isOk());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);
        Permit testPermit = permitList.get(permitList.size() - 1);
        assertThat(testPermit.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testPermit.getSubjectId()).isEqualTo(DEFAULT_SUBJECT_ID);
        assertThat(testPermit.getRight()).isEqualTo(UPDATED_RIGHT);
    }

    @Test
    @Transactional
    void fullUpdatePermitWithPatch() throws Exception {
        // Initialize the database
        permitRepository.saveAndFlush(permit);

        int databaseSizeBeforeUpdate = permitRepository.findAll().size();

        // Update the permit using partial update
        Permit partialUpdatedPermit = new Permit();
        partialUpdatedPermit.setId(permit.getId());

        partialUpdatedPermit.subject(UPDATED_SUBJECT).subjectId(UPDATED_SUBJECT_ID).right(UPDATED_RIGHT);

        restPermitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPermit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPermit))
            )
            .andExpect(status().isOk());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);
        Permit testPermit = permitList.get(permitList.size() - 1);
        assertThat(testPermit.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testPermit.getSubjectId()).isEqualTo(UPDATED_SUBJECT_ID);
        assertThat(testPermit.getRight()).isEqualTo(UPDATED_RIGHT);
    }

    @Test
    @Transactional
    void patchNonExistingPermit() throws Exception {
        int databaseSizeBeforeUpdate = permitRepository.findAll().size();
        permit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPermitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, permit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(permit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPermit() throws Exception {
        int databaseSizeBeforeUpdate = permitRepository.findAll().size();
        permit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(permit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPermit() throws Exception {
        int databaseSizeBeforeUpdate = permitRepository.findAll().size();
        permit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(permit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Permit in the database
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(0)).save(permit);
    }

    @Test
    @Transactional
    void deletePermit() throws Exception {
        // Initialize the database
        permitRepository.saveAndFlush(permit);

        int databaseSizeBeforeDelete = permitRepository.findAll().size();

        // Delete the permit
        restPermitMockMvc
            .perform(delete(ENTITY_API_URL_ID, permit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Permit> permitList = permitRepository.findAll();
        assertThat(permitList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Permit in Elasticsearch
        verify(mockPermitSearchRepository, times(1)).deleteById(permit.getId());
    }

    @Test
    @Transactional
    void searchPermit() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        permitRepository.saveAndFlush(permit);
        when(mockPermitSearchRepository.search("id:" + permit.getId())).thenReturn(Stream.of(permit));

        // Search the permit
        restPermitMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + permit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(permit.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].subjectId").value(hasItem(DEFAULT_SUBJECT_ID.intValue())))
            .andExpect(jsonPath("$.[*].right").value(hasItem(DEFAULT_RIGHT.toString())));
    }
}
