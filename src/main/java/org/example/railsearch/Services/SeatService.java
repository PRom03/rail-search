package org.example.railsearch.Services;

import org.example.railsearch.Repositories.SeatRepository;
import org.example.railsearch.Entities.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    public List<Seat> findByTrainAndTrainCar(Integer carNumber,Long trainId) {
        return seatRepository.findByTrainAndTrainCar(carNumber, trainId);
    }
}
