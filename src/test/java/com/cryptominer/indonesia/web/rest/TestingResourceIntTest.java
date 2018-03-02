package com.cryptominer.indonesia.web.rest;

import com.cryptominer.indonesia.CryptoMinerIndonesiaApp;

import com.cryptominer.indonesia.domain.Testing;
import com.cryptominer.indonesia.repository.TestingRepository;
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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.cryptominer.indonesia.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TestingResource REST controller.
 *
 * @see TestingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CryptoMinerIndonesiaApp.class)
public class TestingResourceIntTest {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(10000, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(5000000, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_AGREEMENT = false;
    private static final Boolean UPDATED_AGREEMENT = true;

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    private TestingRepository testingRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTestingMockMvc;

    private Testing testing;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TestingResource testingResource = new TestingResource(testingRepository);
        this.restTestingMockMvc = MockMvcBuilders.standaloneSetup(testingResource)
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
    public static Testing createEntity(EntityManager em) {
        Testing testing = new Testing()
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .agreement(DEFAULT_AGREEMENT)
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE);
        return testing;
    }

    @Before
    public void initTest() {
        testing = createEntity(em);
    }

    @Test
    @Transactional
    public void createTesting() throws Exception {
        int databaseSizeBeforeCreate = testingRepository.findAll().size();

        // Create the Testing
        restTestingMockMvc.perform(post("/api/testings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testing)))
            .andExpect(status().isCreated());

        // Validate the Testing in the database
        List<Testing> testingList = testingRepository.findAll();
        assertThat(testingList).hasSize(databaseSizeBeforeCreate + 1);
        Testing testTesting = testingList.get(testingList.size() - 1);
        assertThat(testTesting.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testTesting.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testTesting.isAgreement()).isEqualTo(DEFAULT_AGREEMENT);
        assertThat(testTesting.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testTesting.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createTestingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = testingRepository.findAll().size();

        // Create the Testing with an existing ID
        testing.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestingMockMvc.perform(post("/api/testings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testing)))
            .andExpect(status().isBadRequest());

        // Validate the Testing in the database
        List<Testing> testingList = testingRepository.findAll();
        assertThat(testingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkImageIsRequired() throws Exception {
        int databaseSizeBeforeTest = testingRepository.findAll().size();
        // set the field null
        testing.setImage(null);

        // Create the Testing, which fails.

        restTestingMockMvc.perform(post("/api/testings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testing)))
            .andExpect(status().isBadRequest());

        List<Testing> testingList = testingRepository.findAll();
        assertThat(testingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTestings() throws Exception {
        // Initialize the database
        testingRepository.saveAndFlush(testing);

        // Get all the testingList
        restTestingMockMvc.perform(get("/api/testings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testing.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].agreement").value(hasItem(DEFAULT_AGREEMENT.booleanValue())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    public void getTesting() throws Exception {
        // Initialize the database
        testingRepository.saveAndFlush(testing);

        // Get the testing
        restTestingMockMvc.perform(get("/api/testings/{id}", testing.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(testing.getId().intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.agreement").value(DEFAULT_AGREEMENT.booleanValue()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingTesting() throws Exception {
        // Get the testing
        restTestingMockMvc.perform(get("/api/testings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTesting() throws Exception {
        // Initialize the database
        testingRepository.saveAndFlush(testing);
        int databaseSizeBeforeUpdate = testingRepository.findAll().size();

        // Update the testing
        Testing updatedTesting = testingRepository.findOne(testing.getId());
        // Disconnect from session so that the updates on updatedTesting are not directly saved in db
        em.detach(updatedTesting);
        updatedTesting
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .agreement(UPDATED_AGREEMENT)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restTestingMockMvc.perform(put("/api/testings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTesting)))
            .andExpect(status().isOk());

        // Validate the Testing in the database
        List<Testing> testingList = testingRepository.findAll();
        assertThat(testingList).hasSize(databaseSizeBeforeUpdate);
        Testing testTesting = testingList.get(testingList.size() - 1);
        assertThat(testTesting.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testTesting.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testTesting.isAgreement()).isEqualTo(UPDATED_AGREEMENT);
        assertThat(testTesting.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testTesting.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingTesting() throws Exception {
        int databaseSizeBeforeUpdate = testingRepository.findAll().size();

        // Create the Testing

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTestingMockMvc.perform(put("/api/testings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testing)))
            .andExpect(status().isCreated());

        // Validate the Testing in the database
        List<Testing> testingList = testingRepository.findAll();
        assertThat(testingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTesting() throws Exception {
        // Initialize the database
        testingRepository.saveAndFlush(testing);
        int databaseSizeBeforeDelete = testingRepository.findAll().size();

        // Get the testing
        restTestingMockMvc.perform(delete("/api/testings/{id}", testing.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Testing> testingList = testingRepository.findAll();
        assertThat(testingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Testing.class);
        Testing testing1 = new Testing();
        testing1.setId(1L);
        Testing testing2 = new Testing();
        testing2.setId(testing1.getId());
        assertThat(testing1).isEqualTo(testing2);
        testing2.setId(2L);
        assertThat(testing1).isNotEqualTo(testing2);
        testing1.setId(null);
        assertThat(testing1).isNotEqualTo(testing2);
    }
}
