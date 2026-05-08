package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.model.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class SessionRepository {
    private final SpeakerRepository speakerRepository;
    private DataSourceConfig dataSource;

    private Session rowMapper(ResultSet rs) throws SQLException {
        return Session.builder()
                .id(UUID.fromString(rs.getString("id")))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .capacity(rs.getInt("capacity"))
                .eventId(UUID.fromString(rs.getString("event_id")))
                .roomId(UUID.fromString(rs.getString("room_id")))
                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    public Optional<Session> findById(UUID id) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                """
                SELECT id, event_id, room_id, title, description, start_time, end_time, capacity, status, created_at, updated_at
                FROM sessions WHERE id = ?::UUID
                """
            );
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Session session = rowMapper(rs);
                session.setSpeakers(speakerRepository.getBySessionId(id));
                return Optional.of(session);
            }
            return Optional.empty();
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public List<Session> getAll(UUID eventId) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    SELECT id, event_id, room_id, title, description, start_time, end_time, capacity, status, created_at, updated_at
                    FROM sessions WHERE event_id = ?::UUID
                    """
            );
            ps.setObject(1, eventId);
            ResultSet rs = ps.executeQuery();
            List<Session> sessions = new ArrayList<>();
            while (rs.next()) {
                Session session = rowMapper(rs);
                session.setSpeakers(speakerRepository.getBySessionId(UUID.fromString(rs.getString("id"))));
                sessions.add(session);
            }
            return sessions;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}
