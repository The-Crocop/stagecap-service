package de.verbavoice.stagecapservice.web.rest;

import com.codahale.metrics.annotation.Timed;
import de.verbavoice.stagecapservice.domain.AudioDescription;
import de.verbavoice.stagecapservice.domain.enumeration.Language;
import de.verbavoice.stagecapservice.service.AudioDescriptionService;
import de.verbavoice.stagecapservice.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing AudioDescription.
 */
@RestController
@RequestMapping("/api")
public class AudioDescriptionResource {

    private final Logger log = LoggerFactory.getLogger(AudioDescriptionResource.class);

    @Inject
    private AudioDescriptionService audioDescriptionService;

    /**
     * POST  /audio-descriptions : Create a new audioDescription.
     *
     * @param audioDescription the audioDescription to create
     * @return the ResponseEntity with status 201 (Created) and with body the new audioDescription, or with status 400 (Bad Request) if the audioDescription has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/audio-descriptions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AudioDescription> createAudioDescription(@Valid @RequestBody AudioDescription audioDescription) throws URISyntaxException {
        log.debug("REST request to save AudioDescription : {}", audioDescription);
        if (audioDescription.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("audioDescription", "idexists", "A new audioDescription cannot already have an ID")).body(null);
        }
        AudioDescription result = audioDescriptionService.save(audioDescription);
        return ResponseEntity.created(new URI("/api/audio-descriptions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("audioDescription", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /audio-descriptions : Updates an existing audioDescription.
     *
     * @param audioDescription the audioDescription to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated audioDescription,
     * or with status 400 (Bad Request) if the audioDescription is not valid,
     * or with status 500 (Internal Server Error) if the audioDescription couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/audio-descriptions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AudioDescription> updateAudioDescription(@Valid @RequestBody AudioDescription audioDescription) throws URISyntaxException {
        log.debug("REST request to update AudioDescription : {}", audioDescription);
        if (audioDescription.getId() == null) {
            return createAudioDescription(audioDescription);
        }
        AudioDescription result = audioDescriptionService.save(audioDescription);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("audioDescription", audioDescription.getId().toString()))
            .body(result);
    }

    /**
     * GET  /audio-descriptions : get all the audioDescriptions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of audioDescriptions in body
     */
    @RequestMapping(value = "/audio-descriptions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AudioDescription> getAllAudioDescriptions(Long projectId, Language language) {
        log.debug("REST request to get all AudioDescriptions");
        return audioDescriptionService.findAll(projectId,language);
    }

    /**
     * GET  /audio-descriptions/:id : get the "id" audioDescription.
     *
     * @param id the id of the audioDescription to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the audioDescription, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/audio-descriptions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AudioDescription> getAudioDescription(@PathVariable Long id) {
        log.debug("REST request to get AudioDescription : {}", id);
        AudioDescription audioDescription = audioDescriptionService.findOne(id);
        return Optional.ofNullable(audioDescription)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /audio-descriptions/:id : delete the "id" audioDescription.
     *
     * @param id the id of the audioDescription to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/audio-descriptions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAudioDescription(@PathVariable Long id) {
        log.debug("REST request to delete AudioDescription : {}", id);
        audioDescriptionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("audioDescription", id.toString())).build();
    }

    /**
     * SEARCH  /_search/audio-descriptions?query=:query : search for the audioDescription corresponding
     * to the query.
     *
     * @param query the query of the audioDescription search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/audio-descriptions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AudioDescription> searchAudioDescriptions(@RequestParam String query) {
        log.debug("REST request to search AudioDescriptions for query {}", query);
        return audioDescriptionService.search(query);
    }

}
