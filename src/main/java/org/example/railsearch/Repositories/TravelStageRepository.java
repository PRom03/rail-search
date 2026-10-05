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
SELECT count(ts)>0 from TravelStage ts where ts.seatTrainDate.id = :seatTrainDate_id and ts.ticket.status="bought"
""")
    boolean existsBySeatTrainDateId(Integer seatTrainDate_id,TravelStage stage);
    @Query("""
SELECT count(ts)>0 from TravelStage ts where ts.ticket.id=:ticketId and ts.ticket.user.id=:userId and ts.seatTrainDate IS NOT NULL and ts.trainDate.id=:trainDateId
""")
    boolean hasUserAlreadyReservedASeat(Integer userId,Integer trainDateId,Integer ticketId);
@Query("""
select ts from TravelStage ts where ts.ticket.user.id=:userId and ts.trainDate.id = :trainDateId
""")
    TravelStage findByUserAndTrainDateId(Integer userId,Integer trainDateId);
}
