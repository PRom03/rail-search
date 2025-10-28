package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Distance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface DistanceRepository extends JpaRepository<Distance, Long> {
    @Query("""
           SELECT d
           FROM Distance d JOIN FETCH d.station1 JOIN FETCH d.station2
           WHERE ( d.station1.id = :id1 AND d.station2.id = :id2 )
              OR ( d.station1.id = :id2 AND d.station2.id = :id1 )
           """)
    Distance findDistanceBetweenNeighboring(@Param("id1") Integer id1,
                                            @Param("id2") Integer id2);



}
