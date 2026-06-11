package event.sync.repository;

import event.sync.model.SpeakerLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpeakerLinkRepository extends JpaRepository<SpeakerLink, UUID>, JpaSpecificationExecutor<SpeakerLink> {
    @Query("SELECT s FROM SpeakerLink s WHERE s.speaker.id = :speakerId")
    void deleteBySpeakerId(@Param("speakerId") UUID speakerId);
}
