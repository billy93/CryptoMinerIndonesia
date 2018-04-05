package com.cryptominer.indonesia.web.rest;

import java.math.BigDecimal;
import java.net.URI;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.domain.User;
import com.cryptominer.indonesia.domain.UserReferral;
import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import com.cryptominer.indonesia.domain.enumeration.TransactionType;
import com.cryptominer.indonesia.repository.UserReferralRepository;
import com.cryptominer.indonesia.repository.UserRepository;
import com.cryptominer.indonesia.security.SecurityUtils;
import com.cryptominer.indonesia.service.PackageCmiQueryService;
import com.cryptominer.indonesia.service.PackageCmiService;
import com.cryptominer.indonesia.service.WalletUsdTransactionService;
import com.cryptominer.indonesia.service.dto.PackageCmiCriteria;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;

import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.web.util.ResponseUtil;

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

    private final WalletUsdTransactionService walletUsdTransactionService;

    private final UserRepository userRepository;

    private final UserReferralRepository userReferralRepository;

    public PackageCmiResource(PackageCmiService packageCmiService, PackageCmiQueryService packageCmiQueryService, UserRepository userRepository, WalletUsdTransactionService walletUsdTransactionService, UserReferralRepository userReferralRepository) {
        this.packageCmiService = packageCmiService;
        this.packageCmiQueryService = packageCmiQueryService;
        this.userRepository = userRepository;
        this.walletUsdTransactionService = walletUsdTransactionService;
        this.userReferralRepository = userReferralRepository;
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
        
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        if(user.isEnabled()) {
	        final Totp totp = new Totp(user.getSecret());
	        if (!isValidLong(packageCmi.getGauth()) || !totp.verify(packageCmi.getGauth())) {
	            throw new BadRequestAlertException("Invalid verfication code", ENTITY_NAME, "invalidverificationcode");
	        }
        }
        
        PackageCmi result = packageCmiService.save(packageCmi);
        
        WalletUsdTransaction walletUsd = new WalletUsdTransaction();
        walletUsd.setAmount(packageCmi.getAmount());
        walletUsd.setUsername(SecurityUtils.getCurrentUserLogin().get());
        walletUsd.setToUsername(packageCmi.getUsername());
        walletUsd.setType(TransactionType.BUY_PACKAGE);
        walletUsdTransactionService.save(walletUsd);
        
        User from = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        from.setUsdAmount(from.getUsdAmount().subtract(packageCmi.getAmount()));
        if(from.getUsdAmount().intValue() < 0) {
        		throw new BadRequestAlertException("Insufficient Amount", ENTITY_NAME, "insufficient");
        }
        else {
	        userRepository.save(from);
	        
	        //Bonus Sponsor
	        String username = packageCmi.getUsername();
	        int i=0;
	        do {
		        UserReferral userReferral = userReferralRepository.findByReferral(username);
		        username = userReferral.getUsername();
		        i++;
		        User u = userRepository.findOneByLogin(username).get();
		        if(i == 1) {
		        	BigDecimal amount = packageCmi.getAmount().multiply(new BigDecimal("10")).divide(new BigDecimal("100"));
		            u.setUsdAmount(u.getUsdAmount().add(amount));
		            
		            WalletUsdTransaction w = new WalletUsdTransaction();
		            w.setAmount(amount);
		            w.setUsername(username);
		            w.setFromUsername(packageCmi.getUsername());
//		            w.setToUsername(packageCmi.getUsername());
		            w.setType(TransactionType.BONUS_SPONSOR_1);
		            walletUsdTransactionService.save(w);

		        } else if(i == 2) {
		        	BigDecimal amount = packageCmi.getAmount().multiply(new BigDecimal("5")).divide(new BigDecimal("100"));
		            u.setUsdAmount(u.getUsdAmount().add(amount));		        	
		            
		            WalletUsdTransaction w = new WalletUsdTransaction();
		            w.setAmount(amount);
		            w.setUsername(username);
		            w.setFromUsername(packageCmi.getUsername());
//		            w.setToUsername(packageCmi.getUsername());
		            w.setType(TransactionType.BONUS_SPONSOR_2);
		            walletUsdTransactionService.save(w);
		        } else if(i == 3) {
		        	BigDecimal amount = packageCmi.getAmount().multiply(new BigDecimal("3")).divide(new BigDecimal("100"));
		            u.setUsdAmount(u.getUsdAmount().add(amount));		        	
		            
		            WalletUsdTransaction w = new WalletUsdTransaction();
		            w.setAmount(amount);
		            w.setUsername(username);
		            w.setFromUsername(packageCmi.getUsername());
//		            w.setToUsername(packageCmi.getUsername());
		            w.setType(TransactionType.BONUS_SPONSOR_3);
		            walletUsdTransactionService.save(w);
		        } else if(i == 4) {
		        	BigDecimal amount = packageCmi.getAmount().multiply(new BigDecimal("2")).divide(new BigDecimal("100"));
		            u.setUsdAmount(u.getUsdAmount().add(amount));		        	
		            WalletUsdTransaction w = new WalletUsdTransaction();
		            w.setAmount(amount);
		            w.setUsername(username);
		            w.setFromUsername(packageCmi.getUsername());
//		            w.setToUsername(packageCmi.getUsername());
		            w.setType(TransactionType.BONUS_SPONSOR_4);
		            walletUsdTransactionService.save(w);
		        } else if(i == 5) {
		        	BigDecimal amount = packageCmi.getAmount().multiply(new BigDecimal("1")).divide(new BigDecimal("100"));
		            u.setUsdAmount(u.getUsdAmount().add(amount));		        	
		            WalletUsdTransaction w = new WalletUsdTransaction();
		            w.setAmount(amount);
		            w.setUsername(username);
		            w.setFromUsername(packageCmi.getUsername());
//		            w.setToUsername(packageCmi.getUsername());
		            w.setType(TransactionType.BONUS_SPONSOR_5);
		            walletUsdTransactionService.save(w);
		        }
		        userRepository.save(u);
	        } while (!username.contentEquals("admin"));
	        
	        //Reward
	        User current = userRepository.findOneByLogin(packageCmi.getUsername()).get();
//	        UserReferral userReferral = userReferralRepository.findByReferral(packageCmi.getUsername());
	        String usernameLeader = current.getUpline();
	        List<UserReferral> userRef = userReferralRepository.findAllByUsername(usernameLeader);
	        
	        boolean getReward = false;
	        int refIndex=0;
	        System.out.println("USER REF SIZE : "+userRef.size());
	        if(userRef.size() >= 10) {
	        	for(UserReferral u : userRef) {
		        	refIndex++;
		        	if(refIndex >= 10) {
			        	System.out.println("Check Referral Index : "+u.getReferral() +" | "+packageCmi.getUsername());
		        		if(u.getReferral().contentEquals(packageCmi.getUsername())) {
		        			getReward = true;
		        			break;
		        		}
		        	}
		        }
	        }
	        
	        System.out.println("CHECK REF INDEX : "+refIndex);
	        System.out.println("GET REWARD : "+getReward);
	        if(getReward) {
	        	 User leader = userRepository.findOneByLogin(usernameLeader).get();
	        	 
	        	 BigDecimal amount = packageCmi.getAmount().multiply(new BigDecimal("5")).divide(new BigDecimal("100"));
	        	 leader.setUsdAmount(leader.getUsdAmount().add(amount));
	            
	        	 WalletUsdTransaction w = new WalletUsdTransaction();
	        	 w.setAmount(amount);
	        	 w.setUsername(usernameLeader);
		         w.setType(TransactionType.REWARD);
		         walletUsdTransactionService.save(w);	             
		         
		         userRepository.save(leader);
	        }
        }
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
        
        StringFilter filterUsername = new StringFilter();
        filterUsername.setEquals(SecurityUtils.getCurrentUserLogin().get());
        criteria.setUsername(filterUsername);
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
