package org.example.railsearch.Services;

import org.example.railsearch.Repositories.TransporterRepository;
import org.springframework.stereotype.Service;

@Service
public class TransporterService {
    private final TransporterRepository transporterRepository;

    public TransporterService(TransporterRepository transporterRepository) {
        this.transporterRepository = transporterRepository;
    }
    public Integer getTransporterIdByName(String name) {
        return transporterRepository.getTransporterIdByName(name);
    }
}
