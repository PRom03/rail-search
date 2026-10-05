package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface PriceRepository extends JpaRepository<Price, Long> {
    @Query("""
            SELECT p.price from Price p where :distance>=p.kmFloor AND :distance<=p.kmCeil AND p.transporter.id=:transporterId
            """)
    BigDecimal getPriceRangeForDistance(Integer distance, Integer transporterId);
}
