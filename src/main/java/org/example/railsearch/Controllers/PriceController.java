package org.example.railsearch.Controllers;


import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.PriceService;
import org.example.railsearch.Services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/price")
public class PriceController {
    public record PriceResponse(BigDecimal price){}
    @Autowired
    private PriceService priceService;
    @Autowired
    private TransporterService transporterService;
    @Autowired
    private DistanceService distanceService;

//    
    @RequestMapping(value="/{distance}/{trainName}/{is1stClass}" ,  method = RequestMethod.GET, produces="application/json")
    public PriceResponse getPriceForDistance(@PathVariable Integer distance,@PathVariable String trainName,@PathVariable Integer is1stClass) {
        Integer abbrev= transporterService.getTransporterIdByName(trainName.split(" ")[0]);
        System.out.println(abbrev);
        if(is1stClass==1) abbrev=3;
        return new PriceResponse(priceService.getPriceRangeForDistance(distance, abbrev));
    }
}
