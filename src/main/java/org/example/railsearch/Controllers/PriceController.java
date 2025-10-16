package org.example.railsearch.Controllers;

import org.example.railsearch.Repositories.PriceRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.PriceService;
import org.example.railsearch.Services.TransporterService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/price")
public class PriceController {
    public record PriceResponse(BigDecimal price){};
    private final PriceService priceService;
    private final DistanceService distanceService;
    private final TransporterService transporterService;

    public PriceController(PriceService priceService, DistanceService distanceService, TransporterService transporterService) {
        this.priceService = priceService;
        this.distanceService = distanceService;
        this.transporterService = transporterService;
    }
    @RequestMapping(value="/{stationFrom}/{stationTo}/{trainName}" ,  method = RequestMethod.GET, produces="application/json")
    public PriceResponse getPriceForDistance(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable String trainName) {
        Integer abbrev= transporterService.getTransporterIdByName(trainName.split(" ")[0]);
        System.out.println(abbrev);
        return new PriceResponse(priceService.getPriceRangeForDistance(Math.round(distanceService.getDistanceForTrain(stationFrom, stationTo, trainName)), abbrev));
    }
}
