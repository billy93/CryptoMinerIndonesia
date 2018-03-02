package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.PackageCmi;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PackageCmi entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PackageCmiRepository extends JpaRepository<PackageCmi, Long>, JpaSpecificationExecutor<PackageCmi> {

}
