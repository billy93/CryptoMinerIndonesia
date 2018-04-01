package com.cryptominer.indonesia.repository;

import com.cryptominer.indonesia.domain.PackageCmi;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PackageCmi entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PackageCmiRepository extends JpaRepository<PackageCmi, Long>, JpaSpecificationExecutor<PackageCmi> {
	List<PackageCmi> findByStartDateGreaterThanAndEndDateLessThan(LocalDate now, LocalDate now2);

	List<PackageCmi> findByStartDateAfter(LocalDate now);

	List<PackageCmi> findByStartDateBeforeAndEndDateAfter(LocalDate now, LocalDate now2);

	@Query(value="SELECT * FROM package_cmi where current_date() between start_date and end_date", nativeQuery=true)
	List<PackageCmi> findAllByCurrentDateBetweenStartAndEndDate();

}
