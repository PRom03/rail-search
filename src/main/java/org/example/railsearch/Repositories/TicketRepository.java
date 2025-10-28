package org.example.railsearch.Repositories;

import org.example.railsearch.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Ticket findById(long id);
}
