package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.TravelStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelStageRepository extends JpaRepository<TravelStage,Long> {
    @Query(
            """
SELECT ts from TravelStage ts where ts.ticket.id = :id
"""
    )
    List<TravelStage> findTravelStagesByTicketId(Integer id);
    @Query("""
SELECT count(ts)>0 from TravelStage ts where ts.seatTrainDate.id = :seatTrainDate_id
 
""")
    boolean existsBySeatTrainDateId(Integer seatTrainDate_id,TravelStage stage);
}
