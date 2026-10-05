package org.example.railsearch.Services;

import org.example.railsearch.Entities.*;
import org.example.railsearch.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatTravelStageService {
    @Autowired
    private SeatTravelStageRepository seatTravelStageRepository;
    @Autowired
    private TrainDateRepository trainDateRepository;
    @Autowired
    private TrainCarRepository trainCarRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatTrainDateRepository seatTrainDateRepository;
    @Autowired
    private TravelStageRepository travelStageRepository;

    public List<SeatTrainDateRepository> findByTravelStageId(Long id) {
        return seatTravelStageRepository.findByTravelStageId(id);
    }


}
