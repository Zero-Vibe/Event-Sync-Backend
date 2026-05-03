package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final DataSourceConfig dataSourceConfig;

    private Event mapRow(ResultSet rs) throws SQLException {
        return Event.builder()
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
    }

    public List<Event> findAll() {
        String sql = "SELECT id, title, description, start_date, end_date, location, created_by, created_at, updated_at FROM events ORDER BY start_date DESC";
        List<Event> events = new ArrayList<>();
        Connection conn = dataSourceConfig.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                events.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch events: " + e.getMessage(), e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return events;
    }

    public Optional<Event> findById(UUID id) {
        String sql = "SELECT id, title, description, start_date, end_date, location, created_by, created_at, updated_at FROM events WHERE id = ?::uuid";
        Connection conn = dataSourceConfig.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch event by id: " + e.getMessage(), e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return Optional.empty();
    }

    public Event save(Event event) {
        String sql = "INSERT INTO events (id, title, description, start_date, end_date, location, created_by, created_at, updated_at) VALUES (?::uuid, ?, ?, ?, ?, ?, ?::uuid, ?, ?)";
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Connection conn = dataSourceConfig.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(event.getStartDate()));
            ps.setTimestamp(5, Timestamp.valueOf(event.getEndDate()));
            ps.setString(6, event.getLocation());
            ps.setString(7, event.getCreatedBy() != null ? event.getCreatedBy().toString() : null);
            ps.setTimestamp(8, Timestamp.valueOf(now));
            ps.setTimestamp(9, Timestamp.valueOf(now));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save event: " + e.getMessage(), e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return event.toBuilder().id(id).createdAt(now).updatedAt(now).build();
    }

    public Optional<Event> update(UUID id, Event event) {
        String sql = "UPDATE events SET title = ?, description = ?, start_date = ?, end_date = ?, location = ?, updated_at = ? WHERE id = ?::uuid";
        LocalDateTime now = LocalDateTime.now();
        Connection conn = dataSourceConfig.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
            ps.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            ps.setString(5, event.getLocation());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setString(7, id.toString());
            int rows = ps.executeUpdate();
            if (rows == 0) return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update event: " + e.getMessage(), e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return findById(id);
    }

    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM events WHERE id = ?::uuid";
        Connection conn = dataSourceConfig.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete event: " + e.getMessage(), e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}