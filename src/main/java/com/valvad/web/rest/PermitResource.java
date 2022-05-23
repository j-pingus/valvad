package com.valvad.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.valvad.domain.Permit;
import com.valvad.repository.PermitRepository;
import com.valvad.repository.search.PermitSearchRepository;
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
 * REST controller for managing {@link com.valvad.domain.Permit}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PermitResource {

    private final Logger log = LoggerFactory.getLogger(PermitResource.class);

    private static final String ENTITY_NAME = "permit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PermitRepository permitRepository;

    private final PermitSearchRepository permitSearchRepository;

    public PermitResource(PermitRepository permitRepository, PermitSearchRepository permitSearchRepository) {
        this.permitRepository = permitRepository;
        this.permitSearchRepository = permitSearchRepository;
    }

    /**
     * {@code POST  /permits} : Create a new permit.
     *
     * @param permit the permit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new permit, or with status {@code 400 (Bad Request)} if the permit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/permits")
    public ResponseEntity<Permit> createPermit(@Valid @RequestBody Permit permit) throws URISyntaxException {
        log.debug("REST request to save Permit : {}", permit);
        if (permit.getId() != null) {
            throw new BadRequestAlertException("A new permit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Permit result = permitRepository.save(permit);
        permitSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/permits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /permits/:id} : Updates an existing permit.
     *
     * @param id the id of the permit to save.
     * @param permit the permit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permit,
     * or with status {@code 400 (Bad Request)} if the permit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the permit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/permits/{id}")
    public ResponseEntity<Permit> updatePermit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Permit permit
    ) throws URISyntaxException {
        log.debug("REST request to update Permit : {}, {}", id, permit);
        if (permit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!permitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Permit result = permitRepository.save(permit);
        permitSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, permit.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /permits/:id} : Partial updates given fields of an existing permit, field will ignore if it is null
     *
     * @param id the id of the permit to save.
     * @param permit the permit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permit,
     * or with status {@code 400 (Bad Request)} if the permit is not valid,
     * or with status {@code 404 (Not Found)} if the permit is not found,
     * or with status {@code 500 (Internal Server Error)} if the permit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/permits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Permit> partialUpdatePermit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Permit permit
    ) throws URISyntaxException {
        log.debug("REST request to partial update Permit partially : {}, {}", id, permit);
        if (permit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!permitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Permit> result = permitRepository
            .findById(permit.getId())
            .map(existingPermit -> {
                if (permit.getSubject() != null) {
                    existingPermit.setSubject(permit.getSubject());
                }
                if (permit.getSubjectId() != null) {
                    existingPermit.setSubjectId(permit.getSubjectId());
                }
                if (permit.getRight() != null) {
                    existingPermit.setRight(permit.getRight());
                }

                return existingPermit;
            })
            .map(permitRepository::save)
            .map(savedPermit -> {
                permitSearchRepository.save(savedPermit);

                return savedPermit;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, permit.getId().toString())
        );
    }

    /**
     * {@code GET  /permits} : get all the permits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of permits in body.
     */
    @GetMapping("/permits")
    public List<Permit> getAllPermits() {
        log.debug("REST request to get all Permits");
        return permitRepository.findAll();
    }

    /**
     * {@code GET  /permits/:id} : get the "id" permit.
     *
     * @param id the id of the permit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the permit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/permits/{id}")
    public ResponseEntity<Permit> getPermit(@PathVariable Long id) {
        log.debug("REST request to get Permit : {}", id);
        Optional<Permit> permit = permitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(permit);
    }

    /**
     * {@code DELETE  /permits/:id} : delete the "id" permit.
     *
     * @param id the id of the permit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/permits/{id}")
    public ResponseEntity<Void> deletePermit(@PathVariable Long id) {
        log.debug("REST request to delete Permit : {}", id);
        permitRepository.deleteById(id);
        permitSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/permits?query=:query} : search for the permit corresponding
     * to the query.
     *
     * @param query the query of the permit search.
     * @return the result of the search.
     */
    @GetMapping("/_search/permits")
    public List<Permit> searchPermits(@RequestParam String query) {
        log.debug("REST request to search Permits for query {}", query);
        return StreamSupport.stream(permitSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
