package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Services.StopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stops")
public class StopController {
    @Autowired
    private StopService stopService;
    @GetMapping(value = "/{fromId}/{toId}/{trainId}",produces = "application/json")
    public List<Stop> getStopsBetweenIds(@PathVariable Integer fromId, @PathVariable Integer toId,@PathVariable Integer trainId) {
        return stopService.getStopsBetweenIds(fromId, toId,trainId);
    }
}
