package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Distance;
import org.example.railsearch.Entities.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Query("SELECT s from Station s where s.name=:name")
    Station getStationByName(String name);
    @Query("""
SELECT
    CASE
        WHEN d.station1.name = :name THEN d.station2
        ELSE d.station1
    END
    FROM Distance d
    WHERE d=:distance
""")
    Station getAnotherStationForStation(String name, Distance distance);

    Station findStationByName(String name);
    @Query("select  s from Station s inner join Distance d on d.station1.id=s.id or d.station2.id=s.id group by s.id having count(d)>2")
    List<Station> findJunctionStations();
}
