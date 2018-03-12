package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.Withdraw;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Withdraw entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {

}
