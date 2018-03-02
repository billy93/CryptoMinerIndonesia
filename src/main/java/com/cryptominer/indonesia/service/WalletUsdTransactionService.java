package com.cryptominer.indonesia.service;

import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import com.cryptominer.indonesia.repository.WalletUsdTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing WalletUsdTransaction.
 */
@Service
@Transactional
public class WalletUsdTransactionService {

    private final Logger log = LoggerFactory.getLogger(WalletUsdTransactionService.class);

    private final WalletUsdTransactionRepository walletUsdTransactionRepository;

    public WalletUsdTransactionService(WalletUsdTransactionRepository walletUsdTransactionRepository) {
        this.walletUsdTransactionRepository = walletUsdTransactionRepository;
    }

    /**
     * Save a walletUsdTransaction.
     *
     * @param walletUsdTransaction the entity to save
     * @return the persisted entity
     */
    public WalletUsdTransaction save(WalletUsdTransaction walletUsdTransaction) {
        log.debug("Request to save WalletUsdTransaction : {}", walletUsdTransaction);
        return walletUsdTransactionRepository.save(walletUsdTransaction);
    }

    /**
     * Get all the walletUsdTransactions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<WalletUsdTransaction> findAll(Pageable pageable) {
        log.debug("Request to get all WalletUsdTransactions");
        return walletUsdTransactionRepository.findAll(pageable);
    }

    /**
     * Get one walletUsdTransaction by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public WalletUsdTransaction findOne(Long id) {
        log.debug("Request to get WalletUsdTransaction : {}", id);
        return walletUsdTransactionRepository.findOne(id);
    }

    /**
     * Delete the walletUsdTransaction by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete WalletUsdTransaction : {}", id);
        walletUsdTransactionRepository.delete(id);
    }
}
