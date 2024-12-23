package ru.practicum.ewm.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.service.ParticipationRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .requestId(request.getRequestId())
                .eventId(request.getEvent().getId())
                .createdAt(request.getCreatedAt())
                .requesterId(request.getRequester().getUserId())
                .status(request.getStatus())
                .build();
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .requestId(participationRequestDto.getRequestId())
                .createdAt(participationRequestDto.getCreatedAt())
                .status(participationRequestDto.getStatus())
                .build();
    }
}