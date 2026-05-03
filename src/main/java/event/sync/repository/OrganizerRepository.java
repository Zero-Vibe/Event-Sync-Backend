package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.model.Organizer;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrganizerRepository {

    private final DataSourceConfig dataSource;

    public OrganizerRepository(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Organizer> findByEmail(String email) {
        String sql = "SELECT id, email, password_hash, name, created_at, updated_at FROM organizers WHERE email = ?";

        Connection conn = dataSource.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find organizer by email", e);
        } finally {
            dataSource.closeConnection(conn);
        }
    }

    public Organizer addOrganizer(Organizer organizer) {
        String sql = "INSERT INTO organizers (id, email, password_hash, name, created_at, updated_at) ) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = dataSource.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, organizer.getEmail());
            statement.setString(3, organizer.getPasswordHash());
            statement.setString(4, organizer.getName());
            statement.setTimestamp(5, Timestamp.valueOf(organizer.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(organizer.getUpdatedAt()));

            statement.executeUpdate();

            return organizer;


        } catch (SQLException e) {
            throw new RuntimeException("Failed to add organizer", e);
        } finally {
            dataSource.closeConnection(conn);
        }

    }

    private Organizer mapRow(ResultSet rs) throws SQLException {
        return Organizer.builder()
                .id(rs.getObject("id", UUID.class))
                .email(rs.getString("email"))
                .passwordHash(rs.getString("password_hash"))
                .name(rs.getString("name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }
}