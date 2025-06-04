package com.markcollab.repository;

import com.markcollab.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByProject_ProjectId(Long projectId);
}
