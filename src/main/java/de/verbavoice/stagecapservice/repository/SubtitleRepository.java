package de.verbavoice.stagecapservice.repository;

import de.verbavoice.stagecapservice.domain.Subtitle;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Subtitle entity.
 */
public interface SubtitleRepository extends JpaRepository<Subtitle,Long> {
        List<Subtitle> findByProjectId(Long projectId);
}
