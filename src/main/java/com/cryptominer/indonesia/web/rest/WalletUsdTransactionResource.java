package com.cryptominer.indonesia.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import com.cryptominer.indonesia.service.WalletUsdTransactionService;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;
import com.cryptominer.indonesia.service.dto.WalletUsdTransactionCriteria;
import com.cryptominer.indonesia.service.WalletUsdTransactionQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing WalletUsdTransaction.
 */
@RestController
@RequestMapping("/api")
public class WalletUsdTransactionResource {

    private final Logger log = LoggerFactory.getLogger(WalletUsdTransactionResource.class);

    private static final String ENTITY_NAME = "walletUsdTransaction";

    private final WalletUsdTransactionService walletUsdTransactionService;

    private final WalletUsdTransactionQueryService walletUsdTransactionQueryService;

    public WalletUsdTransactionResource(WalletUsdTransactionService walletUsdTransactionService, WalletUsdTransactionQueryService walletUsdTransactionQueryService) {
        this.walletUsdTransactionService = walletUsdTransactionService;
        this.walletUsdTransactionQueryService = walletUsdTransactionQueryService;
    }

    /**
     * POST  /wallet-usd-transactions : Create a new walletUsdTransaction.
     *
     * @param walletUsdTransaction the walletUsdTransaction to create
     * @return the ResponseEntity with status 201 (Created) and with body the new walletUsdTransaction, or with status 400 (Bad Request) if the walletUsdTransaction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wallet-usd-transactions")
    @Timed
    public ResponseEntity<WalletUsdTransaction> createWalletUsdTransaction(@Valid @RequestBody WalletUsdTransaction walletUsdTransaction) throws URISyntaxException {
        log.debug("REST request to save WalletUsdTransaction : {}", walletUsdTransaction);
        if (walletUsdTransaction.getId() != null) {
            throw new BadRequestAlertException("A new walletUsdTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WalletUsdTransaction result = walletUsdTransactionService.save(walletUsdTransaction);
        return ResponseEntity.created(new URI("/api/wallet-usd-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /wallet-usd-transactions : Updates an existing walletUsdTransaction.
     *
     * @param walletUsdTransaction the walletUsdTransaction to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated walletUsdTransaction,
     * or with status 400 (Bad Request) if the walletUsdTransaction is not valid,
     * or with status 500 (Internal Server Error) if the walletUsdTransaction couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/wallet-usd-transactions")
    @Timed
    public ResponseEntity<WalletUsdTransaction> updateWalletUsdTransaction(@Valid @RequestBody WalletUsdTransaction walletUsdTransaction) throws URISyntaxException {
        log.debug("REST request to update WalletUsdTransaction : {}", walletUsdTransaction);
        if (walletUsdTransaction.getId() == null) {
            return createWalletUsdTransaction(walletUsdTransaction);
        }
        WalletUsdTransaction result = walletUsdTransactionService.save(walletUsdTransaction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, walletUsdTransaction.getId().toString()))
            .body(result);
    }

    /**
     * GET  /wallet-usd-transactions : get all the walletUsdTransactions.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of walletUsdTransactions in body
     */
    @GetMapping("/wallet-usd-transactions")
    @Timed
    public ResponseEntity<List<WalletUsdTransaction>> getAllWalletUsdTransactions(WalletUsdTransactionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get WalletUsdTransactions by criteria: {}", criteria);
        Page<WalletUsdTransaction> page = walletUsdTransactionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/wallet-usd-transactions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /wallet-usd-transactions/:id : get the "id" walletUsdTransaction.
     *
     * @param id the id of the walletUsdTransaction to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the walletUsdTransaction, or with status 404 (Not Found)
     */
    @GetMapping("/wallet-usd-transactions/{id}")
    @Timed
    public ResponseEntity<WalletUsdTransaction> getWalletUsdTransaction(@PathVariable Long id) {
        log.debug("REST request to get WalletUsdTransaction : {}", id);
        WalletUsdTransaction walletUsdTransaction = walletUsdTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(walletUsdTransaction));
    }

    /**
     * DELETE  /wallet-usd-transactions/:id : delete the "id" walletUsdTransaction.
     *
     * @param id the id of the walletUsdTransaction to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/wallet-usd-transactions/{id}")
    @Timed
    public ResponseEntity<Void> deleteWalletUsdTransaction(@PathVariable Long id) {
        log.debug("REST request to delete WalletUsdTransaction : {}", id);
        walletUsdTransactionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
