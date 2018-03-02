package com.cryptominer.indonesia.web.rest;

import com.cryptominer.indonesia.CryptoMinerIndonesiaApp;

import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.repository.PackageCmiRepository;
import com.cryptominer.indonesia.service.PackageCmiService;
import com.cryptominer.indonesia.web.rest.errors.ExceptionTranslator;
import com.cryptominer.indonesia.service.dto.PackageCmiCriteria;
import com.cryptominer.indonesia.service.PackageCmiQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.cryptominer.indonesia.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PackageCmiResource REST controller.
 *
 * @see PackageCmiResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoMinerIndonesiaApp.class)
public class PackageCmiResourceIntTest {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PackageCmiRepository packageCmiRepository;

    @Autowired
    private PackageCmiService packageCmiService;

    @Autowired
    private PackageCmiQueryService packageCmiQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPackageCmiMockMvc;

    private PackageCmi packageCmi;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PackageCmiResource packageCmiResource = new PackageCmiResource(packageCmiService, packageCmiQueryService);
        this.restPackageCmiMockMvc = MockMvcBuilders.standaloneSetup(packageCmiResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PackageCmi createEntity(EntityManager em) {
        PackageCmi packageCmi = new PackageCmi()
            .username(DEFAULT_USERNAME)
            .amount(DEFAULT_AMOUNT)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return packageCmi;
    }

    @Before
    public void initTest() {
        packageCmi = createEntity(em);
    }

    @Test
    @Transactional
    public void createPackageCmi() throws Exception {
        int databaseSizeBeforeCreate = packageCmiRepository.findAll().size();

        // Create the PackageCmi
        restPackageCmiMockMvc.perform(post("/api/package-cmis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packageCmi)))
            .andExpect(status().isCreated());

        // Validate the PackageCmi in the database
        List<PackageCmi> packageCmiList = packageCmiRepository.findAll();
        assertThat(packageCmiList).hasSize(databaseSizeBeforeCreate + 1);
        PackageCmi testPackageCmi = packageCmiList.get(packageCmiList.size() - 1);
        assertThat(testPackageCmi.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testPackageCmi.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPackageCmi.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testPackageCmi.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    public void createPackageCmiWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = packageCmiRepository.findAll().size();

        // Create the PackageCmi with an existing ID
        packageCmi.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPackageCmiMockMvc.perform(post("/api/package-cmis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packageCmi)))
            .andExpect(status().isBadRequest());

        // Validate the PackageCmi in the database
        List<PackageCmi> packageCmiList = packageCmiRepository.findAll();
        assertThat(packageCmiList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPackageCmis() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList
        restPackageCmiMockMvc.perform(get("/api/package-cmis?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packageCmi.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    public void getPackageCmi() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get the packageCmi
        restPackageCmiMockMvc.perform(get("/api/package-cmis/{id}", packageCmi.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(packageCmi.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllPackageCmisByUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where username equals to DEFAULT_USERNAME
        defaultPackageCmiShouldBeFound("username.equals=" + DEFAULT_USERNAME);

        // Get all the packageCmiList where username equals to UPDATED_USERNAME
        defaultPackageCmiShouldNotBeFound("username.equals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where username in DEFAULT_USERNAME or UPDATED_USERNAME
        defaultPackageCmiShouldBeFound("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME);

        // Get all the packageCmiList where username equals to UPDATED_USERNAME
        defaultPackageCmiShouldNotBeFound("username.in=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where username is not null
        defaultPackageCmiShouldBeFound("username.specified=true");

        // Get all the packageCmiList where username is null
        defaultPackageCmiShouldNotBeFound("username.specified=false");
    }

    @Test
    @Transactional
    public void getAllPackageCmisByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where amount equals to DEFAULT_AMOUNT
        defaultPackageCmiShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the packageCmiList where amount equals to UPDATED_AMOUNT
        defaultPackageCmiShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultPackageCmiShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the packageCmiList where amount equals to UPDATED_AMOUNT
        defaultPackageCmiShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where amount is not null
        defaultPackageCmiShouldBeFound("amount.specified=true");

        // Get all the packageCmiList where amount is null
        defaultPackageCmiShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllPackageCmisByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where startDate equals to DEFAULT_START_DATE
        defaultPackageCmiShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the packageCmiList where startDate equals to UPDATED_START_DATE
        defaultPackageCmiShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultPackageCmiShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the packageCmiList where startDate equals to UPDATED_START_DATE
        defaultPackageCmiShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where startDate is not null
        defaultPackageCmiShouldBeFound("startDate.specified=true");

        // Get all the packageCmiList where startDate is null
        defaultPackageCmiShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPackageCmisByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where startDate greater than or equals to DEFAULT_START_DATE
        defaultPackageCmiShouldBeFound("startDate.greaterOrEqualThan=" + DEFAULT_START_DATE);

        // Get all the packageCmiList where startDate greater than or equals to UPDATED_START_DATE
        defaultPackageCmiShouldNotBeFound("startDate.greaterOrEqualThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where startDate less than or equals to DEFAULT_START_DATE
        defaultPackageCmiShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the packageCmiList where startDate less than or equals to UPDATED_START_DATE
        defaultPackageCmiShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }


    @Test
    @Transactional
    public void getAllPackageCmisByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where endDate equals to DEFAULT_END_DATE
        defaultPackageCmiShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the packageCmiList where endDate equals to UPDATED_END_DATE
        defaultPackageCmiShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultPackageCmiShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the packageCmiList where endDate equals to UPDATED_END_DATE
        defaultPackageCmiShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where endDate is not null
        defaultPackageCmiShouldBeFound("endDate.specified=true");

        // Get all the packageCmiList where endDate is null
        defaultPackageCmiShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPackageCmisByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where endDate greater than or equals to DEFAULT_END_DATE
        defaultPackageCmiShouldBeFound("endDate.greaterOrEqualThan=" + DEFAULT_END_DATE);

        // Get all the packageCmiList where endDate greater than or equals to UPDATED_END_DATE
        defaultPackageCmiShouldNotBeFound("endDate.greaterOrEqualThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllPackageCmisByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        packageCmiRepository.saveAndFlush(packageCmi);

        // Get all the packageCmiList where endDate less than or equals to DEFAULT_END_DATE
        defaultPackageCmiShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the packageCmiList where endDate less than or equals to UPDATED_END_DATE
        defaultPackageCmiShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPackageCmiShouldBeFound(String filter) throws Exception {
        restPackageCmiMockMvc.perform(get("/api/package-cmis?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(packageCmi.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPackageCmiShouldNotBeFound(String filter) throws Exception {
        restPackageCmiMockMvc.perform(get("/api/package-cmis?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingPackageCmi() throws Exception {
        // Get the packageCmi
        restPackageCmiMockMvc.perform(get("/api/package-cmis/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePackageCmi() throws Exception {
        // Initialize the database
        packageCmiService.save(packageCmi);

        int databaseSizeBeforeUpdate = packageCmiRepository.findAll().size();

        // Update the packageCmi
        PackageCmi updatedPackageCmi = packageCmiRepository.findOne(packageCmi.getId());
        // Disconnect from session so that the updates on updatedPackageCmi are not directly saved in db
        em.detach(updatedPackageCmi);
        updatedPackageCmi
            .username(UPDATED_USERNAME)
            .amount(UPDATED_AMOUNT)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restPackageCmiMockMvc.perform(put("/api/package-cmis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPackageCmi)))
            .andExpect(status().isOk());

        // Validate the PackageCmi in the database
        List<PackageCmi> packageCmiList = packageCmiRepository.findAll();
        assertThat(packageCmiList).hasSize(databaseSizeBeforeUpdate);
        PackageCmi testPackageCmi = packageCmiList.get(packageCmiList.size() - 1);
        assertThat(testPackageCmi.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testPackageCmi.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPackageCmi.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testPackageCmi.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingPackageCmi() throws Exception {
        int databaseSizeBeforeUpdate = packageCmiRepository.findAll().size();

        // Create the PackageCmi

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPackageCmiMockMvc.perform(put("/api/package-cmis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(packageCmi)))
            .andExpect(status().isCreated());

        // Validate the PackageCmi in the database
        List<PackageCmi> packageCmiList = packageCmiRepository.findAll();
        assertThat(packageCmiList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePackageCmi() throws Exception {
        // Initialize the database
        packageCmiService.save(packageCmi);

        int databaseSizeBeforeDelete = packageCmiRepository.findAll().size();

        // Get the packageCmi
        restPackageCmiMockMvc.perform(delete("/api/package-cmis/{id}", packageCmi.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PackageCmi> packageCmiList = packageCmiRepository.findAll();
        assertThat(packageCmiList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PackageCmi.class);
        PackageCmi packageCmi1 = new PackageCmi();
        packageCmi1.setId(1L);
        PackageCmi packageCmi2 = new PackageCmi();
        packageCmi2.setId(packageCmi1.getId());
        assertThat(packageCmi1).isEqualTo(packageCmi2);
        packageCmi2.setId(2L);
        assertThat(packageCmi1).isNotEqualTo(packageCmi2);
        packageCmi1.setId(null);
        assertThat(packageCmi1).isNotEqualTo(packageCmi2);
    }
}
