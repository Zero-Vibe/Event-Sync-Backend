package event.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session_registrations")
public class SessionRegistration {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "session_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Session session;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column
    private Instant registrationTime;

    private UUID getSessionId() {
        return (session != null) ? session.getId() : null;
    };

    private UUID getUserId() {
        return (user != null) ? user.getId() : null;
    };
}
