package com.cryptominer.indonesia.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.Testing;

import com.cryptominer.indonesia.repository.TestingRepository;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Testing.
 */
@RestController
@RequestMapping("/api")
public class TestingResource {

    private final Logger log = LoggerFactory.getLogger(TestingResource.class);

    private static final String ENTITY_NAME = "testing";

    private final TestingRepository testingRepository;

    public TestingResource(TestingRepository testingRepository) {
        this.testingRepository = testingRepository;
    }

    /**
     * POST  /testings : Create a new testing.
     *
     * @param testing the testing to create
     * @return the ResponseEntity with status 201 (Created) and with body the new testing, or with status 400 (Bad Request) if the testing has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/testings")
    @Timed
    public ResponseEntity<Testing> createTesting(@Valid @RequestBody Testing testing) throws URISyntaxException {
        log.debug("REST request to save Testing : {}", testing);
        if (testing.getId() != null) {
            throw new BadRequestAlertException("A new testing cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Testing result = testingRepository.save(testing);
        return ResponseEntity.created(new URI("/api/testings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /testings : Updates an existing testing.
     *
     * @param testing the testing to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated testing,
     * or with status 400 (Bad Request) if the testing is not valid,
     * or with status 500 (Internal Server Error) if the testing couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/testings")
    @Timed
    public ResponseEntity<Testing> updateTesting(@Valid @RequestBody Testing testing) throws URISyntaxException {
        log.debug("REST request to update Testing : {}", testing);
        if (testing.getId() == null) {
            return createTesting(testing);
        }
        Testing result = testingRepository.save(testing);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, testing.getId().toString()))
            .body(result);
    }

    /**
     * GET  /testings : get all the testings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of testings in body
     */
    @GetMapping("/testings")
    @Timed
    public List<Testing> getAllTestings() {
        log.debug("REST request to get all Testings");
        return testingRepository.findAll();
        }

    /**
     * GET  /testings/:id : get the "id" testing.
     *
     * @param id the id of the testing to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the testing, or with status 404 (Not Found)
     */
    @GetMapping("/testings/{id}")
    @Timed
    public ResponseEntity<Testing> getTesting(@PathVariable Long id) {
        log.debug("REST request to get Testing : {}", id);
        Testing testing = testingRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(testing));
    }

    /**
     * DELETE  /testings/:id : delete the "id" testing.
     *
     * @param id the id of the testing to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/testings/{id}")
    @Timed
    public ResponseEntity<Void> deleteTesting(@PathVariable Long id) {
        log.debug("REST request to delete Testing : {}", id);
        testingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
