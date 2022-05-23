package com.valvad.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.valvad.domain.Compatibility;
import com.valvad.repository.CompatibilityRepository;
import com.valvad.repository.search.CompatibilitySearchRepository;
import com.valvad.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.valvad.domain.Compatibility}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CompatibilityResource {

    private final Logger log = LoggerFactory.getLogger(CompatibilityResource.class);

    private static final String ENTITY_NAME = "compatibility";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompatibilityRepository compatibilityRepository;

    private final CompatibilitySearchRepository compatibilitySearchRepository;

    public CompatibilityResource(
        CompatibilityRepository compatibilityRepository,
        CompatibilitySearchRepository compatibilitySearchRepository
    ) {
        this.compatibilityRepository = compatibilityRepository;
        this.compatibilitySearchRepository = compatibilitySearchRepository;
    }

    /**
     * {@code POST  /compatibilities} : Create a new compatibility.
     *
     * @param compatibility the compatibility to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new compatibility, or with status {@code 400 (Bad Request)} if the compatibility has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/compatibilities")
    public ResponseEntity<Compatibility> createCompatibility(@RequestBody Compatibility compatibility) throws URISyntaxException {
        log.debug("REST request to save Compatibility : {}", compatibility);
        if (compatibility.getId() != null) {
            throw new BadRequestAlertException("A new compatibility cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Compatibility result = compatibilityRepository.save(compatibility);
        compatibilitySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/compatibilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /compatibilities/:id} : Updates an existing compatibility.
     *
     * @param id the id of the compatibility to save.
     * @param compatibility the compatibility to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated compatibility,
     * or with status {@code 400 (Bad Request)} if the compatibility is not valid,
     * or with status {@code 500 (Internal Server Error)} if the compatibility couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/compatibilities/{id}")
    public ResponseEntity<Compatibility> updateCompatibility(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Compatibility compatibility
    ) throws URISyntaxException {
        log.debug("REST request to update Compatibility : {}, {}", id, compatibility);
        if (compatibility.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, compatibility.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!compatibilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        // no save call needed as we have no fields that can be updated
        Compatibility result = compatibility;
        compatibilitySearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, compatibility.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /compatibilities/:id} : Partial updates given fields of an existing compatibility, field will ignore if it is null
     *
     * @param id the id of the compatibility to save.
     * @param compatibility the compatibility to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated compatibility,
     * or with status {@code 400 (Bad Request)} if the compatibility is not valid,
     * or with status {@code 404 (Not Found)} if the compatibility is not found,
     * or with status {@code 500 (Internal Server Error)} if the compatibility couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/compatibilities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Compatibility> partialUpdateCompatibility(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Compatibility compatibility
    ) throws URISyntaxException {
        log.debug("REST request to partial update Compatibility partially : {}, {}", id, compatibility);
        if (compatibility.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, compatibility.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!compatibilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Compatibility> result = compatibilityRepository
            .findById(compatibility.getId())
            .map(existingCompatibility -> {
                return existingCompatibility;
            })
            // .map(compatibilityRepository::save)
            .map(savedCompatibility -> {
                compatibilitySearchRepository.save(savedCompatibility);

                return savedCompatibility;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, compatibility.getId().toString())
        );
    }

    /**
     * {@code GET  /compatibilities} : get all the compatibilities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of compatibilities in body.
     */
    @GetMapping("/compatibilities")
    public List<Compatibility> getAllCompatibilities() {
        log.debug("REST request to get all Compatibilities");
        return compatibilityRepository.findAll();
    }

    /**
     * {@code GET  /compatibilities/:id} : get the "id" compatibility.
     *
     * @param id the id of the compatibility to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the compatibility, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/compatibilities/{id}")
    public ResponseEntity<Compatibility> getCompatibility(@PathVariable Long id) {
        log.debug("REST request to get Compatibility : {}", id);
        Optional<Compatibility> compatibility = compatibilityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(compatibility);
    }

    /**
     * {@code DELETE  /compatibilities/:id} : delete the "id" compatibility.
     *
     * @param id the id of the compatibility to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/compatibilities/{id}")
    public ResponseEntity<Void> deleteCompatibility(@PathVariable Long id) {
        log.debug("REST request to delete Compatibility : {}", id);
        compatibilityRepository.deleteById(id);
        compatibilitySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/compatibilities?query=:query} : search for the compatibility corresponding
     * to the query.
     *
     * @param query the query of the compatibility search.
     * @return the result of the search.
     */
    @GetMapping("/_search/compatibilities")
    public List<Compatibility> searchCompatibilities(@RequestParam String query) {
        log.debug("REST request to search Compatibilities for query {}", query);
        return StreamSupport.stream(compatibilitySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
