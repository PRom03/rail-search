package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransporterRepository extends JpaRepository<Transporter,Long> {
    @Query("""
SELECT t.id from Transporter t where t.name=:name
""")
    Integer getTransporterIdByName(String name);
}
