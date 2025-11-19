package org.example.railsearch.Services;

import org.example.railsearch.Entities.*;
import org.example.railsearch.Repositories.*;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TravelStageService {
    @Autowired
    private TravelStageRepository travelStageRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private TrainDateRepository trainDateRepository;
    @Autowired
    private TrainCarRepository  trainCarRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatTrainDateRepository seatTrainDateRepository;
    public List<TravelStage> saveAll(List<TravelStageCreateDto> dtos) {
        List<TravelStage> travelStages = new ArrayList<>();
        for (var dto : dtos) {
            TravelStage stage = new TravelStage();
            stage.setTicket(ticketRepository.findById(dto.ticketId()));
            stage.setStopFrom(stopRepository.findById(dto.stopFromId()));
            stage.setStopTo(stopRepository.findById(dto.stopToId()));
            stage.setTrainDate(trainDateRepository.findById(dto.trainDateId()));
            stage.setDistance(dto.distance());
            travelStages.add(travelStageRepository.save(stage));
        }
        return travelStages;
    }
    public List<TravelStage> findTravelStagesByTicketId(Integer id) {
        return travelStageRepository.findTravelStagesByTicketId(id);
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TravelStage reserveSeat(long trainDateId, int carNumber, int seatNumber, Integer travelStageId) {


        TrainDate trainDate = trainDateRepository.findById(trainDateId)
                .orElseThrow(() -> new IllegalArgumentException("Train date not found"));

        TrainCar car = trainCarRepository
                .findByTrainIdAndNumberForUpdate(trainDate.getTrain().getId(), carNumber)
                .orElseThrow(() -> new IllegalArgumentException("Train car not found"));

        Seat seat = seatRepository
                .findByTrainCarIdAndNumberForUpdate(car.getId(), seatNumber)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found"));

        SeatTrainDate seatTrainDate = seatTrainDateRepository
                .findBySeatIdAndTrainDateId(seat.getId(), trainDateId)
                .orElseGet(() -> {
                    SeatTrainDate std = new SeatTrainDate();
                    std.setSeat(seat);
                    std.setTrainDate(trainDate);
                    return seatTrainDateRepository.save(std);
                });
        TravelStage reservation = travelStageRepository.findById(Long.valueOf(travelStageId)).orElse(null);

        boolean occupied = travelStageRepository.existsBySeatTrainDateId(seatTrainDate.getId(),reservation);
        if (occupied) {
            throw new IllegalStateException("Seat already reserved");
        }


        reservation.setSeatTrainDate(seatTrainDate);

        return travelStageRepository.save(reservation);
    }
}
