package com.cryptominer.indonesia.service;


import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import com.cryptominer.indonesia.domain.*; // for static metamodels
import com.cryptominer.indonesia.repository.WalletUsdTransactionRepository;
import com.cryptominer.indonesia.service.dto.WalletUsdTransactionCriteria;

import com.cryptominer.indonesia.domain.enumeration.TransactionType;

/**
 * Service for executing complex queries for WalletUsdTransaction entities in the database.
 * The main input is a {@link WalletUsdTransactionCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WalletUsdTransaction} or a {@link Page} of {@link WalletUsdTransaction} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WalletUsdTransactionQueryService extends QueryService<WalletUsdTransaction> {

    private final Logger log = LoggerFactory.getLogger(WalletUsdTransactionQueryService.class);


    private final WalletUsdTransactionRepository walletUsdTransactionRepository;

    public WalletUsdTransactionQueryService(WalletUsdTransactionRepository walletUsdTransactionRepository) {
        this.walletUsdTransactionRepository = walletUsdTransactionRepository;
    }

    /**
     * Return a {@link List} of {@link WalletUsdTransaction} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WalletUsdTransaction> findByCriteria(WalletUsdTransactionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<WalletUsdTransaction> specification = createSpecification(criteria);
        return walletUsdTransactionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link WalletUsdTransaction} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WalletUsdTransaction> findByCriteria(WalletUsdTransactionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<WalletUsdTransaction> specification = createSpecification(criteria);
        return walletUsdTransactionRepository.findAll(specification, page);
    }

    /**
     * Function to convert WalletUsdTransactionCriteria to a {@link Specifications}
     */
    private Specifications<WalletUsdTransaction> createSpecification(WalletUsdTransactionCriteria criteria) {
        Specifications<WalletUsdTransaction> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), WalletUsdTransaction_.id));
            }
            if (criteria.getUsername() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUsername(), WalletUsdTransaction_.username));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), WalletUsdTransaction_.amount));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), WalletUsdTransaction_.type));
            }
            if (criteria.getFromUsername() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFromUsername(), WalletUsdTransaction_.fromUsername));
            }
            if (criteria.getTxid() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTxid(), WalletUsdTransaction_.txid));
            }
        }
        return specification;
    }

}
