package org.example.railsearch.Services;

import org.example.railsearch.Repositories.TransporterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransporterService {
    @Autowired
    private TransporterRepository transporterRepository;


    public Integer getTransporterIdByName(String name) {
        return transporterRepository.getTransporterIdByName(name);
    }
}
