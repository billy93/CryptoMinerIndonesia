package com.cryptominer.indonesia.service;

import com.cryptominer.indonesia.domain.AbstractAuditingEntity;
import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.repository.WalletBtcTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing WalletBtcTransaction.
 */
@Service
@Transactional
public class WalletBtcTransactionService extends AbstractAuditingEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(WalletBtcTransactionService.class);

    private final WalletBtcTransactionRepository walletBtcTransactionRepository;

    public WalletBtcTransactionService(WalletBtcTransactionRepository walletBtcTransactionRepository) {
        this.walletBtcTransactionRepository = walletBtcTransactionRepository;
    }

    /**
     * Save a walletBtcTransaction.
     *
     * @param walletBtcTransaction the entity to save
     * @return the persisted entity
     */
    public WalletBtcTransaction save(WalletBtcTransaction walletBtcTransaction) {
        log.debug("Request to save WalletBtcTransaction : {}", walletBtcTransaction);
        return walletBtcTransactionRepository.save(walletBtcTransaction);
    }

    /**
     * Get all the walletBtcTransactions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<WalletBtcTransaction> findAll(Pageable pageable) {
        log.debug("Request to get all WalletBtcTransactions");
        return walletBtcTransactionRepository.findAll(pageable);
    }

    /**
     * Get one walletBtcTransaction by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public WalletBtcTransaction findOne(Long id) {
        log.debug("Request to get WalletBtcTransaction : {}", id);
        return walletBtcTransactionRepository.findOne(id);
    }

    /**
     * Delete the walletBtcTransaction by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete WalletBtcTransaction : {}", id);
        walletBtcTransactionRepository.delete(id);
    }
}
