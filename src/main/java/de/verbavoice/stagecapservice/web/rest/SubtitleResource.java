package de.verbavoice.stagecapservice.web.rest;

import com.codahale.metrics.annotation.Timed;
import de.verbavoice.stagecapservice.domain.Subtitle;
import de.verbavoice.stagecapservice.service.SubtitleService;
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
 * REST controller for managing Subtitle.
 */
@RestController
@RequestMapping("/api")
public class SubtitleResource {

    private final Logger log = LoggerFactory.getLogger(SubtitleResource.class);

    @Inject
    private SubtitleService subtitleService;

    /**
     * POST  /subtitles : Create a new subtitle.
     *
     * @param subtitle the subtitle to create
     * @return the ResponseEntity with status 201 (Created) and with body the new subtitle, or with status 400 (Bad Request) if the subtitle has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subtitles",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subtitle> createSubtitle(@Valid @RequestBody Subtitle subtitle) throws URISyntaxException {
        log.debug("REST request to save Subtitle : {}", subtitle);
        if (subtitle.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("subtitle", "idexists", "A new subtitle cannot already have an ID")).body(null);
        }
        Subtitle result = subtitleService.save(subtitle);
        return ResponseEntity.created(new URI("/api/subtitles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("subtitle", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /subtitles : Updates an existing subtitle.
     *
     * @param subtitle the subtitle to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated subtitle,
     * or with status 400 (Bad Request) if the subtitle is not valid,
     * or with status 500 (Internal Server Error) if the subtitle couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subtitles",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subtitle> updateSubtitle(@Valid @RequestBody Subtitle subtitle) throws URISyntaxException {
        log.debug("REST request to update Subtitle : {}", subtitle);
        if (subtitle.getId() == null) {
            return createSubtitle(subtitle);
        }
        Subtitle result = subtitleService.save(subtitle);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("subtitle", subtitle.getId().toString()))
            .body(result);
    }

    /**
     * GET  /subtitles : get all the subtitles.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of subtitles in body
     */
    @RequestMapping(value = "/subtitles",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Subtitle> getAllSubtitles(Long projectId) {
        log.debug("REST request to get all Subtitles");
        return projectId != null? subtitleService.findAll(projectId):subtitleService.findAll();
    }

    /**
     * GET  /subtitles/:id : get the "id" subtitle.
     *
     * @param id the id of the subtitle to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the subtitle, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/subtitles/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subtitle> getSubtitle(@PathVariable Long id) {
        log.debug("REST request to get Subtitle : {}", id);
        Subtitle subtitle = subtitleService.findOne(id);
        return Optional.ofNullable(subtitle)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /subtitles/:id : delete the "id" subtitle.
     *
     * @param id the id of the subtitle to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/subtitles/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSubtitle(@PathVariable Long id) {
        log.debug("REST request to delete Subtitle : {}", id);
        subtitleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subtitle", id.toString())).build();
    }

    /**
     * SEARCH  /_search/subtitles?query=:query : search for the subtitle corresponding
     * to the query.
     *
     * @param query the query of the subtitle search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/subtitles",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Subtitle> searchSubtitles(@RequestParam String query) {
        log.debug("REST request to search Subtitles for query {}", query);
        return subtitleService.search(query);
    }

}
