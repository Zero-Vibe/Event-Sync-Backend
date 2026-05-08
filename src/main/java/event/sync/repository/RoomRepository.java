package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.dto.room.RoomWithDetailsResponse;
import event.sync.model.Room;
import event.sync.model.Session;
import event.sync.model.Speaker;
import event.sync.model.enums.SessionStatus;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class RoomRepository {

    private final DataSourceConfig dataSource;

    public RoomRepository(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }


    private final String GET_ALL_ROOMS = "SELECT id, name, created_at, updated_at FROM rooms ";

    public Optional<List<RoomResponse>> getAllRooms() {
        Connection conn = dataSource.getConnection();

        List<RoomResponse> rooms = new ArrayList<RoomResponse>();

        try (PreparedStatement stmt = conn.prepareStatement(GET_ALL_ROOMS)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rooms.add(RoomResponseRowMapper(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }

        return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms);
    }

    private final String CREATE_ROOM = "INSERT INTO rooms (id, name, created_at, updated_at) VALUES (?::uuid, ?, ?, ?)";

    public Optional<RoomResponse> createRoom(RoomRequest roomRequest) {
        Connection conn = dataSource.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(CREATE_ROOM)) {
            String uuid = UUID.randomUUID().toString();
            ps.setString(1, uuid);
            ps.setString(2, roomRequest.getName());
            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

            return Optional.ofNullable(RoomResponse.builder()
                    .id(UUID.fromString(uuid)).name(roomRequest.getName()).build());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }


    private RoomResponse RoomResponseRowMapper(ResultSet rs) throws SQLException {
        return RoomResponse.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .build();

    }

    private Room RoomRowMapper(ResultSet rs) throws SQLException {
        return Room.builder()
                .id(rs.getObject("id", UUID.class))
                .name(rs.getString("name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    private Room rowMapper(ResultSet rs) throws SQLException {
        return Room.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    private final String GET_ROOM_WITH_DETAILS = """
                SELECT
                    r.id::TEXT         AS room_id,
                    r.name             AS room_name,
                    s.id::TEXT         AS session_id,
                    s.title            AS session_title,
                    s.start_time::TEXT AS session_start_time,
                    s.end_time::TEXT   AS session_end_time,
                    s.status           AS session_status,
                    sp.id::TEXT        AS speaker_id,
                    sp.full_name       AS speaker_full_name,
                    sp.profile_picture AS speaker_profile_picture
                FROM sessions s
                JOIN rooms r             ON r.id = s.room_id
                JOIN session_speakers ss ON ss.session_id = s.id
                JOIN speakers sp         ON sp.id = ss.speaker_id
                WHERE r.id = ?::UUID;
            """;


    private RoomWithDetailsResponse RoomWithDetailsResponseRowMapper(ResultSet rs) throws SQLException {
        return RoomWithDetailsResponse.builder()
                .id(rs.getString("room_id"))
                .name(rs.getString("room_name"))
                .session(new ArrayList<>())
                .build();
    }

    private static final DateTimeFormatter PG_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Session sessionRowMapper(ResultSet rs) throws SQLException {
        String rawStart = rs.getString("session_start_time");
        String rawEnd = rs.getString("session_end_time");

        return Session.builder()
                .id(UUID.fromString(rs.getString("session_id")))
                .title(rs.getString("session_title"))
                .startTime(rawStart != null ? LocalDateTime.parse(rawStart, PG_TIMESTAMP) : null)
                .endTime(rawEnd != null ? LocalDateTime.parse(rawEnd, PG_TIMESTAMP) : null)
                .status(SessionStatus.valueOf(rs.getString("session_status")))
                .speakers(new ArrayList<>())
                .build();
    }

    private Speaker speakerRowMapper(ResultSet rs) throws SQLException {
        return Speaker.builder()
                .id(UUID.fromString(rs.getString("speaker_id")))
                .fullName(rs.getString("speaker_full_name"))
                .profilePicture(rs.getString("speaker_profile_picture"))
                .build();
    }

    public Optional<RoomWithDetailsResponse> getRoomWithDetails(String id) {
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(GET_ROOM_WITH_DETAILS)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            RoomWithDetailsResponse room = null;
            Map<UUID, Session> sessionMap = new LinkedHashMap<>();

            while (rs.next()) {
                if (room == null) {
                    room = RoomWithDetailsResponseRowMapper(rs);
                }

                String rawSessionId = rs.getString("session_id");
                if (rawSessionId != null) {
                    UUID sessionId = UUID.fromString(rawSessionId);

                    Session session = sessionMap.computeIfAbsent(sessionId, k -> {
                        try {
                            return sessionRowMapper(rs);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    String rawSpeakerId = rs.getString("speaker_id");
                    if (rawSpeakerId != null) {
                        UUID speakerId = UUID.fromString(rawSpeakerId);
                        boolean speakerExists = session.getSpeakers().stream()
                                .anyMatch(sp -> sp.getId().equals(speakerId));

                        if (!speakerExists) {
                            session.getSpeakers().add(speakerRowMapper(rs));
                        }
                    }
                }
            }

            if (room == null) {
                return Optional.empty();
            }

            room.setSession(new ArrayList<>(sessionMap.values()));
            return Optional.of(room);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    public Optional<Room> findById(UUID id) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            SELECT id, name, created_at, updated_at
                            FROM rooms WHERE id = ?::UUID
                            """
            );
            ps.setString(1, id.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Room room = rowMapper(rs);
                return Optional.of(room);
            }
            return Optional.empty();
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    private final String UPDATE_ROOM = "UPDATE rooms SET name = ?, updated_at = ? WHERE id = ?::uuid";

    public Optional<RoomResponse> updateRoom(String id, RoomRequest roomRequest) {
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_ROOM)) {
            ps.setString(1, roomRequest.getName());
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setString(3, id);
            int rows = ps.executeUpdate();
            if (rows == 0) return Optional.empty();
            return Optional.of(RoomResponse.builder()
                    .id(UUID.fromString(id))
                    .name(roomRequest.getName())
                    .build());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    private final String DELETE_ROOM = "DELETE FROM rooms WHERE id = ?::uuid";

    public boolean deleteRoom(String id) {
        Connection conn = dataSource.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ROOM)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }
}
