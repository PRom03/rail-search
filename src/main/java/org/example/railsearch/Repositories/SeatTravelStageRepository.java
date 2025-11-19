package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.SeatTrainDate;
import org.example.railsearch.Entities.SeatTravelStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatTravelStageRepository extends JpaRepository<SeatTravelStage, Long> {
    @Query(
            """
select s from SeatTravelStage s where s.travelStage.id=:id
"""
    )
    List<SeatTrainDateRepository> findByTravelStageId(Long id);
    @Query("""
select count(s)>0 from SeatTrainDate s where s.trainDate.id=:trainDateId and s.seat.id=:seatId
""")
    boolean existsBySeatIdAndTrainDateId(long seatId, long trainDateId);

}
