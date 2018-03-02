package com.cryptominer.indonesia.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.service.WalletBtcTransactionService;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;
import com.cryptominer.indonesia.service.dto.WalletBtcTransactionCriteria;
import com.cryptominer.indonesia.service.WalletBtcTransactionQueryService;
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

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing WalletBtcTransaction.
 */
@RestController
@RequestMapping("/api")
public class WalletBtcTransactionResource {

    private final Logger log = LoggerFactory.getLogger(WalletBtcTransactionResource.class);

    private static final String ENTITY_NAME = "walletBtcTransaction";

    private final WalletBtcTransactionService walletBtcTransactionService;

    private final WalletBtcTransactionQueryService walletBtcTransactionQueryService;

    public WalletBtcTransactionResource(WalletBtcTransactionService walletBtcTransactionService, WalletBtcTransactionQueryService walletBtcTransactionQueryService) {
        this.walletBtcTransactionService = walletBtcTransactionService;
        this.walletBtcTransactionQueryService = walletBtcTransactionQueryService;
    }

    /**
     * POST  /wallet-btc-transactions : Create a new walletBtcTransaction.
     *
     * @param walletBtcTransaction the walletBtcTransaction to create
     * @return the ResponseEntity with status 201 (Created) and with body the new walletBtcTransaction, or with status 400 (Bad Request) if the walletBtcTransaction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wallet-btc-transactions")
    @Timed
    public ResponseEntity<WalletBtcTransaction> createWalletBtcTransaction(@Valid @RequestBody WalletBtcTransaction walletBtcTransaction) throws URISyntaxException {
        log.debug("REST request to save WalletBtcTransaction : {}", walletBtcTransaction);
        if (walletBtcTransaction.getId() != null) {
            throw new BadRequestAlertException("A new walletBtcTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        walletBtcTransaction.setAmount(new BigDecimal("0,000018"));
        WalletBtcTransaction result = walletBtcTransactionService.save(walletBtcTransaction);
        return ResponseEntity.created(new URI("/api/wallet-btc-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /wallet-btc-transactions : Updates an existing walletBtcTransaction.
     *
     * @param walletBtcTransaction the walletBtcTransaction to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated walletBtcTransaction,
     * or with status 400 (Bad Request) if the walletBtcTransaction is not valid,
     * or with status 500 (Internal Server Error) if the walletBtcTransaction couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/wallet-btc-transactions")
    @Timed
    public ResponseEntity<WalletBtcTransaction> updateWalletBtcTransaction(@Valid @RequestBody WalletBtcTransaction walletBtcTransaction) throws URISyntaxException {
        log.debug("REST request to update WalletBtcTransaction : {}", walletBtcTransaction);
        if (walletBtcTransaction.getId() == null) {
            return createWalletBtcTransaction(walletBtcTransaction);
        }
        WalletBtcTransaction result = walletBtcTransactionService.save(walletBtcTransaction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, walletBtcTransaction.getId().toString()))
            .body(result);
    }

    /**
     * GET  /wallet-btc-transactions : get all the walletBtcTransactions.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of walletBtcTransactions in body
     */
    @GetMapping("/wallet-btc-transactions")
    @Timed
    public ResponseEntity<List<WalletBtcTransaction>> getAllWalletBtcTransactions(WalletBtcTransactionCriteria criteria, Pageable pageable) {
        log.debug("REST request to get WalletBtcTransactions by criteria: {}", criteria);
        Page<WalletBtcTransaction> page = walletBtcTransactionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/wallet-btc-transactions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /wallet-btc-transactions/:id : get the "id" walletBtcTransaction.
     *
     * @param id the id of the walletBtcTransaction to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the walletBtcTransaction, or with status 404 (Not Found)
     */
    @GetMapping("/wallet-btc-transactions/{id}")
    @Timed
    public ResponseEntity<WalletBtcTransaction> getWalletBtcTransaction(@PathVariable Long id) {
        log.debug("REST request to get WalletBtcTransaction : {}", id);
        WalletBtcTransaction walletBtcTransaction = walletBtcTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(walletBtcTransaction));
    }

    /**
     * DELETE  /wallet-btc-transactions/:id : delete the "id" walletBtcTransaction.
     *
     * @param id the id of the walletBtcTransaction to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/wallet-btc-transactions/{id}")
    @Timed
    public ResponseEntity<Void> deleteWalletBtcTransaction(@PathVariable Long id) {
        log.debug("REST request to delete WalletBtcTransaction : {}", id);
        walletBtcTransactionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
