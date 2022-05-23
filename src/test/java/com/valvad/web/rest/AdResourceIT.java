package com.valvad.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.valvad.IntegrationTest;
import com.valvad.domain.Ad;
import com.valvad.domain.User;
import com.valvad.domain.enumeration.Quality;
import com.valvad.repository.AdRepository;
import com.valvad.repository.search.AdSearchRepository;
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
 * Integration tests for the {@link AdResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AdResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Quality DEFAULT_QUALITY = Quality.UNKNOWN;
    private static final Quality UPDATED_QUALITY = Quality.UNUSED;

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final String ENTITY_API_URL = "/api/ads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/ads";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdRepository adRepository;

    /**
     * This repository is mocked in the com.valvad.repository.search test package.
     *
     * @see com.valvad.repository.search.AdSearchRepositoryMockConfiguration
     */
    @Autowired
    private AdSearchRepository mockAdSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdMockMvc;

    private Ad ad;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ad createEntity(EntityManager em) {
        Ad ad = new Ad().description(DEFAULT_DESCRIPTION).quality(DEFAULT_QUALITY).price(DEFAULT_PRICE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        ad.setPublisher(user);
        return ad;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ad createUpdatedEntity(EntityManager em) {
        Ad ad = new Ad().description(UPDATED_DESCRIPTION).quality(UPDATED_QUALITY).price(UPDATED_PRICE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        ad.setPublisher(user);
        return ad;
    }

    @BeforeEach
    public void initTest() {
        ad = createEntity(em);
    }

    @Test
    @Transactional
    void createAd() throws Exception {
        int databaseSizeBeforeCreate = adRepository.findAll().size();
        // Create the Ad
        restAdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isCreated());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeCreate + 1);
        Ad testAd = adList.get(adList.size() - 1);
        assertThat(testAd.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAd.getQuality()).isEqualTo(DEFAULT_QUALITY);
        assertThat(testAd.getPrice()).isEqualTo(DEFAULT_PRICE);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(1)).save(testAd);
    }

    @Test
    @Transactional
    void createAdWithExistingId() throws Exception {
        // Create the Ad with an existing ID
        ad.setId(1L);

        int databaseSizeBeforeCreate = adRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isBadRequest());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeCreate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = adRepository.findAll().size();
        // set the field null
        ad.setDescription(null);

        // Create the Ad, which fails.

        restAdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isBadRequest());

        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQualityIsRequired() throws Exception {
        int databaseSizeBeforeTest = adRepository.findAll().size();
        // set the field null
        ad.setQuality(null);

        // Create the Ad, which fails.

        restAdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isBadRequest());

        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = adRepository.findAll().size();
        // set the field null
        ad.setPrice(null);

        // Create the Ad, which fails.

        restAdMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isBadRequest());

        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAds() throws Exception {
        // Initialize the database
        adRepository.saveAndFlush(ad);

        // Get all the adList
        restAdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ad.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].quality").value(hasItem(DEFAULT_QUALITY.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    void getAd() throws Exception {
        // Initialize the database
        adRepository.saveAndFlush(ad);

        // Get the ad
        restAdMockMvc
            .perform(get(ENTITY_API_URL_ID, ad.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ad.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.quality").value(DEFAULT_QUALITY.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingAd() throws Exception {
        // Get the ad
        restAdMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAd() throws Exception {
        // Initialize the database
        adRepository.saveAndFlush(ad);

        int databaseSizeBeforeUpdate = adRepository.findAll().size();

        // Update the ad
        Ad updatedAd = adRepository.findById(ad.getId()).get();
        // Disconnect from session so that the updates on updatedAd are not directly saved in db
        em.detach(updatedAd);
        updatedAd.description(UPDATED_DESCRIPTION).quality(UPDATED_QUALITY).price(UPDATED_PRICE);

        restAdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAd.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAd))
            )
            .andExpect(status().isOk());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);
        Ad testAd = adList.get(adList.size() - 1);
        assertThat(testAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAd.getQuality()).isEqualTo(UPDATED_QUALITY);
        assertThat(testAd.getPrice()).isEqualTo(UPDATED_PRICE);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository).save(testAd);
    }

    @Test
    @Transactional
    void putNonExistingAd() throws Exception {
        int databaseSizeBeforeUpdate = adRepository.findAll().size();
        ad.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ad.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void putWithIdMismatchAd() throws Exception {
        int databaseSizeBeforeUpdate = adRepository.findAll().size();
        ad.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ad))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAd() throws Exception {
        int databaseSizeBeforeUpdate = adRepository.findAll().size();
        ad.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void partialUpdateAdWithPatch() throws Exception {
        // Initialize the database
        adRepository.saveAndFlush(ad);

        int databaseSizeBeforeUpdate = adRepository.findAll().size();

        // Update the ad using partial update
        Ad partialUpdatedAd = new Ad();
        partialUpdatedAd.setId(ad.getId());

        partialUpdatedAd.description(UPDATED_DESCRIPTION).quality(UPDATED_QUALITY).price(UPDATED_PRICE);

        restAdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAd))
            )
            .andExpect(status().isOk());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);
        Ad testAd = adList.get(adList.size() - 1);
        assertThat(testAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAd.getQuality()).isEqualTo(UPDATED_QUALITY);
        assertThat(testAd.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateAdWithPatch() throws Exception {
        // Initialize the database
        adRepository.saveAndFlush(ad);

        int databaseSizeBeforeUpdate = adRepository.findAll().size();

        // Update the ad using partial update
        Ad partialUpdatedAd = new Ad();
        partialUpdatedAd.setId(ad.getId());

        partialUpdatedAd.description(UPDATED_DESCRIPTION).quality(UPDATED_QUALITY).price(UPDATED_PRICE);

        restAdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAd))
            )
            .andExpect(status().isOk());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);
        Ad testAd = adList.get(adList.size() - 1);
        assertThat(testAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAd.getQuality()).isEqualTo(UPDATED_QUALITY);
        assertThat(testAd.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingAd() throws Exception {
        int databaseSizeBeforeUpdate = adRepository.findAll().size();
        ad.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ad.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ad))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAd() throws Exception {
        int databaseSizeBeforeUpdate = adRepository.findAll().size();
        ad.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ad))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAd() throws Exception {
        int databaseSizeBeforeUpdate = adRepository.findAll().size();
        ad.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ad)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ad in the database
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(0)).save(ad);
    }

    @Test
    @Transactional
    void deleteAd() throws Exception {
        // Initialize the database
        adRepository.saveAndFlush(ad);

        int databaseSizeBeforeDelete = adRepository.findAll().size();

        // Delete the ad
        restAdMockMvc.perform(delete(ENTITY_API_URL_ID, ad.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ad> adList = adRepository.findAll();
        assertThat(adList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Ad in Elasticsearch
        verify(mockAdSearchRepository, times(1)).deleteById(ad.getId());
    }

    @Test
    @Transactional
    void searchAd() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        adRepository.saveAndFlush(ad);
        when(mockAdSearchRepository.search("id:" + ad.getId())).thenReturn(Stream.of(ad));

        // Search the ad
        restAdMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ad.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ad.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].quality").value(hasItem(DEFAULT_QUALITY.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }
}
