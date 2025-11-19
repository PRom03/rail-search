package org.example.railsearch.Controllers;

import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.TicketService;
import org.example.railsearch.Services.UserService;
import org.example.railsearch.Entities.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @PostMapping("/")
    public Ticket create(@RequestBody TicketService.TicketCreateDto ticket) {
    return ticketService.save(ticket);
    }
    @PostMapping("/buy")
    public Ticket buy(@RequestBody TicketService.TicketPurchaseDto body) {
        Ticket ticket = new Ticket();

        return ticket;
    }
    @GetMapping("/my")
    public List<Ticket> getMyTickets(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return List.of();
        }
        token=token.replace("Bearer ", "");
        return ticketService.findByUserId(Long.valueOf(
                userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null).getId())
        );
    }
    @GetMapping(value = "/{id}",produces = "application/json")
    public Ticket getTicketById(@RequestHeader("Authorization") String token,@PathVariable Integer id) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        return ticketService.findById(id);
    }
    @PostMapping("/update/{ticketId}")
    public Ticket update(@RequestHeader("Authorization") String token,@PathVariable String ticketId,@RequestBody TicketService.updateTicketRequest body){
        if(token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return ticketService.update(ticketId,body.status(),body.paymentId());
    }
    public record LastIdResponse(String ticketId){}
    @GetMapping(value = "/last",produces = "application/json")
    public LastIdResponse getLastTicketId(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new LastIdResponse(String.valueOf(0L));
        }
        return new LastIdResponse(String.valueOf(ticketService.getLastId()));
    }
}
