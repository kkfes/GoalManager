package org.example.goalmanager.repository;

import org.example.goalmanager.entity.Goal;
import org.example.goalmanager.entity.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByUserId(Long userId);

    List<Goal> findAllByUserIdAndStatus(Long userId, GoalStatus status);

    List<Goal> findAllByUserIdAndTargetDateBetween(Long userId, LocalDateTime from, LocalDateTime to);

    List<Goal> findAllByParentGoalId(Long parentGoalId);

    Page<Goal> findAllByUserId(Long userId, Pageable pageable);

    List<Goal> findAllByTargetDateBetween(LocalDateTime from, LocalDateTime to);

    long countByStatus(GoalStatus status);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByCompletedAtBetween(LocalDateTime from, LocalDateTime to);
}
