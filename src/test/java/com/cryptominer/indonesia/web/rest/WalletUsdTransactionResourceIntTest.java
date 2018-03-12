package com.cryptominer.indonesia.web.rest;

import com.cryptominer.indonesia.CryptoMinerIndonesiaApp;

import com.cryptominer.indonesia.domain.WalletUsdTransaction;
import com.cryptominer.indonesia.repository.WalletUsdTransactionRepository;
import com.cryptominer.indonesia.service.WalletUsdTransactionService;
import com.cryptominer.indonesia.web.rest.errors.ExceptionTranslator;
import com.cryptominer.indonesia.service.dto.WalletUsdTransactionCriteria;
import com.cryptominer.indonesia.service.WalletUsdTransactionQueryService;

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
 * Test class for the WalletUsdTransactionResource REST controller.
 *
 * @see WalletUsdTransactionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoMinerIndonesiaApp.class)
public class WalletUsdTransactionResourceIntTest {

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

    private static final String DEFAULT_TO_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_TO_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private WalletUsdTransactionRepository walletUsdTransactionRepository;

    @Autowired
    private WalletUsdTransactionService walletUsdTransactionService;

    @Autowired
    private WalletUsdTransactionQueryService walletUsdTransactionQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWalletUsdTransactionMockMvc;

    private WalletUsdTransaction walletUsdTransaction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WalletUsdTransactionResource walletUsdTransactionResource = new WalletUsdTransactionResource(walletUsdTransactionService, walletUsdTransactionQueryService, null);
        this.restWalletUsdTransactionMockMvc = MockMvcBuilders.standaloneSetup(walletUsdTransactionResource)
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
    public static WalletUsdTransaction createEntity(EntityManager em) {
        WalletUsdTransaction walletUsdTransaction = new WalletUsdTransaction()
            .username(DEFAULT_USERNAME)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE)
            .fromUsername(DEFAULT_FROM_USERNAME)
            .txid(DEFAULT_TXID)
            .toUsername(DEFAULT_TO_USERNAME)
            .status(DEFAULT_STATUS);
        return walletUsdTransaction;
    }

    @Before
    public void initTest() {
        walletUsdTransaction = createEntity(em);
    }

    @Test
    @Transactional
    public void createWalletUsdTransaction() throws Exception {
        int databaseSizeBeforeCreate = walletUsdTransactionRepository.findAll().size();

        // Create the WalletUsdTransaction
        restWalletUsdTransactionMockMvc.perform(post("/api/wallet-usd-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletUsdTransaction)))
            .andExpect(status().isCreated());

        // Validate the WalletUsdTransaction in the database
        List<WalletUsdTransaction> walletUsdTransactionList = walletUsdTransactionRepository.findAll();
        assertThat(walletUsdTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        WalletUsdTransaction testWalletUsdTransaction = walletUsdTransactionList.get(walletUsdTransactionList.size() - 1);
        assertThat(testWalletUsdTransaction.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testWalletUsdTransaction.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testWalletUsdTransaction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testWalletUsdTransaction.getFromUsername()).isEqualTo(DEFAULT_FROM_USERNAME);
        assertThat(testWalletUsdTransaction.getTxid()).isEqualTo(DEFAULT_TXID);
        assertThat(testWalletUsdTransaction.getToUsername()).isEqualTo(DEFAULT_TO_USERNAME);
        assertThat(testWalletUsdTransaction.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createWalletUsdTransactionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = walletUsdTransactionRepository.findAll().size();

        // Create the WalletUsdTransaction with an existing ID
        walletUsdTransaction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletUsdTransactionMockMvc.perform(post("/api/wallet-usd-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletUsdTransaction)))
            .andExpect(status().isBadRequest());

        // Validate the WalletUsdTransaction in the database
        List<WalletUsdTransaction> walletUsdTransactionList = walletUsdTransactionRepository.findAll();
        assertThat(walletUsdTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletUsdTransactionRepository.findAll().size();
        // set the field null
        walletUsdTransaction.setUsername(null);

        // Create the WalletUsdTransaction, which fails.

        restWalletUsdTransactionMockMvc.perform(post("/api/wallet-usd-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletUsdTransaction)))
            .andExpect(status().isBadRequest());

        List<WalletUsdTransaction> walletUsdTransactionList = walletUsdTransactionRepository.findAll();
        assertThat(walletUsdTransactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactions() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList
        restWalletUsdTransactionMockMvc.perform(get("/api/wallet-usd-transactions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletUsdTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fromUsername").value(hasItem(DEFAULT_FROM_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].txid").value(hasItem(DEFAULT_TXID.toString())))
            .andExpect(jsonPath("$.[*].toUsername").value(hasItem(DEFAULT_TO_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getWalletUsdTransaction() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get the walletUsdTransaction
        restWalletUsdTransactionMockMvc.perform(get("/api/wallet-usd-transactions/{id}", walletUsdTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(walletUsdTransaction.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.fromUsername").value(DEFAULT_FROM_USERNAME.toString()))
            .andExpect(jsonPath("$.txid").value(DEFAULT_TXID.toString()))
            .andExpect(jsonPath("$.toUsername").value(DEFAULT_TO_USERNAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where username equals to DEFAULT_USERNAME
        defaultWalletUsdTransactionShouldBeFound("username.equals=" + DEFAULT_USERNAME);

        // Get all the walletUsdTransactionList where username equals to UPDATED_USERNAME
        defaultWalletUsdTransactionShouldNotBeFound("username.equals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where username in DEFAULT_USERNAME or UPDATED_USERNAME
        defaultWalletUsdTransactionShouldBeFound("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME);

        // Get all the walletUsdTransactionList where username equals to UPDATED_USERNAME
        defaultWalletUsdTransactionShouldNotBeFound("username.in=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where username is not null
        defaultWalletUsdTransactionShouldBeFound("username.specified=true");

        // Get all the walletUsdTransactionList where username is null
        defaultWalletUsdTransactionShouldNotBeFound("username.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where amount equals to DEFAULT_AMOUNT
        defaultWalletUsdTransactionShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the walletUsdTransactionList where amount equals to UPDATED_AMOUNT
        defaultWalletUsdTransactionShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultWalletUsdTransactionShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the walletUsdTransactionList where amount equals to UPDATED_AMOUNT
        defaultWalletUsdTransactionShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where amount is not null
        defaultWalletUsdTransactionShouldBeFound("amount.specified=true");

        // Get all the walletUsdTransactionList where amount is null
        defaultWalletUsdTransactionShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where type equals to DEFAULT_TYPE
        defaultWalletUsdTransactionShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the walletUsdTransactionList where type equals to UPDATED_TYPE
        defaultWalletUsdTransactionShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultWalletUsdTransactionShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the walletUsdTransactionList where type equals to UPDATED_TYPE
        defaultWalletUsdTransactionShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where type is not null
        defaultWalletUsdTransactionShouldBeFound("type.specified=true");

        // Get all the walletUsdTransactionList where type is null
        defaultWalletUsdTransactionShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByFromUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where fromUsername equals to DEFAULT_FROM_USERNAME
        defaultWalletUsdTransactionShouldBeFound("fromUsername.equals=" + DEFAULT_FROM_USERNAME);

        // Get all the walletUsdTransactionList where fromUsername equals to UPDATED_FROM_USERNAME
        defaultWalletUsdTransactionShouldNotBeFound("fromUsername.equals=" + UPDATED_FROM_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByFromUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where fromUsername in DEFAULT_FROM_USERNAME or UPDATED_FROM_USERNAME
        defaultWalletUsdTransactionShouldBeFound("fromUsername.in=" + DEFAULT_FROM_USERNAME + "," + UPDATED_FROM_USERNAME);

        // Get all the walletUsdTransactionList where fromUsername equals to UPDATED_FROM_USERNAME
        defaultWalletUsdTransactionShouldNotBeFound("fromUsername.in=" + UPDATED_FROM_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByFromUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where fromUsername is not null
        defaultWalletUsdTransactionShouldBeFound("fromUsername.specified=true");

        // Get all the walletUsdTransactionList where fromUsername is null
        defaultWalletUsdTransactionShouldNotBeFound("fromUsername.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByTxidIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where txid equals to DEFAULT_TXID
        defaultWalletUsdTransactionShouldBeFound("txid.equals=" + DEFAULT_TXID);

        // Get all the walletUsdTransactionList where txid equals to UPDATED_TXID
        defaultWalletUsdTransactionShouldNotBeFound("txid.equals=" + UPDATED_TXID);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByTxidIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where txid in DEFAULT_TXID or UPDATED_TXID
        defaultWalletUsdTransactionShouldBeFound("txid.in=" + DEFAULT_TXID + "," + UPDATED_TXID);

        // Get all the walletUsdTransactionList where txid equals to UPDATED_TXID
        defaultWalletUsdTransactionShouldNotBeFound("txid.in=" + UPDATED_TXID);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByTxidIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where txid is not null
        defaultWalletUsdTransactionShouldBeFound("txid.specified=true");

        // Get all the walletUsdTransactionList where txid is null
        defaultWalletUsdTransactionShouldNotBeFound("txid.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByToUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where toUsername equals to DEFAULT_TO_USERNAME
        defaultWalletUsdTransactionShouldBeFound("toUsername.equals=" + DEFAULT_TO_USERNAME);

        // Get all the walletUsdTransactionList where toUsername equals to UPDATED_TO_USERNAME
        defaultWalletUsdTransactionShouldNotBeFound("toUsername.equals=" + UPDATED_TO_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByToUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where toUsername in DEFAULT_TO_USERNAME or UPDATED_TO_USERNAME
        defaultWalletUsdTransactionShouldBeFound("toUsername.in=" + DEFAULT_TO_USERNAME + "," + UPDATED_TO_USERNAME);

        // Get all the walletUsdTransactionList where toUsername equals to UPDATED_TO_USERNAME
        defaultWalletUsdTransactionShouldNotBeFound("toUsername.in=" + UPDATED_TO_USERNAME);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByToUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where toUsername is not null
        defaultWalletUsdTransactionShouldBeFound("toUsername.specified=true");

        // Get all the walletUsdTransactionList where toUsername is null
        defaultWalletUsdTransactionShouldNotBeFound("toUsername.specified=false");
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where status equals to DEFAULT_STATUS
        defaultWalletUsdTransactionShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the walletUsdTransactionList where status equals to UPDATED_STATUS
        defaultWalletUsdTransactionShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultWalletUsdTransactionShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the walletUsdTransactionList where status equals to UPDATED_STATUS
        defaultWalletUsdTransactionShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllWalletUsdTransactionsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletUsdTransactionRepository.saveAndFlush(walletUsdTransaction);

        // Get all the walletUsdTransactionList where status is not null
        defaultWalletUsdTransactionShouldBeFound("status.specified=true");

        // Get all the walletUsdTransactionList where status is null
        defaultWalletUsdTransactionShouldNotBeFound("status.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultWalletUsdTransactionShouldBeFound(String filter) throws Exception {
        restWalletUsdTransactionMockMvc.perform(get("/api/wallet-usd-transactions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletUsdTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fromUsername").value(hasItem(DEFAULT_FROM_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].txid").value(hasItem(DEFAULT_TXID.toString())))
            .andExpect(jsonPath("$.[*].toUsername").value(hasItem(DEFAULT_TO_USERNAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultWalletUsdTransactionShouldNotBeFound(String filter) throws Exception {
        restWalletUsdTransactionMockMvc.perform(get("/api/wallet-usd-transactions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingWalletUsdTransaction() throws Exception {
        // Get the walletUsdTransaction
        restWalletUsdTransactionMockMvc.perform(get("/api/wallet-usd-transactions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWalletUsdTransaction() throws Exception {
        // Initialize the database
        walletUsdTransactionService.save(walletUsdTransaction);

        int databaseSizeBeforeUpdate = walletUsdTransactionRepository.findAll().size();

        // Update the walletUsdTransaction
        WalletUsdTransaction updatedWalletUsdTransaction = walletUsdTransactionRepository.findOne(walletUsdTransaction.getId());
        // Disconnect from session so that the updates on updatedWalletUsdTransaction are not directly saved in db
        em.detach(updatedWalletUsdTransaction);
        updatedWalletUsdTransaction
            .username(UPDATED_USERNAME)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE)
            .fromUsername(UPDATED_FROM_USERNAME)
            .txid(UPDATED_TXID)
            .toUsername(UPDATED_TO_USERNAME)
            .status(UPDATED_STATUS);

        restWalletUsdTransactionMockMvc.perform(put("/api/wallet-usd-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWalletUsdTransaction)))
            .andExpect(status().isOk());

        // Validate the WalletUsdTransaction in the database
        List<WalletUsdTransaction> walletUsdTransactionList = walletUsdTransactionRepository.findAll();
        assertThat(walletUsdTransactionList).hasSize(databaseSizeBeforeUpdate);
        WalletUsdTransaction testWalletUsdTransaction = walletUsdTransactionList.get(walletUsdTransactionList.size() - 1);
        assertThat(testWalletUsdTransaction.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testWalletUsdTransaction.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testWalletUsdTransaction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testWalletUsdTransaction.getFromUsername()).isEqualTo(UPDATED_FROM_USERNAME);
        assertThat(testWalletUsdTransaction.getTxid()).isEqualTo(UPDATED_TXID);
        assertThat(testWalletUsdTransaction.getToUsername()).isEqualTo(UPDATED_TO_USERNAME);
        assertThat(testWalletUsdTransaction.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingWalletUsdTransaction() throws Exception {
        int databaseSizeBeforeUpdate = walletUsdTransactionRepository.findAll().size();

        // Create the WalletUsdTransaction

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWalletUsdTransactionMockMvc.perform(put("/api/wallet-usd-transactions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(walletUsdTransaction)))
            .andExpect(status().isCreated());

        // Validate the WalletUsdTransaction in the database
        List<WalletUsdTransaction> walletUsdTransactionList = walletUsdTransactionRepository.findAll();
        assertThat(walletUsdTransactionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWalletUsdTransaction() throws Exception {
        // Initialize the database
        walletUsdTransactionService.save(walletUsdTransaction);

        int databaseSizeBeforeDelete = walletUsdTransactionRepository.findAll().size();

        // Get the walletUsdTransaction
        restWalletUsdTransactionMockMvc.perform(delete("/api/wallet-usd-transactions/{id}", walletUsdTransaction.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<WalletUsdTransaction> walletUsdTransactionList = walletUsdTransactionRepository.findAll();
        assertThat(walletUsdTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WalletUsdTransaction.class);
        WalletUsdTransaction walletUsdTransaction1 = new WalletUsdTransaction();
        walletUsdTransaction1.setId(1L);
        WalletUsdTransaction walletUsdTransaction2 = new WalletUsdTransaction();
        walletUsdTransaction2.setId(walletUsdTransaction1.getId());
        assertThat(walletUsdTransaction1).isEqualTo(walletUsdTransaction2);
        walletUsdTransaction2.setId(2L);
        assertThat(walletUsdTransaction1).isNotEqualTo(walletUsdTransaction2);
        walletUsdTransaction1.setId(null);
        assertThat(walletUsdTransaction1).isNotEqualTo(walletUsdTransaction2);
    }
}
