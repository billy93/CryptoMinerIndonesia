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

import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.domain.*; // for static metamodels
import com.cryptominer.indonesia.repository.WalletBtcTransactionRepository;
import com.cryptominer.indonesia.service.dto.WalletBtcTransactionCriteria;

import com.cryptominer.indonesia.domain.enumeration.TransactionType;

/**
 * Service for executing complex queries for WalletBtcTransaction entities in the database.
 * The main input is a {@link WalletBtcTransactionCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WalletBtcTransaction} or a {@link Page} of {@link WalletBtcTransaction} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WalletBtcTransactionQueryService extends QueryService<WalletBtcTransaction> {

    private final Logger log = LoggerFactory.getLogger(WalletBtcTransactionQueryService.class);


    private final WalletBtcTransactionRepository walletBtcTransactionRepository;

    public WalletBtcTransactionQueryService(WalletBtcTransactionRepository walletBtcTransactionRepository) {
        this.walletBtcTransactionRepository = walletBtcTransactionRepository;
    }

    /**
     * Return a {@link List} of {@link WalletBtcTransaction} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WalletBtcTransaction> findByCriteria(WalletBtcTransactionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<WalletBtcTransaction> specification = createSpecification(criteria);
        return walletBtcTransactionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link WalletBtcTransaction} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WalletBtcTransaction> findByCriteria(WalletBtcTransactionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<WalletBtcTransaction> specification = createSpecification(criteria);
        return walletBtcTransactionRepository.findAll(specification, page);
    }

    /**
     * Function to convert WalletBtcTransactionCriteria to a {@link Specifications}
     */
    private Specifications<WalletBtcTransaction> createSpecification(WalletBtcTransactionCriteria criteria) {
        Specifications<WalletBtcTransaction> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), WalletBtcTransaction_.id));
            }
            if (criteria.getUsername() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUsername(), WalletBtcTransaction_.username));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), WalletBtcTransaction_.amount));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), WalletBtcTransaction_.type));
            }
            if (criteria.getFromUsername() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFromUsername(), WalletBtcTransaction_.fromUsername));
            }
            if (criteria.getTxid() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTxid(), WalletBtcTransaction_.txid));
            }
        }
        return specification;
    }

}
