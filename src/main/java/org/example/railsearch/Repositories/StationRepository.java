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
    @Query("""
SELECT DISTINCT s from Station s JOIN Distance d on d.station1.id=s.id or d.station2.id=s.id where ((d.station1.id=(SELECT s.id from Station s where s.id=:id) ) or (d.station2.id=(SELECT s.id from Station s where s.id=:id))) AND s.id!=:id""")
    List<Station> findNeighboringStations(Integer id);
    @Query("""
        SELECT s.station.id from Stop s where s.train.id=:id
""")
    List<Integer> findStationsInStopsForTrain(int id);
    @Query("SELECT s from Station s where s.name like '%' || :query || '%'")
    List<Station> getSuggestedStations(String query);
}
