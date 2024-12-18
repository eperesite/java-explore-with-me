package ru.practicum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatisticsService;
import ru.practicum.StatInDto;
import ru.practicum.StatOutDto;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;
    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping(value = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postHit(@Valid @RequestBody StatInDto statInDto) {
        statisticsService.create(statInDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatOutDto> get(
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) @Valid @NotNull LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) @Valid @NotNull LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return statisticsService.get(start, end, uris, unique);
    }
}