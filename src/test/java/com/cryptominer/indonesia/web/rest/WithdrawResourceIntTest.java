package com.cryptominer.indonesia.web.rest;

import com.cryptominer.indonesia.CryptoMinerIndonesiaApp;

import com.cryptominer.indonesia.domain.Withdraw;
import com.cryptominer.indonesia.repository.WithdrawRepository;
import com.cryptominer.indonesia.web.rest.errors.ExceptionTranslator;

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
import java.util.List;

import static com.cryptominer.indonesia.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WithdrawResource REST controller.
 *
 * @see WithdrawResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoMinerIndonesiaApp.class)
public class WithdrawResourceIntTest {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWithdrawMockMvc;

    private Withdraw withdraw;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WithdrawResource withdrawResource = new WithdrawResource(withdrawRepository);
        this.restWithdrawMockMvc = MockMvcBuilders.standaloneSetup(withdrawResource)
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
    public static Withdraw createEntity(EntityManager em) {
        Withdraw withdraw = new Withdraw()
            .username(DEFAULT_USERNAME)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS);
        return withdraw;
    }

    @Before
    public void initTest() {
        withdraw = createEntity(em);
    }

    @Test
    @Transactional
    public void createWithdraw() throws Exception {
        int databaseSizeBeforeCreate = withdrawRepository.findAll().size();

        // Create the Withdraw
        restWithdrawMockMvc.perform(post("/api/withdraws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(withdraw)))
            .andExpect(status().isCreated());

        // Validate the Withdraw in the database
        List<Withdraw> withdrawList = withdrawRepository.findAll();
        assertThat(withdrawList).hasSize(databaseSizeBeforeCreate + 1);
        Withdraw testWithdraw = withdrawList.get(withdrawList.size() - 1);
        assertThat(testWithdraw.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testWithdraw.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testWithdraw.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createWithdrawWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = withdrawRepository.findAll().size();

        // Create the Withdraw with an existing ID
        withdraw.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWithdrawMockMvc.perform(post("/api/withdraws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(withdraw)))
            .andExpect(status().isBadRequest());

        // Validate the Withdraw in the database
        List<Withdraw> withdrawList = withdrawRepository.findAll();
        assertThat(withdrawList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllWithdraws() throws Exception {
        // Initialize the database
        withdrawRepository.saveAndFlush(withdraw);

        // Get all the withdrawList
        restWithdrawMockMvc.perform(get("/api/withdraws?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(withdraw.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getWithdraw() throws Exception {
        // Initialize the database
        withdrawRepository.saveAndFlush(withdraw);

        // Get the withdraw
        restWithdrawMockMvc.perform(get("/api/withdraws/{id}", withdraw.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(withdraw.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWithdraw() throws Exception {
        // Get the withdraw
        restWithdrawMockMvc.perform(get("/api/withdraws/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWithdraw() throws Exception {
        // Initialize the database
        withdrawRepository.saveAndFlush(withdraw);
        int databaseSizeBeforeUpdate = withdrawRepository.findAll().size();

        // Update the withdraw
        Withdraw updatedWithdraw = withdrawRepository.findOne(withdraw.getId());
        // Disconnect from session so that the updates on updatedWithdraw are not directly saved in db
        em.detach(updatedWithdraw);
        updatedWithdraw
            .username(UPDATED_USERNAME)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);

        restWithdrawMockMvc.perform(put("/api/withdraws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWithdraw)))
            .andExpect(status().isOk());

        // Validate the Withdraw in the database
        List<Withdraw> withdrawList = withdrawRepository.findAll();
        assertThat(withdrawList).hasSize(databaseSizeBeforeUpdate);
        Withdraw testWithdraw = withdrawList.get(withdrawList.size() - 1);
        assertThat(testWithdraw.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testWithdraw.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testWithdraw.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingWithdraw() throws Exception {
        int databaseSizeBeforeUpdate = withdrawRepository.findAll().size();

        // Create the Withdraw

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWithdrawMockMvc.perform(put("/api/withdraws")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(withdraw)))
            .andExpect(status().isCreated());

        // Validate the Withdraw in the database
        List<Withdraw> withdrawList = withdrawRepository.findAll();
        assertThat(withdrawList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWithdraw() throws Exception {
        // Initialize the database
        withdrawRepository.saveAndFlush(withdraw);
        int databaseSizeBeforeDelete = withdrawRepository.findAll().size();

        // Get the withdraw
        restWithdrawMockMvc.perform(delete("/api/withdraws/{id}", withdraw.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Withdraw> withdrawList = withdrawRepository.findAll();
        assertThat(withdrawList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Withdraw.class);
        Withdraw withdraw1 = new Withdraw();
        withdraw1.setId(1L);
        Withdraw withdraw2 = new Withdraw();
        withdraw2.setId(withdraw1.getId());
        assertThat(withdraw1).isEqualTo(withdraw2);
        withdraw2.setId(2L);
        assertThat(withdraw1).isNotEqualTo(withdraw2);
        withdraw1.setId(null);
        assertThat(withdraw1).isNotEqualTo(withdraw2);
    }
}
