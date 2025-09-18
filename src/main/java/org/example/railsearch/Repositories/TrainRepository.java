package org.example.railsearch.Repositories;
import org.example.railsearch.Entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {
    // You can define custom query methods here if needed
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
//    @Query("""
//SELECT t FROM Train t WHERE EXISTS (
//SELECT s1,s2 FROM Stop s1 , Stop s2 WHERE s1.train.id = t.id AND (s1.station.id = :id1 OR s2.station.id = :id2) AND s2.train.id = t.id)
//""")
//    List<Train> getPreliminaryTrains(int id1,int id2);
//    @Query("""
//   WITH trains AS (
//   SELECT t1.id AS train1,
//             t2.id AS train2
//      FROM Train t1
//      JOIN Stop s1 ON s1.train.id = t1.id
//      JOIN Train t2 ON 1=1
//      JOIN Stop s2 ON s2.train.id = t2.id
//
//      WHERE s1.station.id = :id1
//        AND s2.station.id = :id2
//        AND s1.departureTime < s2.departureTime
//        )
//        SELECT ct.train1, ct.train2, st.station.id
//        FROM trains ct
//        JOIN Stop st ON st.train.id = ct.train1
//        WHERE st.station.id IN (
//            SELECT s.station.id
//            FROM Stop s
//            WHERE s.train.id = ct.train2
//        )
//
//""")
//    List<List<Integer>> getTrainPairsByStations(int id1,int id2);
    @Query("SELECT t from Train t where t.name=:name")
    Train getTrainByName(String name);
}
