package com.project.demo.logic.entity.goals;

import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalsRepository extends JpaRepository<Goals, Long> {

    List<Goals> findByUser(User user);

    List<Goals> findByUserAndDeclaration(User user, String declaration);

    @Query("SELECT e FROM Goals e WHERE " +
            "e.user.id = :userId AND (" +
            "LOWER(CAST(e.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.type AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.declaration AS string)) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Goals> searchGoals(@Param("search") String search, @Param("userId") Long userId,
                                            Pageable pageable);

    Page<Goals> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT i FROM Goals i WHERE i.user.id = :userId")
    List<Goals> findByUserId(@Param("userId") Long userId);
}