package com.cryptominer.indonesia.web.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.User;
import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import com.cryptominer.indonesia.domain.Withdraw;
import com.cryptominer.indonesia.domain.enumeration.TransactionType;
import com.cryptominer.indonesia.repository.UserRepository;
import com.cryptominer.indonesia.security.SecurityUtils;
import com.cryptominer.indonesia.service.WalletBtcTransactionQueryService;
import com.cryptominer.indonesia.service.WalletBtcTransactionService;
import com.cryptominer.indonesia.service.dto.WalletBtcTransactionCriteria;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;

import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.web.util.ResponseUtil;

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

    private final UserRepository userRepository;
    
    public WalletBtcTransactionResource(WalletBtcTransactionService walletBtcTransactionService, WalletBtcTransactionQueryService walletBtcTransactionQueryService, UserRepository userRepository) {
        this.walletBtcTransactionService = walletBtcTransactionService;
        this.walletBtcTransactionQueryService = walletBtcTransactionQueryService;
        this.userRepository = userRepository;
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
        
        //sender
        User u = userRepository.findOneByLogin(walletBtcTransaction.getUsername()).get();
        if(walletBtcTransaction.getType() == TransactionType.DEPOSIT) {
        		u.setBtcAmount(u.getBtcAmount().add(walletBtcTransaction.getAmount()));
        }
        else if(walletBtcTransaction.getType() == TransactionType.WITHDRAW) {
        		u.setBtcAmount(u.getBtcAmount().subtract(walletBtcTransaction.getAmount()));
        }
        userRepository.save(u);
        
        log.debug("REST request BTC AMOUNT : {}", u.getBtcAmount());
        
        WalletBtcTransaction result = walletBtcTransactionService.save(walletBtcTransaction);
        return ResponseEntity.created(new URI("/api/wallet-btc-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * POST  /wallet-usd-transactions/transfer : Transfer.
     *
     * @param walletUsdTransaction the walletUsdTransaction to create
     * @return the ResponseEntity with status 201 (Created) and with body the new walletUsdTransaction, or with status 400 (Bad Request) if the walletUsdTransaction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wallet-btc-transactions/transfer")
    @Timed
    public ResponseEntity<WalletBtcTransaction> transfer(@Valid @RequestBody WalletBtcTransaction walletBtcTransaction) throws URISyntaxException {
        log.debug("REST request to save walletBtcTransaction : {}", walletBtcTransaction);
        if (walletBtcTransaction.getId() != null) {
            throw new BadRequestAlertException("A new walletUsdTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        BigDecimal totalTransfer = walletBtcTransaction.getAmount().add(new BigDecimal("0.0005"));
	      
        User from = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        from.setBtcAmount(from.getBtcAmount().subtract(totalTransfer));
        if(from.getBtcAmount().doubleValue() < 0) {
        		throw new BadRequestAlertException("Insufficient Amount", ENTITY_NAME, "insufficient");
        }
        else {
	        userRepository.save(from);
	        
	        WalletBtcTransaction x = new WalletBtcTransaction();
	        x.setAmount(walletBtcTransaction.getAmount());
	        x.setFromUsername(SecurityUtils.getCurrentUserLogin().get());
	        x.setUsername(walletBtcTransaction.getUsername());
	        x.setType(TransactionType.DEPOSIT);
	        x.setStatus("COMPLETE");
	        walletBtcTransactionService.save(x);
	        
	        WalletBtcTransaction y = new WalletBtcTransaction();
	        y.setAmount(walletBtcTransaction.getAmount());
	        y.setUsername(SecurityUtils.getCurrentUserLogin().get());
	        y.setToUsername(walletBtcTransaction.getUsername());
	        y.setType(TransactionType.TRANSFER);
	        y.setFee(new BigDecimal("0.0005"));
	        y.setStatus("COMPLETE");
	        walletBtcTransactionService.save(y);
	        
//	        WalletUsdTransaction z = new WalletUsdTransaction();
//	        z.setAmount(new BigDecimal(walletBtcTransaction.getAmount().intValue() * 5 / 100));
//	        z.setUsername("itdev");
//	        z.setType(TransactionType.FEE);
//	        z.setStatus("COMPLETE");
//	        walletBtcTransactionService.save(z);
	        
//	        User itdev = userRepository.findOneByLogin("itdev").get();
//	        itdev.setUsdAmount(itdev.getUsdAmount().add(new BigDecimal(walletUsdTransaction.getAmount().intValue() * 5 / 100)));
//	        userRepository.save(itdev);  
	        
	        User to = userRepository.findOneByLogin(walletBtcTransaction.getUsername()).get();
	        to.setBtcAmount(to.getBtcAmount().add(walletBtcTransaction.getAmount()));
	        userRepository.save(to);
        }
        
        return ResponseEntity.ok().body(walletBtcTransaction);
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
        
        StringFilter sf = new StringFilter();
        sf.setEquals(SecurityUtils.getCurrentUserLogin().get());
        criteria.setUsername(sf);
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
