package org.example.railsearch.Controllers;

import org.example.railsearch.Services.TicketService;
import org.example.railsearch.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @PostMapping("/")
    public Ticket create(@RequestBody TicketService.TicketCreateDto ticket) {
        return ticketService.save(ticket);
    }
}
