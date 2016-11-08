package de.verbavoice.stagecapservice.repository.search;

import de.verbavoice.stagecapservice.domain.Subtitle;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Subtitle entity.
 */
public interface SubtitleSearchRepository extends ElasticsearchRepository<Subtitle, Long> {
}
