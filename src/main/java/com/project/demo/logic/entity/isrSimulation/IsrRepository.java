package com.project.demo.logic.entity.isrSimulation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface IsrRepository extends JpaRepository<IsrSimulation, Long> {

    @Query("SELECT e FROM IsrSimulation e WHERE " +
            "e.user.id = :userId AND (" +
            "LOWER(CAST(e.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.simulationName AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.simulationName AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.user.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<IsrSimulation> searchIsrSimulation(@Param("search") String search, @Param("userId") Long userId,
                                            Pageable pageable);

    Page<IsrSimulation> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT i FROM IsrSimulation i WHERE i.user.id = :userId")
    List<IsrSimulation> findByUserId(@Param("userId") Long userId);

}
