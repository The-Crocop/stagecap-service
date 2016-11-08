package de.verbavoice.stagecapservice.service.impl;

import de.verbavoice.stagecapservice.service.SubtitleService;
import de.verbavoice.stagecapservice.domain.Subtitle;
import de.verbavoice.stagecapservice.repository.SubtitleRepository;
import de.verbavoice.stagecapservice.repository.search.SubtitleSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Subtitle.
 */
@Service
@Transactional
public class SubtitleServiceImpl implements SubtitleService{

    private final Logger log = LoggerFactory.getLogger(SubtitleServiceImpl.class);

    @Inject
    private SubtitleRepository subtitleRepository;

    @Inject
    private SubtitleSearchRepository subtitleSearchRepository;

    /**
     * Save a subtitle.
     *
     * @param subtitle the entity to save
     * @return the persisted entity
     */
    public Subtitle save(Subtitle subtitle) {
        log.debug("Request to save Subtitle : {}", subtitle);
        Subtitle result = subtitleRepository.save(subtitle);
        subtitleSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the subtitles.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Subtitle> findAll() {
        log.debug("Request to get all Subtitles");
        List<Subtitle> result = subtitleRepository.findAll();
        return result;
    }

    @Override
    public List<Subtitle> findAll(Long projectId) {
        log.debug("Request to get all Subtitles for project {}",projectId);
        List<Subtitle> result = subtitleRepository.findByProjectId(projectId);
        return result;
    }

    /**
     *  Get one subtitle by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Subtitle findOne(Long id) {
        log.debug("Request to get Subtitle : {}", id);
        Subtitle subtitle = subtitleRepository.findOne(id);
        return subtitle;
    }

    /**
     *  Delete the  subtitle by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Subtitle : {}", id);
        subtitleRepository.delete(id);
        subtitleSearchRepository.delete(id);
    }

    /**
     * Search for the subtitle corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Subtitle> search(String query) {
        log.debug("Request to search Subtitles for query {}", query);
        return StreamSupport
            .stream(subtitleSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
