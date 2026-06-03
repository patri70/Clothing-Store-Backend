package ro.ubbcluj.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.assignment.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Filter by category
    List<Product> findByCategory(String category);

    // Filter by price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Search by name (case-insensitive, partial match)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Statistics: average price
    @Query("SELECT AVG(p.price) FROM Product p")
    Double findAveragePrice();

    // Statistics: count products per category
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countByCategory();
}
