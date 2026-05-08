package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.dto.question.QuestionCreateRequest;
import event.sync.model.Question;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class QuestionRepository {
    private DataSourceConfig dataSource;

    private Question rowMapper(ResultSet rs) throws SQLException {
        return Question.builder()
                .id(UUID.fromString(rs.getString(1)))
                .sessionId(UUID.fromString(rs.getString(2)))
                .content(rs.getString(3))
                .authorName(rs.getString(4))
                .upvotes(rs.getInt(5))
                .createdAt(rs.getTimestamp(6).toLocalDateTime())
                .build();
    }

    public List<Question> getSessionQuestions(UUID sessionId) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
            """
               SELECT id, session_id, content, author_name, upvotes, created_at
               FROM questions WHERE session_id = ?::UUID
               """);
            ps.setString(1, sessionId.toString());
            ResultSet rs = ps.executeQuery();
            List<Question> questions = new ArrayList<>();
            while (rs.next()) {
                questions.add(rowMapper(rs));
            }
            return questions;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Optional<Question> findById(UUID id) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                       SELECT id, session_id, content, author_name, upvotes, created_at
                       FROM questions WHERE id = ?::UUID
                       """);
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rowMapper(rs));
            }
            return Optional.empty();
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Optional<Question> create(UUID sessionId, QuestionCreateRequest question) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                       INSERT INTO questions (session_id, content, author_name)
                       VALUES (?::UUID, ?, ?)
                       RETURNING id
                       """);
            ps.setString(1, sessionId.toString());
            ps.setString(2, question.getContent());

            if ((question.getAuthorName() != null)) {
                ps.setString(3, question.getAuthorName());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Question could not be created");
            }
            connection.commit();
            return findById(UUID.fromString(rs.getString(1)));
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

    public Optional<Question> updateVote(UUID questionId, boolean upvote) {
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    """
                       UPDATE questions SET upvotes = upvotes + ?
                       WHERE id = ?::UUID
                       RETURNING id
                       """);
            ps.setInt(1, upvote ? +1 : -1);
            ps.setString(2, questionId.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Vote could not be updated");
            }
            connection.commit();
            return findById(UUID.fromString(rs.getString(1)));
        } catch (SQLException | RuntimeException e) {
            dataSource.rollback(connection);
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }
}
