package org.example.railsearch;

import java.math.BigDecimal;

public record TravelStageCreateDto(
        Long stationFromId,
        Long stationToId,
        Long ticketId,
        Long trainId,
        BigDecimal distance
) {};