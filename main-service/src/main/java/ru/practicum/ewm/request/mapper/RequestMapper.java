package ru.practicum.ewm.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        if (request == null) {
            return null;
        }
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getId())
                .status(request.getStatus())
                .build();
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return Request.builder()
                .id(participationRequestDto.getId())
                .event(null)
                .created(participationRequestDto.getCreated())
                .requester(null)
                .status(participationRequestDto.getStatus())
                .build();
    }
}