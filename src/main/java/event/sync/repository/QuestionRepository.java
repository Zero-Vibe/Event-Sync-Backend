package event.sync.repository;

import event.sync.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {
    List<Question> getQuestionsBySession_Id(UUID sessionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Question SET upvotes = upvotes + CASE WHEN :upvote = true THEN 1 ELSE -1 END WHERE id = :questionId")
    int updateVote(@Param("questionId") UUID questionId, @Param("upvote") boolean upvote);

    Page<Question> findAllBySessionId(UUID sessionId, Pageable pageable);
}
