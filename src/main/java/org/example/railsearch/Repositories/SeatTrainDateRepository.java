package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.SeatTrainDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatTrainDateRepository extends JpaRepository<SeatTrainDate, Long> {
    @Query("""
select count(s)>0 from SeatTrainDate s where s.trainDate.id=:trainDateId
""")
    boolean hasSeats(Integer trainDateId);

    @Query("""
select s from SeatTrainDate s where s.trainDate.id=:trainDateId and s.seat.id=:seatId
""")
    Optional<SeatTrainDate> findBySeatIdAndTrainDateId(Integer seatId, long trainDateId);
}
