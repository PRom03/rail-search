package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.TrainDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;

public interface TrainDateRepository extends JpaRepository<TrainDate, Long> {
    @Query("""
select t.id from TrainDate t where t.train.id=:trainId and t.date=:date
""")
    Integer getIdByTrainIdAndDate(int trainId, LocalDate date);

}
