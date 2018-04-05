package com.cryptominer.indonesia.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.jboss.aerogear.security.otp.Totp;
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
import com.cryptominer.indonesia.repository.WalletBtcTransactionRepository;
import com.cryptominer.indonesia.repository.WalletUsdTransactionRepository;
import com.cryptominer.indonesia.repository.WithdrawRepository;
import com.cryptominer.indonesia.security.SecurityUtils;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Withdraw.
 */
@RestController
@RequestMapping("/api")
public class WithdrawResource {

    private final Logger log = LoggerFactory.getLogger(WithdrawResource.class);

    private static final String ENTITY_NAME = "withdraw";

    private final WithdrawRepository withdrawRepository;
    private final UserRepository userRepository;
    private final WalletUsdTransactionRepository walletUsdTransactionRepository;
    private final WalletBtcTransactionRepository walletBtcTransactionRepository;
    
    public WithdrawResource(WithdrawRepository withdrawRepository, UserRepository userRepository, WalletUsdTransactionRepository walletUsdTransactionRepository, WalletBtcTransactionRepository walletBtcTransactionRepository) {
        this.withdrawRepository = withdrawRepository;
        this.userRepository = userRepository;
        this.walletUsdTransactionRepository = walletUsdTransactionRepository;
        this.walletBtcTransactionRepository = walletBtcTransactionRepository;
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
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
        
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(user.isEnabled()) {
	        final Totp totp = new Totp(user.getSecret());
	        if (!isValidLong(withdraw.getGauth()) || !totp.verify(withdraw.getGauth())) {
	            throw new BadRequestAlertException("Invalid verfication code", ENTITY_NAME, "invalidverificationcode");
	        }
        }
        //Withdraw (- current amount)
        User u = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        
        if(withdraw.getType().contentEquals("BTC")) {
        		BigDecimal totalAmount = withdraw.getAmount().add(new BigDecimal("0.0005"));            
        		u.setBtcAmount(u.getBtcAmount().subtract(totalAmount));
	    		
	   		if(u.getBtcAmount().intValue() < 0) {
	   			 throw new BadRequestAlertException("Insufficient Amount", ENTITY_NAME, "insufficient");
	        }
	        else {        
		        withdraw.setUsername(SecurityUtils.getCurrentUserLogin().get());
		        withdraw.setStatus("PENDING");
		        withdraw.setFee(new BigDecimal("0.0005"));
		        
		        WalletBtcTransaction wut = new WalletBtcTransaction();
		        wut.setAmount(withdraw.getAmount());
		        wut.setFee(new BigDecimal("0.0005"));
		        wut.setUsername(SecurityUtils.getCurrentUserLogin().get());
		        wut.setType(TransactionType.WITHDRAW);
		        wut.setStatus("PENDING");	        
		        walletBtcTransactionRepository.save(wut);
		        
		        userRepository.save(u);
		        withdraw.setWalletBtcTransaction(walletBtcTransactionRepository.findOne(wut.getId()));
		        withdrawRepository.save(withdraw);
	        }
        }
        else if(withdraw.getType().contentEquals("USD")) {
        	BigDecimal totalAmount = withdraw.getAmount().add(new BigDecimal(withdraw.getAmount().intValue() * 5 / 100));
        	u.setUsdAmount(u.getUsdAmount().subtract(totalAmount)); //User -5%
        		
        	if(u.getUsdAmount().intValue() < 0) {
         		throw new BadRequestAlertException("Insufficient Amount", ENTITY_NAME, "insufficient");
             }
             else {        
     	        withdraw.setUsername(SecurityUtils.getCurrentUserLogin().get());
     	        withdraw.setStatus("PENDING");
     	        withdraw.setFee(new BigDecimal(withdraw.getAmount().intValue() * 5 / 100));
     	        
     	        WalletUsdTransaction wut = new WalletUsdTransaction();
     	        wut.setAmount(withdraw.getAmount());
     	        wut.setFee(new BigDecimal(withdraw.getAmount().intValue() * 5 / 100));
     	        wut.setUsername(SecurityUtils.getCurrentUserLogin().get());
     	        wut.setType(TransactionType.WITHDRAW);
     	        wut.setStatus("PENDING");	        
     	        walletUsdTransactionRepository.save(wut);
     	        
     	        userRepository.save(u);
     	        withdraw.setWalletUsdTransaction(walletUsdTransactionRepository.findOne(wut.getId()));
     	        withdrawRepository.save(withdraw);
             }
        }
        
       
        return ResponseEntity.ok().body(withdraw);
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
        
        if(withdraw.getStatus().contentEquals("COMPLETE")) {
        		withdraw.getWalletUsdTransaction().setStatus("COMPLETE");
        		walletUsdTransactionRepository.save(withdraw.getWalletUsdTransaction());    
        		
        		 WalletUsdTransaction z = new WalletUsdTransaction();
 	         z.setAmount(new BigDecimal(withdraw.getAmount().intValue() * 1 / 100));
 	         z.setUsername("itdev");
 	         z.setType(TransactionType.FEE);
 	         z.setStatus("SUCCESS");
 	         walletUsdTransactionRepository.save(z);
 	        
 	         User itdev = userRepository.findOneByLogin("itdev").get();
 	         itdev.setUsdAmount(itdev.getUsdAmount().add(new BigDecimal(withdraw.getAmount().intValue() * 1 / 100)));
 	         userRepository.save(itdev);  
        }
        else if(withdraw.getStatus().contentEquals("REJECT")) {
	    		withdraw.getWalletUsdTransaction().setStatus("REJECT");
	    		walletUsdTransactionRepository.save(withdraw.getWalletUsdTransaction());       
	    		
	    		User u = userRepository.findOneByLogin(withdraw.getUsername()).get();
	    		u.setUsdAmount(u.getUsdAmount().add(withdraw.getAmount().add(new BigDecimal(withdraw.getAmount().intValue() * 1 / 100))));
	    		userRepository.save(u);
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
