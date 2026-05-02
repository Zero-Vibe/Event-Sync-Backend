package event.sync.repository;

import event.sync.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Event> rowMapper = (rs, rowNum) -> Event.builder()
            .id(UUID.fromString(rs.getString("id")))
            .title(rs.getString("title"))
            .description(rs.getString("description"))
            .startDate(toLocalDateTime(rs.getTimestamp("start_date")))
            .endDate(toLocalDateTime(rs.getTimestamp("end_date")))
            .location(rs.getString("location"))
            .createdBy(rs.getString("created_by") != null ? UUID.fromString(rs.getString("created_by")) : null)
            .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
            .updatedAt(toLocalDateTime(rs.getTimestamp("updated_at")))
            .sessions(new ArrayList<>())
            .build();

    public List<Event> findAll() {
        return jdbc.query(
                "SELECT id, title, description, start_date, end_date, location, created_by, created_at, updated_at FROM events ORDER BY start_date DESC",
                rowMapper
        );
    }

    public Optional<Event> findById(UUID id) {
        List<Event> results = jdbc.query(
                "SELECT id, title, description, start_date, end_date, location, created_by, created_at, updated_at FROM events WHERE id = ?::uuid",
                rowMapper,
                id.toString()
        );
        return results.stream().findFirst();
    }

    public Event save(Event event) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        jdbc.update(
                "INSERT INTO events (id, title, description, start_date, end_date, location, created_by, created_at, updated_at) VALUES (?::uuid, ?, ?, ?, ?, ?, ?::uuid, ?, ?)",
                id.toString(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLocation(),
                event.getCreatedBy() != null ? event.getCreatedBy().toString() : null,
                now,
                now
        );

        return event.toBuilder().id(id).createdAt(now).updatedAt(now).build();
    }

    public Optional<Event> update(UUID id, Event event) {
        LocalDateTime now = LocalDateTime.now();

        int rows = jdbc.update(
                "UPDATE events SET title = ?, description = ?, start_date = ?, end_date = ?, location = ?, updated_at = ? WHERE id = ?::uuid",
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLocation(),
                now,
                id.toString()
        );

        if (rows == 0) return Optional.empty();
        return findById(id);
    }

    public boolean deleteById(UUID id) {
        return jdbc.update("DELETE FROM events WHERE id = ?::uuid", id.toString()) > 0;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}