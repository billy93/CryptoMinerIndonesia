package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the WalletBtcTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletBtcTransactionRepository extends JpaRepository<WalletBtcTransaction, Long>, JpaSpecificationExecutor<WalletBtcTransaction> {

	@Query(value="SELECT * FROM wallet_btc_transaction where DATE(created_date) = ?1 and package_cmi_id = ?2", nativeQuery=true)
	WalletBtcTransaction findByCreatedDateAndPackageCmi(String date, String cmiId);

}
