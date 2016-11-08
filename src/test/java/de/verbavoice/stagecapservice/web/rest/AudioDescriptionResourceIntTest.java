package de.verbavoice.stagecapservice.web.rest;

import de.verbavoice.stagecapservice.StageCapServiceApp;
import de.verbavoice.stagecapservice.domain.AudioDescription;
import de.verbavoice.stagecapservice.repository.AudioDescriptionRepository;
import de.verbavoice.stagecapservice.service.AudioDescriptionService;
import de.verbavoice.stagecapservice.repository.search.AudioDescriptionSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.verbavoice.stagecapservice.domain.enumeration.Language;

/**
 * Test class for the AudioDescriptionResource REST controller.
 *
 * @see AudioDescriptionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StageCapServiceApp.class)
@WebAppConfiguration
@IntegrationTest
public class AudioDescriptionResourceIntTest {


    private static final Language DEFAULT_LANGUAGE = Language.FRENCH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    @Inject
    private AudioDescriptionRepository audioDescriptionRepository;

    @Inject
    private AudioDescriptionService audioDescriptionService;

    @Inject
    private AudioDescriptionSearchRepository audioDescriptionSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAudioDescriptionMockMvc;

    private AudioDescription audioDescription;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AudioDescriptionResource audioDescriptionResource = new AudioDescriptionResource();
        ReflectionTestUtils.setField(audioDescriptionResource, "audioDescriptionService", audioDescriptionService);
        this.restAudioDescriptionMockMvc = MockMvcBuilders.standaloneSetup(audioDescriptionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        audioDescriptionSearchRepository.deleteAll();
        audioDescription = new AudioDescription();
        audioDescription.setLanguage(DEFAULT_LANGUAGE);
        audioDescription.setFile(DEFAULT_FILE);
        audioDescription.setFileContentType(DEFAULT_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createAudioDescription() throws Exception {
        int databaseSizeBeforeCreate = audioDescriptionRepository.findAll().size();

        // Create the AudioDescription

        restAudioDescriptionMockMvc.perform(post("/api/audio-descriptions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(audioDescription)))
                .andExpect(status().isCreated());

        // Validate the AudioDescription in the database
        List<AudioDescription> audioDescriptions = audioDescriptionRepository.findAll();
        assertThat(audioDescriptions).hasSize(databaseSizeBeforeCreate + 1);
        AudioDescription testAudioDescription = audioDescriptions.get(audioDescriptions.size() - 1);
        assertThat(testAudioDescription.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testAudioDescription.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testAudioDescription.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);

        // Validate the AudioDescription in ElasticSearch
        AudioDescription audioDescriptionEs = audioDescriptionSearchRepository.findOne(testAudioDescription.getId());
        assertThat(audioDescriptionEs).isEqualToComparingFieldByField(testAudioDescription);
    }

    @Test
    @Transactional
    public void checkFileIsRequired() throws Exception {
        int databaseSizeBeforeTest = audioDescriptionRepository.findAll().size();
        // set the field null
        audioDescription.setFile(null);

        // Create the AudioDescription, which fails.

        restAudioDescriptionMockMvc.perform(post("/api/audio-descriptions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(audioDescription)))
                .andExpect(status().isBadRequest());

        List<AudioDescription> audioDescriptions = audioDescriptionRepository.findAll();
        assertThat(audioDescriptions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAudioDescriptions() throws Exception {
        // Initialize the database
        audioDescriptionRepository.saveAndFlush(audioDescription);

        // Get all the audioDescriptions
        restAudioDescriptionMockMvc.perform(get("/api/audio-descriptions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(audioDescription.getId().intValue())))
                .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
                .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    public void getAudioDescription() throws Exception {
        // Initialize the database
        audioDescriptionRepository.saveAndFlush(audioDescription);

        // Get the audioDescription
        restAudioDescriptionMockMvc.perform(get("/api/audio-descriptions/{id}", audioDescription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(audioDescription.getId().intValue()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingAudioDescription() throws Exception {
        // Get the audioDescription
        restAudioDescriptionMockMvc.perform(get("/api/audio-descriptions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAudioDescription() throws Exception {
        // Initialize the database
        audioDescriptionService.save(audioDescription);

        int databaseSizeBeforeUpdate = audioDescriptionRepository.findAll().size();

        // Update the audioDescription
        AudioDescription updatedAudioDescription = new AudioDescription();
        updatedAudioDescription.setId(audioDescription.getId());
        updatedAudioDescription.setLanguage(UPDATED_LANGUAGE);
        updatedAudioDescription.setFile(UPDATED_FILE);
        updatedAudioDescription.setFileContentType(UPDATED_FILE_CONTENT_TYPE);

        restAudioDescriptionMockMvc.perform(put("/api/audio-descriptions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAudioDescription)))
                .andExpect(status().isOk());

        // Validate the AudioDescription in the database
        List<AudioDescription> audioDescriptions = audioDescriptionRepository.findAll();
        assertThat(audioDescriptions).hasSize(databaseSizeBeforeUpdate);
        AudioDescription testAudioDescription = audioDescriptions.get(audioDescriptions.size() - 1);
        assertThat(testAudioDescription.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testAudioDescription.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testAudioDescription.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);

        // Validate the AudioDescription in ElasticSearch
        AudioDescription audioDescriptionEs = audioDescriptionSearchRepository.findOne(testAudioDescription.getId());
        assertThat(audioDescriptionEs).isEqualToComparingFieldByField(testAudioDescription);
    }

    @Test
    @Transactional
    public void deleteAudioDescription() throws Exception {
        // Initialize the database
        audioDescriptionService.save(audioDescription);

        int databaseSizeBeforeDelete = audioDescriptionRepository.findAll().size();

        // Get the audioDescription
        restAudioDescriptionMockMvc.perform(delete("/api/audio-descriptions/{id}", audioDescription.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean audioDescriptionExistsInEs = audioDescriptionSearchRepository.exists(audioDescription.getId());
        assertThat(audioDescriptionExistsInEs).isFalse();

        // Validate the database is empty
        List<AudioDescription> audioDescriptions = audioDescriptionRepository.findAll();
        assertThat(audioDescriptions).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAudioDescription() throws Exception {
        // Initialize the database
        audioDescriptionService.save(audioDescription);

        // Search the audioDescription
        restAudioDescriptionMockMvc.perform(get("/api/_search/audio-descriptions?query=id:" + audioDescription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(audioDescription.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }
}
