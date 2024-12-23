package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    List<Request> findRequestByIdIn(final List<Long> requestsId);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<List<Request>> findByEventIdAndIdIn(Long eventId, List<Long> id);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);
}