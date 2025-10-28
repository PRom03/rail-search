package org.example.railsearch.Services;

import org.example.railsearch.Entities.Discount;
import org.example.railsearch.Entities.User;
import org.example.railsearch.Repositories.DiscountRepository;
import org.example.railsearch.Repositories.TicketRepository;
import org.example.railsearch.Repositories.UserRepository;
import org.example.railsearch.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DiscountRepository discountRepository;
    public record TicketCreateDto(
            Long userId,
            Long discountId,
            Boolean hasDog,
            Boolean hasBike,
            Boolean hasLuggage,
            BigDecimal price,
            String status
    ) {}

    public Ticket save(TicketCreateDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setUser(user);

        if (dto.discountId() != null) {
            Discount discount = discountRepository.findById(dto.discountId())
                    .orElseThrow(() -> new RuntimeException("Discount not found"));
            ticket.setDiscount(discount);
        }

        ticket.setHasDog(dto.hasDog());
        ticket.setHasBike(dto.hasBike());
        ticket.setHasLuggage(dto.hasLuggage());
        ticket.setPrice(dto.price());
        ticket.setStatus(dto.status());

        return ticketRepository.save(ticket);
    }
    public Ticket findById(long id) {
        return ticketRepository.findById(id);
    }
}
