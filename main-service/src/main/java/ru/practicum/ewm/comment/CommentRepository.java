package ru.practicum.ewm.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent_Id(Long eventId, Pageable pageable);

    List<Comment> findByAuthor_Id(Long userId);

    Optional<Comment> findByAuthor_IdAndId(Long userId, Long id);

    @Query("SELECT NEW ru.practicum.ewm.comment.CountCommentsByEventDto(c.event.id, COUNT(c)) " +
            "FROM comments as c where c.event.id in ?1 " +
            "GROUP BY c.event.id")
    List<CommentCountByEventDto> countCommentsByEventIds(List<Long> eventIds);

    @Query("SELECT c " +
            "FROM comments as c " +
            "WHERE LOWER(c.text) LIKE LOWER(CONCAT('%', ?1, '%') )")
    List<Comment> searchByContent(String text, Pageable pageable);
}