package org.example.railsearch.Services;

import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.TicketRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.example.railsearch.Repositories.TravelStageRepository;
import org.example.railsearch.TravelStage;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
    private StationRepository stationRepository;
    public List<TravelStage> saveAll(List<TravelStageCreateDto> dtos) {
        List<TravelStage> travelStages = new ArrayList<>();
        for (var dto : dtos) {
            TravelStage stage = new TravelStage();
            stage.setTicket(ticketRepository.findById(dto.ticketId()));
            stage.setStationFrom(stationRepository.findById(dto.stationFromId()));
            stage.setStationTo(stationRepository.findById(dto.stationToId()));
            stage.setTrain(trainRepository.findById(dto.trainId()));
            stage.setDistance(dto.distance());
            travelStages.add(travelStageRepository.save(stage));
        }
        return travelStages;
    }
}
