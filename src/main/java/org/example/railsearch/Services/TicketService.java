package org.example.railsearch.Services;

import org.example.railsearch.Entities.Discount;
import org.example.railsearch.Entities.User;
import org.example.railsearch.Repositories.DiscountRepository;
import org.example.railsearch.Repositories.PaymentRepository;
import org.example.railsearch.Repositories.TicketRepository;
import org.example.railsearch.Repositories.UserRepository;
import org.example.railsearch.Entities.Ticket;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public record TicketCreateDto(
            Long userId,
            Long discountId,
            Boolean hasDog,
            Boolean hasBike,
            Boolean hasLuggage,
            BigDecimal price,
            String status,
            String extId) {}
    public record TicketPurchaseDto(
List<TravelStageCreateDto> connectionIds,
List<SeatDto> seats,
Integer discountId,
Double amount,
List<Long> trainDateIds
    ){}
    public record SeatDto(Integer carNumber,Integer seatNumber){}
    public record updateTicketRequest(String status,long paymentId) {}
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
        ticket.setExtId(dto.extId());
        return ticketRepository.save(ticket);
    }
    public Ticket findById(long id) {
        return ticketRepository.findById(id).orElse(null);
    }
    public List<Ticket> findByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
    public Ticket update(String ticketId,String status,Long paymentId){
        Ticket ticket=ticketRepository.findByExtId(ticketId);
        ticket.setStatus(status);
        if(paymentId!=null) ticket.setPayment(paymentRepository.findById(paymentId).orElse(null));
        return ticketRepository.save(ticket);
    }
    public Long getLastId(){
        return ticketRepository.getLastTicketId();
    }
}
