package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.Testing;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Testing entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TestingRepository extends JpaRepository<Testing, Long> {

}
