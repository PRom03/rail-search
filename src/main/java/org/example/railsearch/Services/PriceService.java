package org.example.railsearch.Services;

import org.example.railsearch.Repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;
    public BigDecimal getPriceRangeForDistance(Integer distance,Integer transporterId) {
        return priceRepository.getPriceRangeForDistance(distance,transporterId);
    }
}
