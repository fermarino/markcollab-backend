package com.markcollab.repository;

import com.markcollab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCpf(String cpf);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
