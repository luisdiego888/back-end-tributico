package com.project.demo.logic.entity.invoice;

import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT e FROM Invoice e WHERE " +
            "e.user.id = :userId AND (" +
            "LOWER(CAST(e.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.consecutive AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.consecutive AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.issuer.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.issuer.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Invoice> searchInvoices(@Param("search") String search, @Param("userId") Long userId,
                                        Pageable pageable);

    Page<Invoice> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT i FROM Invoice i WHERE YEAR(i.issueDate) = :year AND i.user.id = :userId")
    List<Invoice> findByYear(int year, Long userId);

    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId")
    List<Invoice> findByUserId(@Param("userId") Long userId);

    @Query(value = """
    SELECT 
        EXTRACT(MONTH FROM i.issue_date) AS month,
        SUM(COALESCE(d.total, 0)) AS total
    FROM invoice i
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
      AND (:type = 'all' OR i.type = :type)
      AND (:userId IS NULL OR i.user_id = :userId)
    GROUP BY month
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> getMonthlyInvoiceTotals(@Param("year") int year, @Param("type") String type, @Param("userId") Long userId);

    @Query(value = """
    SELECT 
        EXTRACT(MONTH FROM i.issue_date) AS month,
        COUNT(*) AS invoice_count
    FROM invoice i
    WHERE i.issue_date IS NOT NULL
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
    GROUP BY month
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> getMonthlyInvoiceVolume(@Param("year") int year);

    @Query(value = """
    SELECT 
        d.category,
        SUM(COALESCE(d.total, 0)) AS total
    FROM invoice i
    JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL
      AND i.type = 'gasto'
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
      AND (:userId IS NULL OR i.user_id = :userId)
    GROUP BY d.category
    ORDER BY total DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Object[]> getTop5ExpenseCategoriesByYear(@Param("year") int year, @Param("userId") Long userId);

    @Query(value = """
    SELECT
    u.id AS user_id,
    u.name AS user_name,
    u.lastname AS user_lastname,
    COUNT(DISTINCT i.id) AS total_invoices,
    SUM(CASE WHEN i.type = 'ingreso' THEN COALESCE(d.total, 0) ELSE 0 END) AS total_ingresos,
    SUM(CASE WHEN i.type = 'gasto' THEN COALESCE(d.total, 0) ELSE 0 END) AS total_gastos
    FROM invoice i
    INNER JOIN user u ON i.user_id = u.id
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL
    GROUP BY u.id, u.name, u.lastname
    ORDER BY total_invoices DESC
    LIMIT 10;
    """, nativeQuery = true)
    List<Object[]> getTop10UsersByInvoiceVolume();

    @Query(value = """
    SELECT 
        SUM(CASE WHEN i.type = 'ingreso' THEN COALESCE(d.total, 0) ELSE 0 END)
    FROM invoice i
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL 
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
    """, nativeQuery = true)
    Double getTotalIncomeByYear(@Param("year") int year);

    @Query(value = """
    SELECT 
        SUM(CASE WHEN i.type = 'gasto' THEN COALESCE(d.total, 0) ELSE 0 END)
    FROM invoice i
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL 
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
    """, nativeQuery = true)
    Double getTotalExpensesByYear(@Param("year") int year);

    @Query(value = """
    SELECT 
        EXTRACT(MONTH FROM i.issue_date) AS month,
        SUM(CASE WHEN i.type = 'ingreso' THEN COALESCE(d.total, 0) ELSE 0 END)
    FROM invoice i
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
    GROUP BY EXTRACT(MONTH FROM i.issue_date)
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> getMonthlyIncomeByYear(@Param("year") int year);

    @Query(value = """
    SELECT 
        EXTRACT(MONTH FROM i.issue_date) AS month,
        SUM(CASE WHEN i.type = 'gasto' THEN COALESCE(d.total, 0) ELSE 0 END)
    FROM invoice i
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL
      AND (:year = 0 OR EXTRACT(YEAR FROM i.issue_date) = :year)
    GROUP BY EXTRACT(MONTH FROM i.issue_date)
    ORDER BY month
    """, nativeQuery = true)
    List<Object[]> getMonthlyExpensesByYear(@Param("year") int year);

    @Query(value = """
    SELECT
        u.id AS user_id,
        u.name AS user_name,
        u.lastname AS user_lastname,
        SUM(CASE WHEN i.type = 'ingreso' THEN COALESCE(d.total, 0) ELSE 0 END) -
        SUM(CASE WHEN i.type = 'gasto' THEN COALESCE(d.total, 0) ELSE 0 END) AS balance
    FROM invoice i
    INNER JOIN user u ON i.user_id = u.id
    LEFT JOIN details_invoice d ON i.id = d.invoice_id
    WHERE i.issue_date IS NOT NULL
    GROUP BY u.id, u.name, u.lastname
    ORDER BY ABS(
      SUM(CASE WHEN i.type = 'ingreso' THEN COALESCE(d.total, 0) ELSE 0 END) -
      SUM(CASE WHEN i.type = 'gasto' THEN COALESCE(d.total, 0) ELSE 0 END)
    ) DESC
    LIMIT 10
    """, nativeQuery = true)
    List<Object[]> getTop10UsersByBalance();
}
