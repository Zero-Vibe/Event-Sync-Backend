package event.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, referencedColumnName = "id")
    @JsonIgnore
    private Session session;
    private UUID getSessionId() { return (session != null) ? session.getId() : null; }

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;   // null = anonymous

    @Builder.Default
    @Column(nullable = false)
    private int upvotes = 0;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
