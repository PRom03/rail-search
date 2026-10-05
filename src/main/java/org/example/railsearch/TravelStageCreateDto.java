package org.example.railsearch;

import java.math.BigDecimal;

public record TravelStageCreateDto(
        Long stopFromId,
        Long stopToId,
        Long ticketId,
        Long trainDateId,
        BigDecimal distance
) {};