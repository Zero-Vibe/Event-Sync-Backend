package event.sync.repository;

import event.sync.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, UUID>, JpaSpecificationExecutor<Speaker> {
}
