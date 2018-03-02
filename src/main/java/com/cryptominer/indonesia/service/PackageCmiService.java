package com.cryptominer.indonesia.service;

import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.repository.PackageCmiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing PackageCmi.
 */
@Service
@Transactional
public class PackageCmiService {

    private final Logger log = LoggerFactory.getLogger(PackageCmiService.class);

    private final PackageCmiRepository packageCmiRepository;

    public PackageCmiService(PackageCmiRepository packageCmiRepository) {
        this.packageCmiRepository = packageCmiRepository;
    }

    /**
     * Save a packageCmi.
     *
     * @param packageCmi the entity to save
     * @return the persisted entity
     */
    public PackageCmi save(PackageCmi packageCmi) {
        log.debug("Request to save PackageCmi : {}", packageCmi);
        return packageCmiRepository.save(packageCmi);
    }

    /**
     * Get all the packageCmis.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PackageCmi> findAll(Pageable pageable) {
        log.debug("Request to get all PackageCmis");
        return packageCmiRepository.findAll(pageable);
    }

    /**
     * Get one packageCmi by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public PackageCmi findOne(Long id) {
        log.debug("Request to get PackageCmi : {}", id);
        return packageCmiRepository.findOne(id);
    }

    /**
     * Delete the packageCmi by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PackageCmi : {}", id);
        packageCmiRepository.delete(id);
    }
}
