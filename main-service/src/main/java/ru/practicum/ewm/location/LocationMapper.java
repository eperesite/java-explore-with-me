package ru.practicum.ewm.location;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static ru.practicum.ewm.location.Location toLocation(ru.practicum.ewm.location.Location.LocationDto locationDto) {
        return ru.practicum.ewm.location.Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }


    public  static ru.practicum.ewm.location.Location.LocationDto toLocationDto(ru.practicum.ewm.location.Location location) {
        return ru.practicum.ewm.location.Location.LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}