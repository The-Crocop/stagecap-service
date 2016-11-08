package de.verbavoice.stagecapservice.service.impl;

import de.verbavoice.stagecapservice.domain.enumeration.Language;
import de.verbavoice.stagecapservice.service.AudioDescriptionService;
import de.verbavoice.stagecapservice.domain.AudioDescription;
import de.verbavoice.stagecapservice.repository.AudioDescriptionRepository;
import de.verbavoice.stagecapservice.repository.search.AudioDescriptionSearchRepository;
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
 * Service Implementation for managing AudioDescription.
 */
@Service
@Transactional
public class AudioDescriptionServiceImpl implements AudioDescriptionService{

    private final Logger log = LoggerFactory.getLogger(AudioDescriptionServiceImpl.class);

    @Inject
    private AudioDescriptionRepository audioDescriptionRepository;

    @Inject
    private AudioDescriptionSearchRepository audioDescriptionSearchRepository;

    /**
     * Save a audioDescription.
     *
     * @param audioDescription the entity to save
     * @return the persisted entity
     */
    public AudioDescription save(AudioDescription audioDescription) {
        log.debug("Request to save AudioDescription : {}", audioDescription);
        AudioDescription result = audioDescriptionRepository.save(audioDescription);
        audioDescriptionSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the audioDescriptions.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<AudioDescription> findAll() {
        log.debug("Request to get all AudioDescriptions");
        return audioDescriptionRepository.findAll();
    }

    @Override
    public List<AudioDescription> findAll(Long projectId) {
        log.debug("Request to get all AudioDescriptions for project {}",projectId);
        return audioDescriptionRepository.findByProjectId(projectId);
    }

    @Override
    public List<AudioDescription> findAll(Long projectId, Language language) {
        if(projectId != null && language != null)return audioDescriptionRepository.findByProjectIdAndLanguage(projectId,language);
        else if (projectId != null)return audioDescriptionRepository.findByProjectId(projectId);
        else return audioDescriptionRepository.findAll();
    }

    /**
     *  Get one audioDescription by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public AudioDescription findOne(Long id) {
        log.debug("Request to get AudioDescription : {}", id);
        AudioDescription audioDescription = audioDescriptionRepository.findOne(id);
        return audioDescription;
    }

    /**
     *  Delete the  audioDescription by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete AudioDescription : {}", id);
        audioDescriptionRepository.delete(id);
        audioDescriptionSearchRepository.delete(id);
    }

    /**
     * Search for the audioDescription corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<AudioDescription> search(String query) {
        log.debug("Request to search AudioDescriptions for query {}", query);
        return StreamSupport
            .stream(audioDescriptionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
