package org.example.railsearch.Services;

import org.example.railsearch.Repositories.SeatTrainDateRepository;
import org.example.railsearch.Repositories.SeatTravelStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SeatTrainDateService {
    @Autowired
    private SeatTrainDateRepository seatTrainDateRepository;
    public boolean hasSeats(Integer id) {
        return seatTrainDateRepository.hasSeats(id);
    }
}
