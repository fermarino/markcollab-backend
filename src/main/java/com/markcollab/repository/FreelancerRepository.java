package com.markcollab.repository;

import com.markcollab.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    List<Freelancer> findByNameContainingIgnoreCase(String name);
    List<Freelancer> findByUsernameContainingIgnoreCase(String username);
}
