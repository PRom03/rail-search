package org.example.railsearch.Services;

import org.example.railsearch.Entities.Discount;
import org.example.railsearch.Repositories.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    public List<Discount> findAll() {
        return discountRepository.findAll();
    }
}
