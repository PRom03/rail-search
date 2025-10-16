package org.example.railsearch.Services;

import org.example.railsearch.Repositories.PriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PriceService {
    private final PriceRepository priceRepository;
    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }
    public BigDecimal getPriceRangeForDistance(Integer distance,Integer transporterId) {
        return priceRepository.getPriceRangeForDistance(distance,transporterId);
    }
}
