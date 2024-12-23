package ru.practicum.ewm.location;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<ru.practicum.ewm.location.Location, Long> {
}