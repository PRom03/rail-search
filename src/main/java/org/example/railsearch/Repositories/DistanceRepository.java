package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Distance;
import org.hibernate.graph.*;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.PersistentAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface DistanceRepository extends JpaRepository<Distance, Long> {
    @Query("""
           SELECT d
           FROM Distance d
           WHERE ( d.station1.id = :id1 AND d.station2.id = :id2 )
              OR ( d.station1.id = :id2 AND d.station2.id = :id1 )
           """)
    Distance findDistanceBetweenNeighboring(@Param("id1") Integer id1,
                                            @Param("id2") Integer id2);
    @Query("""
    SELECT d FROM Distance d 
       
                WHERE d.id>=(
        SELECT MIN(d1.id) FROM Distance d1 
        WHERE d1.station1.name=:name1 OR d1.station2.name=:name1
        ) 
        AND d.id<=
        (SELECT MAX(d2.id) FROM Distance d2
        WHERE d2.station1.name=:name2 OR d2.station2.name=:name2)
    """)
    List<Distance> findDistancesBetweenStations(@Param("name1") String name1,@Param("name2")String name2);
    @Query("SELECT d from Distance d where d.station1.name=:name or d.station2.name=:name")
    List<Distance> findDistancesByStationName(String name);

    @Query("SELECT d FROM Distance d   WHERE ((d.station1.name=:name1 AND d.station2.name!=:name2) or (d.station1.name!=:name2 and d.station2.name=:name1))  ")
    Distance findNextDistance(String name1,String name2);

}
