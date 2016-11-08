package de.verbavoice.stagecapservice.repository.search;

import de.verbavoice.stagecapservice.domain.AudioDescription;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the AudioDescription entity.
 */
public interface AudioDescriptionSearchRepository extends ElasticsearchRepository<AudioDescription, Long> {
}
