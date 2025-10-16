package org.example.railsearch.Controllers;


import org.example.railsearch.Entities.Discount;
import org.example.railsearch.Services.DiscountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    private final DiscountService discountService;
    public DiscountController(DiscountService discountService) {

this.discountService = discountService;    }
    public record DiscountResponse(String name, BigDecimal value) {}
    @GetMapping("/")
    public List<DiscountResponse> getDiscounts() {
        List<Discount> discounts = discountService.findAll();
        List<DiscountResponse> discountResponses = new ArrayList<>();
        for (int i = 0; i < discounts.size(); i++) {
            discountResponses.add(new DiscountResponse(discounts.get(i).getName(), discounts.get(i).getValue()));
        }
        return discountResponses;
    }
}
