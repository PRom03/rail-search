package org.example.railsearch.Services;

import org.example.railsearch.Entities.Discount;
import org.example.railsearch.Repositories.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }
    public List<Discount> findAll() {
        return discountRepository.findAll();
    }
}
