package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.dto.session.SessionCreateRequest;
import event.sync.model.Session;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

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

    public Session create(UUID eventId, SessionCreateRequest session) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO sessions (event_id, room_id, title, description, start_time, end_time, capacity)
                    VALUES (?::UUID, ?::UUID, ?, ?, ?, ?, ?)
                    RETURNING id
                    """
            );
            ps.setString(1, eventId.toString());
            ps.setString(2, session.getRoomId().toString());
            ps.setString(3, session.getTitle());
            ps.setString(4, session.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(6, Timestamp.valueOf(session.getEndTime()));
            ps.setInt(7, session.getCapacity());

            PreparedStatement psSpeakers = connection.prepareStatement(
                    """
                    INSERT INTO session_speakers (session_id, speaker_id)
                    VALUES (?::UUID, ?::UUID);
                    """
            );

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create session");
            }
            String sessionId = rs.getString("id");

            psSpeakers.setString(1, sessionId);
            for (UUID speakerUUID : session.getSpeakersId()) {
                psSpeakers.setString(2, speakerUUID.toString());
                psSpeakers.addBatch();
            }
            psSpeakers.executeBatch();

            connection.commit();

            return findById(UUID.fromString(sessionId)).get();
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Session update(UUID sessionId, SessionCreateRequest session) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                    UPDATE sessions SET room_id = ?::UUID, title = ?, description = ?, start_time = ?, end_time = ?, capacity = ?
                    WHERE id = ?::UUID
                    """
            );
            ps.setString(1, session.getRoomId().toString());
            ps.setString(2, session.getTitle());
            ps.setString(3, session.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(session.getEndTime()));
            ps.setInt(6, session.getCapacity());
            ps.setString(7, sessionId.toString());

            PreparedStatement psSpeakers = connection.prepareStatement(
                    """
                    INSERT INTO session_speakers (session_id, speaker_id)
                    VALUES (?::UUID, ?::UUID);
                    """
            );

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create session");
            }
            PreparedStatement deletePs = connection.prepareStatement(
                    """
                    DELETE FROM session_speakers WHERE session_id = ?::UUID
                    """);
            deletePs.setString(1, sessionId.toString());
            deletePs.executeUpdate();

            psSpeakers.setString(1, sessionId.toString());
            for (UUID speakerUUID : session.getSpeakersId()) {
                psSpeakers.setString(2, speakerUUID.toString());
                psSpeakers.addBatch();
            }
            psSpeakers.executeBatch();

            connection.commit();

            return findById(sessionId).get();
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public void delete(UUID sessionId) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                    DELETE FROM sessions WHERE id = ?::UUID
                    """
            );
            ps.setString(1, sessionId.toString());


            PreparedStatement deletePs = connection.prepareStatement(
                    """
                    DELETE FROM session_speakers WHERE session_id = ?::UUID
                    """);
            deletePs.setString(1, sessionId.toString());

            deletePs.executeUpdate();
            ps.executeUpdate();

            connection.commit();
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}
