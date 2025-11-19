package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface StopRepository extends JpaRepository<Stop, Long> {
    @Query("SELECT s FROM Stop s JOIN FETCH s.train JOIN FETCH s.station WHERE s.id>=(SELECT s.id FROM Stop s where s.station.id=?2 and s.train.id=?1) AND s.id<=(SELECT s.id FROM Stop s where s.station.id=?3 and s.train.id=?1)")
    List<Stop> findStopsByTrain(int id,int stadionFromId,int stadionToId);
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.station.id = :stationId
        AND s.departureTime IS NOT NULL
        AND s.departureTime > :time
      ORDER BY s.departureTime
    """)
    List<Stop> findDeparturesFromStationAfter(@Param("stationId") Integer stationId, @Param("time") LocalTime time);

    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.station.id = :stationId
        AND s.arrivalTime IS NOT NULL
        AND s.arrivalTime > :time
      ORDER BY s.arrivalTime
    """)
    List<Stop> findArrivalsAtStationAfter(@Param("stationId") Integer stationId, @Param("time") LocalTime time);
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.train.id = :trainId
      ORDER BY s.id
    """)
    List<Stop> findStopsByTrainOrdered(@Param("trainId") Integer trainId);
@Query
        (
                """
SELECT s
FROM Stop s
WHERE s.station.id = :destId
  AND s.train.id  = :trainId
 AND s.id<(SELECT s1.id from Stop s1 where s.train.id=:trainId AND s.station.id=:fromId)
  
"""
        )
   Stop isTrainToDestination(@Param("trainId") Integer trainId,@Param("fromId") Integer fromId,@Param("destId") Integer destId);
    @Query("""
select s from Stop s where s.id>:fromId and s.id<:toId and s.train.id=:trainId
""")
    List<Stop> getStopsBetweenIds(Integer fromId, Integer toId,Integer trainId);
}
//CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END