package org.example.railsearch.Repositories;
import org.example.railsearch.Entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {
    @Query("""
            SELECT  t
                FROM    Train t 
                WHERE EXISTS ( SELECT s1
                                 FROM Stop s1 , Stop s2
                                 WHERE s1.train.id = t.id
                                   AND s1.station.id = :id1
                                   AND s2.station.id = :id2
                                   AND s2.train.id = t.id
                                   AND s1.id<s2.id)         
""")
    List<Train> getTrainsByStations(int id1,int id2);

    @Query("SELECT t from Train t where t.name=:name")
    Train getTrainByName(String name);

}
