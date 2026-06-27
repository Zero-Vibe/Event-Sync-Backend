package event.sync.repository;

import event.sync.model.SessionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<SessionRegistration, UUID> {
    Optional<SessionRegistration> findBySession_idAndUser_id(UUID sessionId, UUID userId);
    @Query(
        """
        DELETE FROM SessionRegistration AS sr
        WHERE sr.user.id = :userId AND sr.session.id = :sessionId
        """
    )
    @Modifying
    void deleteSessionRegistrationsByIdAndUser_Id(@Param("sessionId") UUID sessionId, @Param("userId") UUID userId);
    long countSessionRegistrationBySessionId(UUID sessionId);
}
