package org.example.railsearch.Services;

import org.example.railsearch.Entities.User;
import org.example.railsearch.Entities.UserRole;
import org.example.railsearch.RegisterException;
import org.example.railsearch.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    public record GetCardTokenRequest(Integer userId){}

    public record SaveCardTokenRequest(String cardToken){}
    public String getCardToken(Long userId){
        return userRepository.findById(userId).get().getCardToken();
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    public User createUser(User user) {
        if(getUserByEmail(user.getEmail()).isPresent()){
            throw new RegisterException("Użytkownik o podanym adresie email już istnieje.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserRole role;
        if(userRepository.count()==0){
            role= UserRole.admin;
        }
        else role=UserRole.client;
        user.setRole(role);
        return userRepository.save(user);
    }
    public User setCardToken(Integer userId,String cardToken) {
        User user=userRepository.findById(Long.valueOf(userId)).orElse(null);
        assert user != null;
        user.setCardToken(cardToken);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        if(getUserByEmail(userDetails.getEmail()).isPresent()){
            throw new RuntimeException("Użytkownik o podanym adresie email już istnieje.");
        }
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstname(userDetails.getFirstname());
                    user.setLastname(userDetails.getLastname());
                    user.setEmail(userDetails.getEmail());
                    user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    user.setDiscount(userDetails.getDiscount());

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
