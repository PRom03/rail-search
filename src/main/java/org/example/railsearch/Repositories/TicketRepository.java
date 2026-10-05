package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    @Query("""

            SELECT t from Ticket t where t.extId=:id
            """

    )
    Ticket findByExtId(String id);
    List<Ticket> findByUserId(Long userId);
    @Query(value = "select last_value from ticket_id_seq", nativeQuery = true)
    Long getLastTicketId();
}
