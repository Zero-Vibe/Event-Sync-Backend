package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.dto.speaker.SpeakerLinkRequest;
import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
import event.sync.model.enums.LinkType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class SpeakerRepository {
    private DataSourceConfig dataSource;

    private Speaker rowMapper(ResultSet rs) throws SQLException {
        return Speaker.builder()
                .id(UUID.fromString(rs.getString("id")))
                .fullName(rs.getString("full_name"))
                .profilePicture(rs.getString("profile_picture"))
                .biography(rs.getString("biography"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    private SpeakerLink speakerLinkMapper(ResultSet rs) throws SQLException {
        return SpeakerLink.builder()
                .id(UUID.fromString(rs.getString("id")))
                .speakerId(UUID.fromString(rs.getString("speaker_id")))
                .type(LinkType.valueOf(rs.getString("type")))
                .url(rs.getString("url"))
                .label(rs.getString("label"))
                .order(rs.getShort("order"))
                .build();
    }

    public List<Speaker> getBySessionId(UUID sessionId) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    SELECT s.id, full_name, profile_picture, biography, created_at, updated_at
                    FROM speakers AS s
                    JOIN session_speakers ON s.id = speaker_id
                    WHERE session_id = ?::UUID
                    """
            );

            ps.setString(1, sessionId.toString());
            ResultSet rs = ps.executeQuery();
            List<Speaker> speakers = new ArrayList<>();
            while (rs.next()) {
                Speaker speaker = rowMapper(rs);
                speaker.setLinks(getSpeakerLinks(connection, speaker.getId()));
                speakers.add(speaker);
            }
            return speakers;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Optional<Speaker> findById(UUID id) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    SELECT s.id, full_name, profile_picture, biography, created_at, updated_at
                    FROM speakers AS s
                    WHERE s.id = ?::UUID
                    """
            );

            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Speaker speaker = rowMapper(rs);
                speaker.setLinks(getSpeakerLinks(connection, speaker.getId()));
                return Optional.of(speaker);
            }
            return Optional.empty();
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    private List<SpeakerLink> getSpeakerLinks(Connection connection, UUID id) {
        try {
            PreparedStatement linksPs = connection.prepareStatement(
                    """
                    SELECT sl.id, speaker_id, type, url, label, "order"
                    FROM speaker_links AS sl
                    JOIN speakers AS s ON sl.speaker_id = s.id
                    WHERE s.id = ?::UUID
                    """
            );
            linksPs.setString(1, id.toString());
            ResultSet rsLinks = linksPs.executeQuery();
            List<SpeakerLink> speakerLinks = new ArrayList<>();
            while (rsLinks.next()) {
                SpeakerLink speakerLink = speakerLinkMapper(rsLinks);
                speakerLinks.add(speakerLink);
            }
            return speakerLinks;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Speaker> getAll() {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    SELECT s.id, full_name, profile_picture, biography, created_at, updated_at
                    FROM speakers AS s
                    """
            );

            ResultSet rs = ps.executeQuery();
            List<Speaker> speakers = new ArrayList<>();
            while (rs.next()) {
                Speaker speaker = rowMapper(rs);
                speaker.setLinks(getSpeakerLinks(connection, speaker.getId()));
                speakers.add(speaker);
            }
            return speakers;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Speaker create(SpeakerCreateRequest speaker) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO speakers (full_name, profile_picture, biography)
                    VALUES (?, ?, ?)
                    RETURNING id;
                    """
            );
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getProfilePicture());
            ps.setString(3, speaker.getBiography());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create speaker");
            }
            UUID speakerId = UUID.fromString(rs.getString("id"));
            createLinks(connection, speakerId, speaker.getSpeakerLinks());
            connection.commit();

            return findById(speakerId).get();
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    private void createLinks(Connection connection, UUID speakerId, List<SpeakerLinkRequest> speakerLinks) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO speaker_links (speaker_id, type, url, label)
                    VALUES (?::UUID, ?::link_type, ?, ?)
                    RETURNING id;
                    """
            );
            ps.setString(1, speakerId.toString());

            for (SpeakerLinkRequest speakerLink : speakerLinks) {
                ps.setString(2, speakerLink.getLinkType().name());
                ps.setString(3, speakerLink.getUrl());
                ps.setString(4, speakerLink.getLabel());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Speaker update(UUID speakerId, SpeakerCreateRequest speaker) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                    UPDATE speakers SET full_name = ?, profile_picture = ?, biography = ?
                    WHERE id = ?;
                    """
            );
            ps.setString(1, speaker.getFullName());
            ps.setString(2, speaker.getProfilePicture());
            ps.setString(3, speaker.getBiography());
            ps.setString(4, speakerId.toString());
            ps.executeUpdate();

            PreparedStatement deleteLinks = connection.prepareStatement(
                    """
                    DELETE FROM speaker_links WHERE id = ?::UUID
                    """
            );
            deleteLinks.setString(1, speakerId.toString());
            deleteLinks.executeUpdate();

            createLinks(connection, speakerId, speaker.getSpeakerLinks());
            connection.commit();

            return findById(speakerId).get();
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}
