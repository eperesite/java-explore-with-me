package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequestIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    Optional<Request> findByRequestIdAndRequesterId(Long requestId, Long requesterId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<List<Request>> findByEventIdAndRequestIdIn(Long eventId, List<Long> requestIds);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);
}