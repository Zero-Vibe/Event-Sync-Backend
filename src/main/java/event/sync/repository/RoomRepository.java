package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.model.Room;
import event.sync.model.Session;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomRepository {
    private DataSourceConfig dataSource;

    private Room rowMapper(ResultSet rs) throws SQLException {
        return Room.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    // Does not include sessions yet
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
}
