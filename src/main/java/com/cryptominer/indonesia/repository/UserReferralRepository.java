package com.cryptominer.indonesia.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptominer.indonesia.domain.UserReferral;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserReferralRepository extends JpaRepository<UserReferral, Long> {
    List<UserReferral> findAllByUsername(String username);

	UserReferral findByReferral(String referral);
}
