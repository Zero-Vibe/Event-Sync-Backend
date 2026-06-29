package event.sync.repository;

import event.sync.model.Session;
import event.sync.model.Speaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session> {
    Page<Session> findAllByEvent_Id(UUID eventId, Pageable pageable);

    @Query(
            """
            SELECT s FROM Session s
            JOIN s.speakers sp
            WHERE sp = :speaker
            ORDER BY s.startTime DESC
            """
    )
    List<Session> findSessionsBySpeaker(@Param("speaker") Speaker speaker);
}
