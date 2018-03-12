package com.cryptominer.indonesia.web.rest;

import com.cryptominer.indonesia.CryptoMinerIndonesiaApp;

import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.repository.WalletBtcTransactionRepository;
import com.cryptominer.indonesia.service.WalletBtcTransactionService;
import com.cryptominer.indonesia.web.rest.errors.ExceptionTranslator;
import com.cryptominer.indonesia.service.dto.WalletBtcTransactionCriteria;
import com.cryptominer.indonesia.service.WalletBtcTransactionQueryService;

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

import com.cryptominer.indonesia.domain.enumeration.TransactionType;
/**
 * Test class for the WalletBtcTransactionResource REST controller.
 *
 * @see WalletBtcTransactionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoMinerIndonesiaApp.class)
public class WalletBtcTransactionResourceIntTest {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final TransactionType DEFAULT_TYPE = TransactionType.DEPOSIT;
    private static final TransactionType UPDATED_TYPE = TransactionType.WITHDRAW;

    private static final String DEFAULT_FROM_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_FROM_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_TXID = "AAAAAAAAAA";
    private static final String UPDATED_TXID = "BBBBBBBBBB";

    @Autowired
    private WalletBtcTransactionRepository walletBtcTransactionRepository;

    @Autowired
    private WalletBtcTransactionService walletBtcTransactionService;

    @Autowired
    private WalletBtcTransactionQueryService walletBtcTransactionQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWalletBtcTransactionMockMvc;

    private WalletBtcTransaction walletBtcTransaction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WalletBtcTransactionResource walletBtcTransactionResource = new WalletBtcTransactionResource(walletBtcTransactionService, walletBtcTransactionQueryService, null);
        this.restWalletBtcTransactionMockMvc = MockMvcBuilders.standaloneSetup(walletBtcTransactionResource)
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
    public static WalletBtcTransaction createEntity(EntityManager em) {
        WalletBtcTransaction walletBtcTransaction = new WalletBtcTransaction()
            .username(DEFAULT_USERNAME)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE)
            .fromUsername(DEFAULT_FROM_USERNAME)
            .txid(DEFAULT_TXID);
        return walletBtcTransaction;
    }

    @Before
    public void initTest() {
        walletBtcTransaction = createEntity(em);
    }

    @Test
    @Transactional
    public void createWalletBtcTransaction() throws Exception {
        int databaseSizeBeforeCreate = walletBtcTransactionRepository.findAll().size();

        // Create the WalletBtcTransaction
        restWalletBtcTransactionMockMvc.perform(post("/api/wallet-btc-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletBtcTransaction)))
            .andExpect(status().isCreated());

        // Validate the WalletBtcTransaction in the database
        List<WalletBtcTransaction> walletBtcTransactionList = walletBtcTransactionRepository.findAll();
        assertThat(walletBtcTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        WalletBtcTransaction testWalletBtcTransaction = walletBtcTransactionList.get(walletBtcTransactionList.size() - 1);
        assertThat(testWalletBtcTransaction.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testWalletBtcTransaction.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testWalletBtcTransaction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testWalletBtcTransaction.getFromUsername()).isEqualTo(DEFAULT_FROM_USERNAME);
        assertThat(testWalletBtcTransaction.getTxid()).isEqualTo(DEFAULT_TXID);
    }

    @Test
    @Transactional
    public void createWalletBtcTransactionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = walletBtcTransactionRepository.findAll().size();

        // Create the WalletBtcTransaction with an existing ID
        walletBtcTransaction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletBtcTransactionMockMvc.perform(post("/api/wallet-btc-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletBtcTransaction)))
            .andExpect(status().isBadRequest());

        // Validate the WalletBtcTransaction in the database
        List<WalletBtcTransaction> walletBtcTransactionList = walletBtcTransactionRepository.findAll();
        assertThat(walletBtcTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletBtcTransactionRepository.findAll().size();
        // set the field null
        walletBtcTransaction.setUsername(null);

        // Create the WalletBtcTransaction, which fails.

        restWalletBtcTransactionMockMvc.perform(post("/api/wallet-btc-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletBtcTransaction)))
            .andExpect(status().isBadRequest());

        List<WalletBtcTransaction> walletBtcTransactionList = walletBtcTransactionRepository.findAll();
        assertThat(walletBtcTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactions() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList
        restWalletBtcTransactionMockMvc.perform(get("/api/wallet-btc-transactions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletBtcTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fromUsername").value(hasItem(DEFAULT_FROM_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].txid").value(hasItem(DEFAULT_TXID.toString())));
    }

    @Test
    @Transactional
    public void getWalletBtcTransaction() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get the walletBtcTransaction
        restWalletBtcTransactionMockMvc.perform(get("/api/wallet-btc-transactions/{id}", walletBtcTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(walletBtcTransaction.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.fromUsername").value(DEFAULT_FROM_USERNAME.toString()))
            .andExpect(jsonPath("$.txid").value(DEFAULT_TXID.toString()));
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where username equals to DEFAULT_USERNAME
        defaultWalletBtcTransactionShouldBeFound("username.equals=" + DEFAULT_USERNAME);

        // Get all the walletBtcTransactionList where username equals to UPDATED_USERNAME
        defaultWalletBtcTransactionShouldNotBeFound("username.equals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where username in DEFAULT_USERNAME or UPDATED_USERNAME
        defaultWalletBtcTransactionShouldBeFound("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME);

        // Get all the walletBtcTransactionList where username equals to UPDATED_USERNAME
        defaultWalletBtcTransactionShouldNotBeFound("username.in=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where username is not null
        defaultWalletBtcTransactionShouldBeFound("username.specified=true");

        // Get all the walletBtcTransactionList where username is null
        defaultWalletBtcTransactionShouldNotBeFound("username.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where amount equals to DEFAULT_AMOUNT
        defaultWalletBtcTransactionShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the walletBtcTransactionList where amount equals to UPDATED_AMOUNT
        defaultWalletBtcTransactionShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultWalletBtcTransactionShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the walletBtcTransactionList where amount equals to UPDATED_AMOUNT
        defaultWalletBtcTransactionShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where amount is not null
        defaultWalletBtcTransactionShouldBeFound("amount.specified=true");

        // Get all the walletBtcTransactionList where amount is null
        defaultWalletBtcTransactionShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where type equals to DEFAULT_TYPE
        defaultWalletBtcTransactionShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the walletBtcTransactionList where type equals to UPDATED_TYPE
        defaultWalletBtcTransactionShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultWalletBtcTransactionShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the walletBtcTransactionList where type equals to UPDATED_TYPE
        defaultWalletBtcTransactionShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where type is not null
        defaultWalletBtcTransactionShouldBeFound("type.specified=true");

        // Get all the walletBtcTransactionList where type is null
        defaultWalletBtcTransactionShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByFromUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where fromUsername equals to DEFAULT_FROM_USERNAME
        defaultWalletBtcTransactionShouldBeFound("fromUsername.equals=" + DEFAULT_FROM_USERNAME);

        // Get all the walletBtcTransactionList where fromUsername equals to UPDATED_FROM_USERNAME
        defaultWalletBtcTransactionShouldNotBeFound("fromUsername.equals=" + UPDATED_FROM_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByFromUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where fromUsername in DEFAULT_FROM_USERNAME or UPDATED_FROM_USERNAME
        defaultWalletBtcTransactionShouldBeFound("fromUsername.in=" + DEFAULT_FROM_USERNAME + "," + UPDATED_FROM_USERNAME);

        // Get all the walletBtcTransactionList where fromUsername equals to UPDATED_FROM_USERNAME
        defaultWalletBtcTransactionShouldNotBeFound("fromUsername.in=" + UPDATED_FROM_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByFromUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where fromUsername is not null
        defaultWalletBtcTransactionShouldBeFound("fromUsername.specified=true");

        // Get all the walletBtcTransactionList where fromUsername is null
        defaultWalletBtcTransactionShouldNotBeFound("fromUsername.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByTxidIsEqualToSomething() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where txid equals to DEFAULT_TXID
        defaultWalletBtcTransactionShouldBeFound("txid.equals=" + DEFAULT_TXID);

        // Get all the walletBtcTransactionList where txid equals to UPDATED_TXID
        defaultWalletBtcTransactionShouldNotBeFound("txid.equals=" + UPDATED_TXID);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByTxidIsInShouldWork() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where txid in DEFAULT_TXID or UPDATED_TXID
        defaultWalletBtcTransactionShouldBeFound("txid.in=" + DEFAULT_TXID + "," + UPDATED_TXID);

        // Get all the walletBtcTransactionList where txid equals to UPDATED_TXID
        defaultWalletBtcTransactionShouldNotBeFound("txid.in=" + UPDATED_TXID);
    }

    @Test
    @Transactional
    public void getAllWalletBtcTransactionsByTxidIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletBtcTransactionRepository.saveAndFlush(walletBtcTransaction);

        // Get all the walletBtcTransactionList where txid is not null
        defaultWalletBtcTransactionShouldBeFound("txid.specified=true");

        // Get all the walletBtcTransactionList where txid is null
        defaultWalletBtcTransactionShouldNotBeFound("txid.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultWalletBtcTransactionShouldBeFound(String filter) throws Exception {
        restWalletBtcTransactionMockMvc.perform(get("/api/wallet-btc-transactions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletBtcTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fromUsername").value(hasItem(DEFAULT_FROM_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].txid").value(hasItem(DEFAULT_TXID.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultWalletBtcTransactionShouldNotBeFound(String filter) throws Exception {
        restWalletBtcTransactionMockMvc.perform(get("/api/wallet-btc-transactions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingWalletBtcTransaction() throws Exception {
        // Get the walletBtcTransaction
        restWalletBtcTransactionMockMvc.perform(get("/api/wallet-btc-transactions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWalletBtcTransaction() throws Exception {
        // Initialize the database
        walletBtcTransactionService.save(walletBtcTransaction);

        int databaseSizeBeforeUpdate = walletBtcTransactionRepository.findAll().size();

        // Update the walletBtcTransaction
        WalletBtcTransaction updatedWalletBtcTransaction = walletBtcTransactionRepository.findOne(walletBtcTransaction.getId());
        // Disconnect from session so that the updates on updatedWalletBtcTransaction are not directly saved in db
        em.detach(updatedWalletBtcTransaction);
        updatedWalletBtcTransaction
            .username(UPDATED_USERNAME)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .fromUsername(UPDATED_FROM_USERNAME)
            .txid(UPDATED_TXID);

        restWalletBtcTransactionMockMvc.perform(put("/api/wallet-btc-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWalletBtcTransaction)))
            .andExpect(status().isOk());

        // Validate the WalletBtcTransaction in the database
        List<WalletBtcTransaction> walletBtcTransactionList = walletBtcTransactionRepository.findAll();
        assertThat(walletBtcTransactionList).hasSize(databaseSizeBeforeUpdate);
        WalletBtcTransaction testWalletBtcTransaction = walletBtcTransactionList.get(walletBtcTransactionList.size() - 1);
        assertThat(testWalletBtcTransaction.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testWalletBtcTransaction.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testWalletBtcTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testWalletBtcTransaction.getFromUsername()).isEqualTo(UPDATED_FROM_USERNAME);
        assertThat(testWalletBtcTransaction.getTxid()).isEqualTo(UPDATED_TXID);
    }

    @Test
    @Transactional
    public void updateNonExistingWalletBtcTransaction() throws Exception {
        int databaseSizeBeforeUpdate = walletBtcTransactionRepository.findAll().size();

        // Create the WalletBtcTransaction

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWalletBtcTransactionMockMvc.perform(put("/api/wallet-btc-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletBtcTransaction)))
            .andExpect(status().isCreated());

        // Validate the WalletBtcTransaction in the database
        List<WalletBtcTransaction> walletBtcTransactionList = walletBtcTransactionRepository.findAll();
        assertThat(walletBtcTransactionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWalletBtcTransaction() throws Exception {
        // Initialize the database
        walletBtcTransactionService.save(walletBtcTransaction);

        int databaseSizeBeforeDelete = walletBtcTransactionRepository.findAll().size();

        // Get the walletBtcTransaction
        restWalletBtcTransactionMockMvc.perform(delete("/api/wallet-btc-transactions/{id}", walletBtcTransaction.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<WalletBtcTransaction> walletBtcTransactionList = walletBtcTransactionRepository.findAll();
        assertThat(walletBtcTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WalletBtcTransaction.class);
        WalletBtcTransaction walletBtcTransaction1 = new WalletBtcTransaction();
        walletBtcTransaction1.setId(1L);
        WalletBtcTransaction walletBtcTransaction2 = new WalletBtcTransaction();
        walletBtcTransaction2.setId(walletBtcTransaction1.getId());
        assertThat(walletBtcTransaction1).isEqualTo(walletBtcTransaction2);
        walletBtcTransaction2.setId(2L);
        assertThat(walletBtcTransaction1).isNotEqualTo(walletBtcTransaction2);
        walletBtcTransaction1.setId(null);
        assertThat(walletBtcTransaction1).isNotEqualTo(walletBtcTransaction2);
    }
}
