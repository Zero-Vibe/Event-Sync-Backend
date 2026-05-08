package event.sync.repository;

import event.sync.datasource.DataSourceConfig;
import event.sync.model.Question;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class QuestionRepository {
    private DataSourceConfig dataSource;

    public List<Question> getSessionQuestions(UUID sessionId) {
        Connection connection = dataSource.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
            """
               SELECT id, session_id, content, author_name, upvotes, created_at
               FROM questions WHERE session_id = ?::UUID
               """);
            ps.setObject(1, sessionId);
            ResultSet rs = ps.executeQuery();
            List<Question> questions = new ArrayList<>();
            while (rs.next()) {
                questions.add(
                        Question.builder()
                        .id(UUID.fromString(rs.getString(1)))
                        .sessionId(UUID.fromString(rs.getString(2)))
                        .content(rs.getString(3))
                        .authorName(rs.getString(4))
                        .upvotes(rs.getInt(5))
                        .createdAt(rs.getTimestamp(6).toLocalDateTime())
                        .build()
                );
            }
            return questions;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            dataSource.closeConnection(connection);
        }
    }

}
