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


    Station findStationByName(String name);
    @Query("""
SELECT DISTINCT s from Station s JOIN Distance d on d.station1.id=s.id or d.station2.id=s.id where ((d.station1.id=(SELECT s.id from Station s where s.id=:id) ) or (d.station2.id=(SELECT s.id from Station s where s.id=:id))) AND s.id!=:id""")
    List<Station> findNeighboringStations(Integer id);

    @Query("SELECT s from Station s where s.name like '%' || :query || '%'")
    List<Station> getSuggestedStations(String query);
}
