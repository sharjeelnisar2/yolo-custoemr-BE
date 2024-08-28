package com.yolo.customer.idea.ideaStatus;

import com.yolo.customer.idea.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdeaStatusRepository extends JpaRepository<IdeaStatus, Long> {
    Optional<IdeaStatus> findByValue(String value);

}
