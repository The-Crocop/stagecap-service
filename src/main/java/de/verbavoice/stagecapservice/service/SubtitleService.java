package de.verbavoice.stagecapservice.service;

import de.verbavoice.stagecapservice.domain.Subtitle;

import java.util.List;

/**
 * Service Interface for managing Subtitle.
 */
public interface SubtitleService {

    /**
     * Save a subtitle.
     *
     * @param subtitle the entity to save
     * @return the persisted entity
     */
    Subtitle save(Subtitle subtitle);

    /**
     *  Get all the subtitles.
     *
     *  @return the list of entities
     */
    List<Subtitle> findAll();

    /**
     *  Get all the subtitles.
     *
     *  @return the list of entities
     */
    List<Subtitle> findAll(Long projectId);

    /**
     *  Get the "id" subtitle.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Subtitle findOne(Long id);

    /**
     *  Delete the "id" subtitle.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the subtitle corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    List<Subtitle> search(String query);
}
