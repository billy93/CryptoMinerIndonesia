package com.cryptominer.indonesia.service;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.domain.*; // for static metamodels
import com.cryptominer.indonesia.repository.PackageCmiRepository;
import com.cryptominer.indonesia.service.dto.PackageCmiCriteria;


/**
 * Service for executing complex queries for PackageCmi entities in the database.
 * The main input is a {@link PackageCmiCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PackageCmi} or a {@link Page} of {@link PackageCmi} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PackageCmiQueryService extends QueryService<PackageCmi> {

    private final Logger log = LoggerFactory.getLogger(PackageCmiQueryService.class);


    private final PackageCmiRepository packageCmiRepository;

    public PackageCmiQueryService(PackageCmiRepository packageCmiRepository) {
        this.packageCmiRepository = packageCmiRepository;
    }

    /**
     * Return a {@link List} of {@link PackageCmi} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PackageCmi> findByCriteria(PackageCmiCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<PackageCmi> specification = createSpecification(criteria);
        return packageCmiRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link PackageCmi} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PackageCmi> findByCriteria(PackageCmiCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<PackageCmi> specification = createSpecification(criteria);
        return packageCmiRepository.findAll(specification, page);
    }

    /**
     * Function to convert PackageCmiCriteria to a {@link Specifications}
     */
    private Specifications<PackageCmi> createSpecification(PackageCmiCriteria criteria) {
        Specifications<PackageCmi> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), PackageCmi_.id));
            }
            if (criteria.getUsername() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUsername(), PackageCmi_.username));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), PackageCmi_.amount));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), PackageCmi_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), PackageCmi_.endDate));
            }
        }
        return specification;
    }

}
