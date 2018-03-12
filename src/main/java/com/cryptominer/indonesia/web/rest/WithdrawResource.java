package com.cryptominer.indonesia.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.Withdraw;

import com.cryptominer.indonesia.repository.WithdrawRepository;
import com.cryptominer.indonesia.security.SecurityUtils;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;
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
 * REST controller for managing Withdraw.
 */
@RestController
@RequestMapping("/api")
public class WithdrawResource {

    private final Logger log = LoggerFactory.getLogger(WithdrawResource.class);

    private static final String ENTITY_NAME = "withdraw";

    private final WithdrawRepository withdrawRepository;

    public WithdrawResource(WithdrawRepository withdrawRepository) {
        this.withdrawRepository = withdrawRepository;
    }

    /**
     * POST  /withdraws : Create a new withdraw.
     *
     * @param withdraw the withdraw to create
     * @return the ResponseEntity with status 201 (Created) and with body the new withdraw, or with status 400 (Bad Request) if the withdraw has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/withdraws")
    @Timed
    public ResponseEntity<Withdraw> createWithdraw(@RequestBody Withdraw withdraw) throws URISyntaxException {
        log.debug("REST request to save Withdraw : {}", withdraw);
        if (withdraw.getId() != null) {
            throw new BadRequestAlertException("A new withdraw cannot already have an ID", ENTITY_NAME, "idexists");
        }
        withdraw.setUsername(SecurityUtils.getCurrentUserLogin().get());
        withdraw.setStatus("PENDING");
        Withdraw result = withdrawRepository.save(withdraw);
        return ResponseEntity.created(new URI("/api/withdraws/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /withdraws : Updates an existing withdraw.
     *
     * @param withdraw the withdraw to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated withdraw,
     * or with status 400 (Bad Request) if the withdraw is not valid,
     * or with status 500 (Internal Server Error) if the withdraw couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/withdraws")
    @Timed
    public ResponseEntity<Withdraw> updateWithdraw(@RequestBody Withdraw withdraw) throws URISyntaxException {
        log.debug("REST request to update Withdraw : {}", withdraw);
        if (withdraw.getId() == null) {
            return createWithdraw(withdraw);
        }
        Withdraw result = withdrawRepository.save(withdraw);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, withdraw.getId().toString()))
            .body(result);
    }

    /**
     * GET  /withdraws : get all the withdraws.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of withdraws in body
     */
    @GetMapping("/withdraws")
    @Timed
    public ResponseEntity<List<Withdraw>> getAllWithdraws(Pageable pageable) {
        log.debug("REST request to get a page of Withdraws");
        Page<Withdraw> page = withdrawRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/withdraws");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /withdraws/:id : get the "id" withdraw.
     *
     * @param id the id of the withdraw to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the withdraw, or with status 404 (Not Found)
     */
    @GetMapping("/withdraws/{id}")
    @Timed
    public ResponseEntity<Withdraw> getWithdraw(@PathVariable Long id) {
        log.debug("REST request to get Withdraw : {}", id);
        Withdraw withdraw = withdrawRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(withdraw));
    }

    /**
     * DELETE  /withdraws/:id : delete the "id" withdraw.
     *
     * @param id the id of the withdraw to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/withdraws/{id}")
    @Timed
    public ResponseEntity<Void> deleteWithdraw(@PathVariable Long id) {
        log.debug("REST request to delete Withdraw : {}", id);
        withdrawRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
