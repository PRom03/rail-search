package org.example.railsearch.Repositories;

import org.example.railsearch.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
