package event.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import event.sync.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;
    public UUID getEventId() { return (event != null) ? event.getId() : null; }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Room room;
    public UUID getRoomId() { return (room != null) ? room.getId() : null; }

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMPTZ CHECK (end_time >= start_time)")
    private Instant endTime;

    @Column
    private Integer capacity;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sessions_speakers",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "speakers_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Speaker> speakers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<SessionRegistration> sessionRegistrations;

    @JsonIgnore
    public boolean isLive() {
        return Instant.now().isAfter(startTime) && Instant.now().isBefore(endTime);
    }
}