package com.tlfdt.bonrecreme.repository.user;

import com.tlfdt.bonrecreme.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}