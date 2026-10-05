package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_Id(Integer userId);
}
