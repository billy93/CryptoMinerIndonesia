package com.cryptominer.indonesia.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.service.PackageCmiService;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;
import com.cryptominer.indonesia.service.dto.PackageCmiCriteria;
import com.cryptominer.indonesia.service.PackageCmiQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PackageCmi.
 */
@RestController
@RequestMapping("/api")
public class PackageCmiResource {

    private final Logger log = LoggerFactory.getLogger(PackageCmiResource.class);

    private static final String ENTITY_NAME = "packageCmi";

    private final PackageCmiService packageCmiService;

    private final PackageCmiQueryService packageCmiQueryService;

    public PackageCmiResource(PackageCmiService packageCmiService, PackageCmiQueryService packageCmiQueryService) {
        this.packageCmiService = packageCmiService;
        this.packageCmiQueryService = packageCmiQueryService;
    }

    /**
     * POST  /package-cmis : Create a new packageCmi.
     *
     * @param packageCmi the packageCmi to create
     * @return the ResponseEntity with status 201 (Created) and with body the new packageCmi, or with status 400 (Bad Request) if the packageCmi has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/package-cmis")
    @Timed
    public ResponseEntity<PackageCmi> createPackageCmi(@RequestBody PackageCmi packageCmi) throws URISyntaxException {
        log.debug("REST request to save PackageCmi : {}", packageCmi);
        if (packageCmi.getId() != null) {
            throw new BadRequestAlertException("A new packageCmi cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PackageCmi result = packageCmiService.save(packageCmi);
        return ResponseEntity.created(new URI("/api/package-cmis/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /package-cmis : Updates an existing packageCmi.
     *
     * @param packageCmi the packageCmi to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated packageCmi,
     * or with status 400 (Bad Request) if the packageCmi is not valid,
     * or with status 500 (Internal Server Error) if the packageCmi couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/package-cmis")
    @Timed
    public ResponseEntity<PackageCmi> updatePackageCmi(@RequestBody PackageCmi packageCmi) throws URISyntaxException {
        log.debug("REST request to update PackageCmi : {}", packageCmi);
        if (packageCmi.getId() == null) {
            return createPackageCmi(packageCmi);
        }
        PackageCmi result = packageCmiService.save(packageCmi);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, packageCmi.getId().toString()))
            .body(result);
    }

    /**
     * GET  /package-cmis : get all the packageCmis.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of packageCmis in body
     */
    @GetMapping("/package-cmis")
    @Timed
    public ResponseEntity<List<PackageCmi>> getAllPackageCmis(PackageCmiCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PackageCmis by criteria: {}", criteria);
        Page<PackageCmi> page = packageCmiQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/package-cmis");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /package-cmis/:id : get the "id" packageCmi.
     *
     * @param id the id of the packageCmi to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the packageCmi, or with status 404 (Not Found)
     */
    @GetMapping("/package-cmis/{id}")
    @Timed
    public ResponseEntity<PackageCmi> getPackageCmi(@PathVariable Long id) {
        log.debug("REST request to get PackageCmi : {}", id);
        PackageCmi packageCmi = packageCmiService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(packageCmi));
    }

    /**
     * DELETE  /package-cmis/:id : delete the "id" packageCmi.
     *
     * @param id the id of the packageCmi to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/package-cmis/{id}")
    @Timed
    public ResponseEntity<Void> deletePackageCmi(@PathVariable Long id) {
        log.debug("REST request to delete PackageCmi : {}", id);
        packageCmiService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
