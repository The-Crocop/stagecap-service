package de.verbavoice.stagecapservice.service;

import de.verbavoice.stagecapservice.domain.AudioDescription;
import de.verbavoice.stagecapservice.domain.enumeration.Language;

import java.util.List;

/**
 * Service Interface for managing AudioDescription.
 */
public interface AudioDescriptionService {

    /**
     * Save a audioDescription.
     *
     * @param audioDescription the entity to save
     * @return the persisted entity
     */
    AudioDescription save(AudioDescription audioDescription);

    /**
     *  Get all the audioDescriptions.
     *
     *  @return the list of entities
     */
    List<AudioDescription> findAll();

    /**
     *  Get all the audioDescriptions.
     *
     *  @return the list of entities
     */
    List<AudioDescription> findAll(Long projectId);

    /**
     *  Get all the audioDescriptions.
     *
     *  @return the list of entities
     */
    List<AudioDescription> findAll(Long projectId, Language language);
    /**
     *  Get the "id" audioDescription.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    AudioDescription findOne(Long id);

    /**
     *  Delete the "id" audioDescription.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the audioDescription corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    List<AudioDescription> search(String query);
}
