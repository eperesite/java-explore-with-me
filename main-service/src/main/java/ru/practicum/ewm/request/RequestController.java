package ru.practicum.ewm.request;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.service.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                 @RequestParam(name = "eventId") @Min(0) Long eventId) {
        log.info("Creating request for event with ID {} by user with ID {}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        log.info("Fetching all requests for user with ID {}", userId);
        return requestService.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                 @PathVariable(value = "requestId") @Min(0) Long requestId) {
        log.info("Canceling request with ID {} by user with ID {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}