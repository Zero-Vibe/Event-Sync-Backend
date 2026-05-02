package event.sync.repository;

import event.sync.model.Evenement;
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
public class EvenementRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Evenement> rowMapper = (rs, rowNum) -> Evenement.builder()
            .id(UUID.fromString(rs.getString("id")))
            .titre(rs.getString("titre"))
            .description(rs.getString("description"))
            .dateDebut(toLocalDateTime(rs.getTimestamp("date_debut")))
            .dateFin(toLocalDateTime(rs.getTimestamp("date_fin")))
            .lieu(rs.getString("lieu"))
            .createdBy(rs.getString("created_by") != null ? UUID.fromString(rs.getString("created_by")) : null)
            .createdAt(toLocalDateTime(rs.getTimestamp("created_at")))
            .updatedAt(toLocalDateTime(rs.getTimestamp("updated_at")))
            .sessions(new ArrayList<>())
            .build();

    public List<Evenement> findAll() {
        return jdbc.query(
                "SELECT id, titre, description, date_debut, date_fin, lieu, created_by, created_at, updated_at FROM evenements ORDER BY date_debut DESC",
                rowMapper
        );
    }

    public Optional<Evenement> findById(UUID id) {
        List<Evenement> results = jdbc.query(
                "SELECT id, titre, description, date_debut, date_fin, lieu, created_by, created_at, updated_at FROM evenements WHERE id = ?::uuid",
                rowMapper,
                id.toString()
        );
        return results.stream().findFirst();
    }

    public Evenement save(Evenement evenement) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        jdbc.update(
                "INSERT INTO evenements (id, titre, description, date_debut, date_fin, lieu, created_by, created_at, updated_at) VALUES (?::uuid, ?, ?, ?, ?, ?, ?::uuid, ?, ?)",
                id.toString(),
                evenement.getTitre(),
                evenement.getDescription(),
                evenement.getDateDebut(),
                evenement.getDateFin(),
                evenement.getLieu(),
                evenement.getCreatedBy() != null ? evenement.getCreatedBy().toString() : null,
                now,
                now
        );

        return evenement.toBuilder().id(id).createdAt(now).updatedAt(now).build();
    }

    public Optional<Evenement> update(UUID id, Evenement evenement) {
        LocalDateTime now = LocalDateTime.now();

        int rows = jdbc.update(
                "UPDATE evenements SET titre = ?, description = ?, date_debut = ?, date_fin = ?, lieu = ?, updated_at = ? WHERE id = ?::uuid",
                evenement.getTitre(),
                evenement.getDescription(),
                evenement.getDateDebut(),
                evenement.getDateFin(),
                evenement.getLieu(),
                now,
                id.toString()
        );

        if (rows == 0) return Optional.empty();
        return findById(id);
    }

    public boolean deleteById(UUID id) {
        return jdbc.update("DELETE FROM evenements WHERE id = ?::uuid", id.toString()) > 0;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}