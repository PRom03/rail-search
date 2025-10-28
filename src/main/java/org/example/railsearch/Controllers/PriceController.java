package org.example.railsearch.Controllers;

import org.example.railsearch.Repositories.PriceRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.PriceService;
import org.example.railsearch.Services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/price")
public class PriceController {
    public record PriceResponse(BigDecimal price){};
    @Autowired
    private PriceService priceService;
    @Autowired
    private DistanceService distanceService;
    @Autowired
    private TransporterService transporterService;


    @RequestMapping(value="/{stationFrom}/{stationTo}/{trainName}" ,  method = RequestMethod.GET, produces="application/json")
    public PriceResponse getPriceForDistance(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable String trainName) {
        Integer abbrev= transporterService.getTransporterIdByName(trainName.split(" ")[0]);
        System.out.println(abbrev);
        return new PriceResponse(priceService.getPriceRangeForDistance(Math.round(distanceService.getDistanceForTrain(stationFrom, stationTo, trainName)), abbrev));
    }
}
