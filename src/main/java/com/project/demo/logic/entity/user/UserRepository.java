package com.project.demo.logic.entity.user;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            SELECT u FROM User u
            WHERE
                LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.lastname2) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)
            """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u")
    Page<User> findAll(Pageable pageable);

    @Query("""
            SELECT u FROM User u WHERE LOWER(u.identification) = LOWER(:identification)
            """)
    Optional<User> findByIdentification(@Param("identification") String identification);

    @Query(value = """
    SELECT 
        m.month,
        COUNT(u.id) AS total
    FROM (
        SELECT 1 AS month UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL 
        SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL 
        SELECT 11 UNION ALL SELECT 12
    ) m
    LEFT JOIN user u 
        ON MONTH(u.created_at) = m.month
    WHERE (:year = 0 OR YEAR(u.created_at) = :year)
      AND (u.role_id IS NULL OR u.role_id <> 2)
    GROUP BY m.month
    ORDER BY m.month
    """, nativeQuery = true)
    List<Object[]> countUsersByMonthAndYear(@Param("year") int year);

    @Query(value = """
    SELECT 
        u.status,
        COUNT(*) AS total
    FROM user u
    GROUP BY u.status
    """, nativeQuery = true)
    List<Object[]> countUsersByStatus();
}
