package event.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import event.sync.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, referencedColumnName = "id")
    private Event event;
    public UUID getEventId() { return (event != null) ? event.getId() : null; }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, referencedColumnName = "id")
    private Room room;
    public UUID getRoomId() { return (room != null) ? room.getId() : null; }

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMP CHECK (end_time::TIMESTAMP >= start_time::TIMESTAMP)")
    private LocalDateTime endTime;

    @Column
    private Integer capacity;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SessionStatus status = SessionStatus.PUBLISHED;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable
    private List<Speaker> speakers = new ArrayList<>();

    @JsonIgnore
    public boolean isLive() {
        status = (LocalDateTime.now().isAfter(endTime)) ? SessionStatus.ENDED :
                LocalDateTime.now().isBefore(startTime) ? SessionStatus.PUBLISHED : SessionStatus.LIVE;
        return SessionStatus.LIVE.equals(this.status);
    }
}