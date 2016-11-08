package de.verbavoice.stagecapservice.repository;

import de.verbavoice.stagecapservice.domain.AudioDescription;
import de.verbavoice.stagecapservice.domain.enumeration.Language;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AudioDescription entity.
 */
public interface AudioDescriptionRepository extends JpaRepository<AudioDescription,Long> {
    List<AudioDescription> findByProjectId(Long projectId);
    List<AudioDescription> findByProjectIdAndLanguage(Long projectId, Language language);
}
