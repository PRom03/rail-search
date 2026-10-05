package org.example.railsearch.Services;

import org.example.railsearch.Entities.*;
import org.example.railsearch.Repositories.*;
import org.example.railsearch.SeatReservationException;
import org.example.railsearch.TicketCreationException;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public List<TravelStage> saveAll(List<TravelStageCreateDto> dtos,Integer userId) {
        List<TravelStage> travelStages = new ArrayList<>();
        for (var dto : dtos) {
            TrainDate trainDate=trainDateRepository.findById(dto.trainDateId()).orElse(null);
            if(travelStageRepository.findByUserAndTrainDateId(userId,trainDate.getId())!=null) throw new TicketCreationException("Użytkownik kupił już bilet na ten pociąg.");

            TravelStage stage = new TravelStage();
            stage.setTicket(ticketRepository.findById(dto.ticketId()));
            stage.setStopFrom(stopRepository.findById(dto.stopFromId()));
            stage.setStopTo(stopRepository.findById(dto.stopToId()));
            stage.setTrainDate(Optional.of(trainDate));
            stage.setDistance(dto.distance());
            travelStages.add(travelStageRepository.save(stage));
        }
        LocalTime departureTime = travelStages.get(0)
                .getStopFrom()
                .getDepartureTime();

        LocalTime now = LocalTime.now();

       if(Duration.between(now, departureTime).toMinutes() < 5 && departureTime.isAfter(now)) throw new RuntimeException("Czas na zakup minął.");
       return travelStages;
    }
    public List<TravelStage> findTravelStagesByTicketId(Integer id) {
        return travelStageRepository.findTravelStagesByTicketId(id);
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TravelStage reserveSeat(
            long trainDateId, int carNumber, int seatNumber, Integer travelStageId
    ) {
        TrainDate trainDate = trainDateRepository.findById(trainDateId)
                .orElseThrow(() -> new SeatReservationException(
                        "Pociąg nie kursuje wybranego dnia."
                ));
        TrainCar car = trainCarRepository
                .findByTrainIdAndNumberForUpdate(
                        trainDate.getTrain().getId(), carNumber)
                .orElseThrow(() ->
                        new SeatReservationException("Wagon nie istnieje.")
                );
        Seat seat = seatRepository
                .findByTrainCarIdAndNumberForUpdate(car.getId(), seatNumber)
                .orElseThrow(() ->
                        new SeatReservationException("Miejsce nie istnieje.")
                );
        SeatTrainDate seatTrainDate = seatTrainDateRepository
                .findBySeatIdAndTrainDateId(seat.getId(), trainDateId)
                .orElseGet(() -> {
                    SeatTrainDate std = new SeatTrainDate();
                    std.setSeat(seat);
                    std.setTrainDate(trainDate);
                    return seatTrainDateRepository.save(std);
                });
        TravelStage reservation = travelStageRepository.findById(
                Long.valueOf(travelStageId)).orElse(null);
        boolean occupied = travelStageRepository.existsBySeatTrainDateId(
                seatTrainDate.getId(),reservation);
        if (occupied){
            throw new SeatReservationException("Miejsce zajęte w pociągu "
                    +trainDate.getTrain().getName()+".");
        }
        assert Objects.requireNonNull(reservation).getTicket() != null;
        boolean hasUserAlreadyReservedASeat=travelStageRepository.
                hasUserAlreadyReservedASeat(
                        reservation.getTicket().getUser().getId(),
                        Integer.parseInt(String.valueOf(trainDateId)),
                        reservation.getTicket().getId());
        if(hasUserAlreadyReservedASeat) {
            throw new SeatReservationException
                    ("Użytkownik zarezerwował już miejsce w tym pociągu.");}
        reservation.setSeatTrainDate(seatTrainDate);
        return travelStageRepository.save(reservation);
    }
}
