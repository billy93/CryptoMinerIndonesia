package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the WalletUsdTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletUsdTransactionRepository extends JpaRepository<WalletUsdTransaction, Long>, JpaSpecificationExecutor<WalletUsdTransaction> {

}
