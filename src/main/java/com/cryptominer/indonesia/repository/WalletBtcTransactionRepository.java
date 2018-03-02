package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the WalletBtcTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletBtcTransactionRepository extends JpaRepository<WalletBtcTransaction, Long>, JpaSpecificationExecutor<WalletBtcTransaction> {

}
