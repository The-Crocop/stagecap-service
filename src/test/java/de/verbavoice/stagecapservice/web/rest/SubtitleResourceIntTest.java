package de.verbavoice.stagecapservice.web.rest;

import de.verbavoice.stagecapservice.StageCapServiceApp;
import de.verbavoice.stagecapservice.domain.Subtitle;
import de.verbavoice.stagecapservice.repository.SubtitleRepository;
import de.verbavoice.stagecapservice.service.SubtitleService;
import de.verbavoice.stagecapservice.repository.search.SubtitleSearchRepository;

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
 * Test class for the SubtitleResource REST controller.
 *
 * @see SubtitleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StageCapServiceApp.class)
@WebAppConfiguration
@IntegrationTest
public class SubtitleResourceIntTest {


    private static final Language DEFAULT_LANGUAGE = Language.FRENCH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    @Inject
    private SubtitleRepository subtitleRepository;

    @Inject
    private SubtitleService subtitleService;

    @Inject
    private SubtitleSearchRepository subtitleSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSubtitleMockMvc;

    private Subtitle subtitle;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SubtitleResource subtitleResource = new SubtitleResource();
        ReflectionTestUtils.setField(subtitleResource, "subtitleService", subtitleService);
        this.restSubtitleMockMvc = MockMvcBuilders.standaloneSetup(subtitleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        subtitleSearchRepository.deleteAll();
        subtitle = new Subtitle();
        subtitle.setLanguage(DEFAULT_LANGUAGE);
        subtitle.setFile(DEFAULT_FILE);
        subtitle.setFileContentType(DEFAULT_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createSubtitle() throws Exception {
        int databaseSizeBeforeCreate = subtitleRepository.findAll().size();

        // Create the Subtitle

        restSubtitleMockMvc.perform(post("/api/subtitles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subtitle)))
                .andExpect(status().isCreated());

        // Validate the Subtitle in the database
        List<Subtitle> subtitles = subtitleRepository.findAll();
        assertThat(subtitles).hasSize(databaseSizeBeforeCreate + 1);
        Subtitle testSubtitle = subtitles.get(subtitles.size() - 1);
        assertThat(testSubtitle.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testSubtitle.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testSubtitle.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);

        // Validate the Subtitle in ElasticSearch
        Subtitle subtitleEs = subtitleSearchRepository.findOne(testSubtitle.getId());
        assertThat(subtitleEs).isEqualToComparingFieldByField(testSubtitle);
    }

    @Test
    @Transactional
    public void checkLanguageIsRequired() throws Exception {
        int databaseSizeBeforeTest = subtitleRepository.findAll().size();
        // set the field null
        subtitle.setLanguage(null);

        // Create the Subtitle, which fails.

        restSubtitleMockMvc.perform(post("/api/subtitles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subtitle)))
                .andExpect(status().isBadRequest());

        List<Subtitle> subtitles = subtitleRepository.findAll();
        assertThat(subtitles).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFileIsRequired() throws Exception {
        int databaseSizeBeforeTest = subtitleRepository.findAll().size();
        // set the field null
        subtitle.setFile(null);

        // Create the Subtitle, which fails.

        restSubtitleMockMvc.perform(post("/api/subtitles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(subtitle)))
                .andExpect(status().isBadRequest());

        List<Subtitle> subtitles = subtitleRepository.findAll();
        assertThat(subtitles).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSubtitles() throws Exception {
        // Initialize the database
        subtitleRepository.saveAndFlush(subtitle);

        // Get all the subtitles
        restSubtitleMockMvc.perform(get("/api/subtitles?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(subtitle.getId().intValue())))
                .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
                .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    public void getSubtitle() throws Exception {
        // Initialize the database
        subtitleRepository.saveAndFlush(subtitle);

        // Get the subtitle
        restSubtitleMockMvc.perform(get("/api/subtitles/{id}", subtitle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(subtitle.getId().intValue()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingSubtitle() throws Exception {
        // Get the subtitle
        restSubtitleMockMvc.perform(get("/api/subtitles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSubtitle() throws Exception {
        // Initialize the database
        subtitleService.save(subtitle);

        int databaseSizeBeforeUpdate = subtitleRepository.findAll().size();

        // Update the subtitle
        Subtitle updatedSubtitle = new Subtitle();
        updatedSubtitle.setId(subtitle.getId());
        updatedSubtitle.setLanguage(UPDATED_LANGUAGE);
        updatedSubtitle.setFile(UPDATED_FILE);
        updatedSubtitle.setFileContentType(UPDATED_FILE_CONTENT_TYPE);

        restSubtitleMockMvc.perform(put("/api/subtitles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSubtitle)))
                .andExpect(status().isOk());

        // Validate the Subtitle in the database
        List<Subtitle> subtitles = subtitleRepository.findAll();
        assertThat(subtitles).hasSize(databaseSizeBeforeUpdate);
        Subtitle testSubtitle = subtitles.get(subtitles.size() - 1);
        assertThat(testSubtitle.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testSubtitle.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testSubtitle.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);

        // Validate the Subtitle in ElasticSearch
        Subtitle subtitleEs = subtitleSearchRepository.findOne(testSubtitle.getId());
        assertThat(subtitleEs).isEqualToComparingFieldByField(testSubtitle);
    }

    @Test
    @Transactional
    public void deleteSubtitle() throws Exception {
        // Initialize the database
        subtitleService.save(subtitle);

        int databaseSizeBeforeDelete = subtitleRepository.findAll().size();

        // Get the subtitle
        restSubtitleMockMvc.perform(delete("/api/subtitles/{id}", subtitle.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean subtitleExistsInEs = subtitleSearchRepository.exists(subtitle.getId());
        assertThat(subtitleExistsInEs).isFalse();

        // Validate the database is empty
        List<Subtitle> subtitles = subtitleRepository.findAll();
        assertThat(subtitles).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSubtitle() throws Exception {
        // Initialize the database
        subtitleService.save(subtitle);

        // Search the subtitle
        restSubtitleMockMvc.perform(get("/api/_search/subtitles?query=id:" + subtitle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subtitle.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }
}
