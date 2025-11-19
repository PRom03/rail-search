package org.example.railsearch.Repositories;

import jakarta.persistence.LockModeType;
import org.example.railsearch.Entities.TrainCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainCarRepository extends JpaRepository<TrainCar, Long> {
    List<TrainCar> findByTrainId(Long trainId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tc FROM TrainCar tc WHERE tc.train.id = :trainId AND tc.number = :number")
    Optional<TrainCar> findByTrainIdAndNumberForUpdate(long trainId, int number);
}
