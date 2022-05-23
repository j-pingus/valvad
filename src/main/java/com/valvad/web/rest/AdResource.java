package com.valvad.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.valvad.domain.Ad;
import com.valvad.repository.AdRepository;
import com.valvad.repository.search.AdSearchRepository;
import com.valvad.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.valvad.domain.Ad}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AdResource {

    private final Logger log = LoggerFactory.getLogger(AdResource.class);

    private static final String ENTITY_NAME = "ad";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdRepository adRepository;

    private final AdSearchRepository adSearchRepository;

    public AdResource(AdRepository adRepository, AdSearchRepository adSearchRepository) {
        this.adRepository = adRepository;
        this.adSearchRepository = adSearchRepository;
    }

    /**
     * {@code POST  /ads} : Create a new ad.
     *
     * @param ad the ad to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ad, or with status {@code 400 (Bad Request)} if the ad has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ads")
    public ResponseEntity<Ad> createAd(@Valid @RequestBody Ad ad) throws URISyntaxException {
        log.debug("REST request to save Ad : {}", ad);
        if (ad.getId() != null) {
            throw new BadRequestAlertException("A new ad cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ad result = adRepository.save(ad);
        adSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/ads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ads/:id} : Updates an existing ad.
     *
     * @param id the id of the ad to save.
     * @param ad the ad to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ad,
     * or with status {@code 400 (Bad Request)} if the ad is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ad couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ads/{id}")
    public ResponseEntity<Ad> updateAd(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Ad ad)
        throws URISyntaxException {
        log.debug("REST request to update Ad : {}, {}", id, ad);
        if (ad.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ad.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Ad result = adRepository.save(ad);
        adSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ad.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ads/:id} : Partial updates given fields of an existing ad, field will ignore if it is null
     *
     * @param id the id of the ad to save.
     * @param ad the ad to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ad,
     * or with status {@code 400 (Bad Request)} if the ad is not valid,
     * or with status {@code 404 (Not Found)} if the ad is not found,
     * or with status {@code 500 (Internal Server Error)} if the ad couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Ad> partialUpdateAd(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Ad ad)
        throws URISyntaxException {
        log.debug("REST request to partial update Ad partially : {}, {}", id, ad);
        if (ad.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ad.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Ad> result = adRepository
            .findById(ad.getId())
            .map(existingAd -> {
                if (ad.getDescription() != null) {
                    existingAd.setDescription(ad.getDescription());
                }
                if (ad.getQuality() != null) {
                    existingAd.setQuality(ad.getQuality());
                }
                if (ad.getPrice() != null) {
                    existingAd.setPrice(ad.getPrice());
                }

                return existingAd;
            })
            .map(adRepository::save)
            .map(savedAd -> {
                adSearchRepository.save(savedAd);

                return savedAd;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ad.getId().toString())
        );
    }

    /**
     * {@code GET  /ads} : get all the ads.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ads in body.
     */
    @GetMapping("/ads")
    public List<Ad> getAllAds() {
        log.debug("REST request to get all Ads");
        return adRepository.findAll();
    }

    /**
     * {@code GET  /ads/:id} : get the "id" ad.
     *
     * @param id the id of the ad to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ad, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ads/{id}")
    public ResponseEntity<Ad> getAd(@PathVariable Long id) {
        log.debug("REST request to get Ad : {}", id);
        Optional<Ad> ad = adRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ad);
    }

    /**
     * {@code DELETE  /ads/:id} : delete the "id" ad.
     *
     * @param id the id of the ad to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        log.debug("REST request to delete Ad : {}", id);
        adRepository.deleteById(id);
        adSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/ads?query=:query} : search for the ad corresponding
     * to the query.
     *
     * @param query the query of the ad search.
     * @return the result of the search.
     */
    @GetMapping("/_search/ads")
    public List<Ad> searchAds(@RequestParam String query) {
        log.debug("REST request to search Ads for query {}", query);
        return StreamSupport.stream(adSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
