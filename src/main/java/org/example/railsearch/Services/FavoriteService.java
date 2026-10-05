package org.example.railsearch.Services;

import org.example.railsearch.Entities.Favorite;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Repositories.FavoriteRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StationRepository stationRepository;

    public List<Favorite> findByUser_Id(Integer userId) {
        return favoriteRepository.findByUser_Id(userId);
    }
    public Favorite add(FavoriteCreateDto favorite,Integer userId) {
        Favorite favoriteToAdd = new Favorite();
        favoriteToAdd.setUser(userRepository.findById(Long.valueOf(userId)).orElse(null));
        favoriteToAdd.setStationFrom(stationRepository.findStationByName(favorite.stationFrom()));
        favoriteToAdd.setStationTo(stationRepository.findStationByName(favorite.stationTo()));
        return favoriteRepository.save(favoriteToAdd);
    }
    public record FavoriteCreateDto(String stationFrom, String stationTo) {}
    public void delete(Integer id) {
        favoriteRepository.delete(favoriteRepository.findById(Long.valueOf(id)).orElse(null));
    }
}
