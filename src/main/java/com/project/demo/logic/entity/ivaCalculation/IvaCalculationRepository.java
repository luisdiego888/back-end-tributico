package com.project.demo.logic.entity.ivaCalculation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IvaCalculationRepository extends JpaRepository<IvaCalculation, Long> {


    @Query("SELECT i FROM IvaCalculation i WHERE i.user.id = :userId AND i.year = :year AND i.month = :month")
    Optional<IvaCalculation> findByUserAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );


    @Query("SELECT i FROM IvaCalculation i WHERE i.user.id = :userId ORDER BY i.year DESC, i.month DESC")
    List<IvaCalculation> findByUserOrderByDateDesc(@Param("userId") Long userId);


    @Query("SELECT COUNT(i) > 0 FROM IvaCalculation i WHERE i.user.id = :userId AND i.year = :year AND i.month = :month")
    boolean existsByUserAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("SELECT e FROM IvaCalculation e WHERE " +
            "e.user.id = :userId AND (" +
            "LOWER(CAST(e.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.user.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<IvaCalculation> searchIvaSimulation(@Param("search") String search, @Param("userId") Long userId,
                                            Pageable pageable);

    Page<IvaCalculation> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT i FROM IvaCalculation i WHERE i.user.id = :userId")
    List<IvaCalculation> findByUserId(@Param("userId") Long userId);
}