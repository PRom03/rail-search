package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public interface StopRepository extends JpaRepository<Stop, Long> {
    @Query("SELECT s FROM Stop s JOIN FETCH s.train JOIN FETCH s.station WHERE s.id>=(SELECT s.id FROM Stop s where s.station.id=?2 and s.train.id=?1) AND s.id<=(SELECT s.id FROM Stop s where s.station.id=?3 and s.train.id=?1)")
    public List<Stop> findStopsByTrain(int id,int stadionFromId,int stadionToId);
    @Query("SELECT s FROM Stop s JOIN FETCH s.train WHERE s.train.id=:id")
    public List<Stop> findStopsByTrain(int id);



    // Możliwe przesiadki na tej samej stacji do innego pociągu
    @Query("""
        SELECT s2
        FROM Stop s1
        JOIN Stop s2 ON s1.station = s2.station
        WHERE s1.id = :stopId
          AND s2.train <> s1.train
        """)
    List<Stop> findTransfersAtSameStation(
            @Param("stopId") Integer stopId
    );

    @Query("""
    SELECT s2
    FROM Stop s1
    JOIN Stop s2 ON s1.train = s2.train
    WHERE s1.station.id = :startStationId
      AND s1.departureTime >= :startTime
      AND s2.station.id = :endStationId
      AND s2.id > s1.id
    ORDER BY s2.arrivalTime
    """)
    List<Stop> findDirectConnections(
            @Param("startStationId") Integer startStationId,
            @Param("endStationId") Integer endStationId,
            @Param("startTime") LocalTime startTime
    );
    // tylko odjazdy z danej stacji, których departureTime >= :time
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.station.id = :stationId
        AND s.departureTime IS NOT NULL
        AND s.departureTime >= :time
      ORDER BY s.departureTime
    """)
    List<Stop> findTransfersAtStationAfterTime(@Param("stationId") Integer stationId, @Param("time") LocalTime time);
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.station.id = :stationId
        AND s.departureTime IS NOT NULL
        AND s.departureTime >= :time
      ORDER BY s.departureTime
    """)
    List<Stop> findDeparturesFromStationAfter(@Param("stationId") Integer stationId, @Param("time") LocalTime time);

    // jak wyżej, ale z limitem (Postgres nativeQuery potrzebny do LIMIT w JPQL)
    @Query("""
       SELECT s
       FROM Stop s
       WHERE s.station.id = :stationId
         AND s.departureTime IS NOT NULL
         AND s.departureTime >= :time
       ORDER BY s.departureTime
       """)
    List<Stop> findDeparturesFromStationAfterLimited(@Param("stationId") Integer stationId,
                                                     @Param("time") LocalTime time);

    // wszystkie stop-y danego pociągu w kolejności (musisz mieć porządek np. id rosnące lub stop_sequence)
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.train.id = :trainId
      ORDER BY s.id
    """)
    List<Stop> findStopsByTrainOrdered(@Param("trainId") Integer trainId);

    // stops należące do danego pociągu po danym stopie (używane, jeżeli chcesz tylko dalsze stop-y)
    @Query("""
      SELECT s2
      FROM Stop s1
      JOIN Stop s2 ON s1.train = s2.train
      WHERE s1.id = :stopId
        AND s2.id > s1.id
      ORDER BY s2.id
    """)
    List<Stop> findNextStopsInSameTrain(@Param("stopId") Integer stopId);

    // (opcjonalnie) wszystkie stopy danego pociągu (może być użyteczne do wstępnego cache)
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.train.id = :trainId
      ORDER BY s.id
    """)
    List<Stop> findAllStopsForTrain(@Param("trainId") Integer trainId);

    // stops at given station arriving (we may need for backward search) - example
    @Query("""
      SELECT s
      FROM Stop s
      WHERE s.station.id = :stationId
        AND s.arrivalTime IS NOT NULL
        AND s.arrivalTime <= :time
      ORDER BY s.arrivalTime DESC
    """)
    List<Stop> findArrivalsAtStationBefore(@Param("stationId") Integer stationId, @Param("time") LocalTime time);
}
