package org.example.railsearch.Controllers;


import org.checkerframework.checker.units.qual.A;
import org.example.railsearch.Entities.Discount;
import org.example.railsearch.Services.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @GetMapping(value = "/",produces = "application/json")
    public List<Discount> getDiscounts() {
        return discountService.findAll();

    }
}
