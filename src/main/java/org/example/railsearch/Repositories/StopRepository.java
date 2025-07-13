package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StopRepository extends JpaRepository<Stop, Long> {
    @Query("SELECT s FROM Stop s JOIN FETCH s.train JOIN FETCH s.station WHERE s.id>=(SELECT s.id FROM Stop s where s.station.id=?2 and s.train.id=?1) AND s.id<=(SELECT s.id FROM Stop s where s.station.id=?3 and s.train.id=?1)")
    public List<Stop> findStopsByTrain(int id,int stadionFromId,int stadionToId);
    @Query("SELECT s FROM Stop s JOIN FETCH s.train WHERE s.train.id=:id")
    public List<Stop> findStopsByTrain(int id);
}
