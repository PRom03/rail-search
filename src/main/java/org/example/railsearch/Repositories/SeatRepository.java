package org.example.railsearch.Repositories;

import jakarta.persistence.LockModeType;
import org.example.railsearch.Entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
   @Query("""
        select s from Seat s where s.trainCar.carNumber=:carNumber and s.trainCar.train.id=:trainId
    """
   )
    List<Seat> findByTrainAndTrainCar(Integer carNumber, Long trainId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.trainCar.id = :carId AND s.number = :number")
    Optional<Seat> findByTrainCarIdAndNumberForUpdate(long carId, int number);
    @Query("""
select s from Seat s where s.trainCar.carNumber=:carId and s.trainCar.train.id=:trainId
and not exists (select ts from TravelStage ts where ts.trainDate.id=:trainDateId and ts.seatTrainDate.seat=s)
""")
    List<Seat> findFreeSeatsByTrainDateId(Integer carId,Integer trainId,long trainDateId);

}
