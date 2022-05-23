package com.valvad.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.valvad.IntegrationTest;
import com.valvad.domain.Photo;
import com.valvad.repository.PhotoRepository;
import com.valvad.repository.search.PhotoSearchRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PhotoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PhotoResourceIT {

    private static final byte[] DEFAULT_BINARY = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BINARY = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BINARY_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BINARY_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/photos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/photos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PhotoRepository photoRepository;

    /**
     * This repository is mocked in the com.valvad.repository.search test package.
     *
     * @see com.valvad.repository.search.PhotoSearchRepositoryMockConfiguration
     */
    @Autowired
    private PhotoSearchRepository mockPhotoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPhotoMockMvc;

    private Photo photo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Photo createEntity(EntityManager em) {
        Photo photo = new Photo().binary(DEFAULT_BINARY).binaryContentType(DEFAULT_BINARY_CONTENT_TYPE).description(DEFAULT_DESCRIPTION);
        return photo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Photo createUpdatedEntity(EntityManager em) {
        Photo photo = new Photo().binary(UPDATED_BINARY).binaryContentType(UPDATED_BINARY_CONTENT_TYPE).description(UPDATED_DESCRIPTION);
        return photo;
    }

    @BeforeEach
    public void initTest() {
        photo = createEntity(em);
    }

    @Test
    @Transactional
    void createPhoto() throws Exception {
        int databaseSizeBeforeCreate = photoRepository.findAll().size();
        // Create the Photo
        restPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photo)))
            .andExpect(status().isCreated());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeCreate + 1);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getBinary()).isEqualTo(DEFAULT_BINARY);
        assertThat(testPhoto.getBinaryContentType()).isEqualTo(DEFAULT_BINARY_CONTENT_TYPE);
        assertThat(testPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(1)).save(testPhoto);
    }

    @Test
    @Transactional
    void createPhotoWithExistingId() throws Exception {
        // Create the Photo with an existing ID
        photo.setId(1L);

        int databaseSizeBeforeCreate = photoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photo)))
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeCreate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = photoRepository.findAll().size();
        // set the field null
        photo.setDescription(null);

        // Create the Photo, which fails.

        restPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photo)))
            .andExpect(status().isBadRequest());

        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPhotos() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].binaryContentType").value(hasItem(DEFAULT_BINARY_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].binary").value(hasItem(Base64Utils.encodeToString(DEFAULT_BINARY))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getPhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get the photo
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL_ID, photo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(photo.getId().intValue()))
            .andExpect(jsonPath("$.binaryContentType").value(DEFAULT_BINARY_CONTENT_TYPE))
            .andExpect(jsonPath("$.binary").value(Base64Utils.encodeToString(DEFAULT_BINARY)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingPhoto() throws Exception {
        // Get the photo
        restPhotoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo
        Photo updatedPhoto = photoRepository.findById(photo.getId()).get();
        // Disconnect from session so that the updates on updatedPhoto are not directly saved in db
        em.detach(updatedPhoto);
        updatedPhoto.binary(UPDATED_BINARY).binaryContentType(UPDATED_BINARY_CONTENT_TYPE).description(UPDATED_DESCRIPTION);

        restPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPhoto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPhoto))
            )
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getBinary()).isEqualTo(UPDATED_BINARY);
        assertThat(testPhoto.getBinaryContentType()).isEqualTo(UPDATED_BINARY_CONTENT_TYPE);
        assertThat(testPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository).save(testPhoto);
    }

    @Test
    @Transactional
    void putNonExistingPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, photo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(photo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void putWithIdMismatchPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(photo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void partialUpdatePhotoWithPatch() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo using partial update
        Photo partialUpdatedPhoto = new Photo();
        partialUpdatedPhoto.setId(photo.getId());

        partialUpdatedPhoto.binary(UPDATED_BINARY).binaryContentType(UPDATED_BINARY_CONTENT_TYPE);

        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPhoto))
            )
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getBinary()).isEqualTo(UPDATED_BINARY);
        assertThat(testPhoto.getBinaryContentType()).isEqualTo(UPDATED_BINARY_CONTENT_TYPE);
        assertThat(testPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdatePhotoWithPatch() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo using partial update
        Photo partialUpdatedPhoto = new Photo();
        partialUpdatedPhoto.setId(photo.getId());

        partialUpdatedPhoto.binary(UPDATED_BINARY).binaryContentType(UPDATED_BINARY_CONTENT_TYPE).description(UPDATED_DESCRIPTION);

        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPhoto))
            )
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getBinary()).isEqualTo(UPDATED_BINARY);
        assertThat(testPhoto.getBinaryContentType()).isEqualTo(UPDATED_BINARY_CONTENT_TYPE);
        assertThat(testPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, photo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(photo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(photo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(photo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void deletePhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeDelete = photoRepository.findAll().size();

        // Delete the photo
        restPhotoMockMvc
            .perform(delete(ENTITY_API_URL_ID, photo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(1)).deleteById(photo.getId());
    }

    @Test
    @Transactional
    void searchPhoto() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        photoRepository.saveAndFlush(photo);
        when(mockPhotoSearchRepository.search("id:" + photo.getId())).thenReturn(Stream.of(photo));

        // Search the photo
        restPhotoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + photo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].binaryContentType").value(hasItem(DEFAULT_BINARY_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].binary").value(hasItem(Base64Utils.encodeToString(DEFAULT_BINARY))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
