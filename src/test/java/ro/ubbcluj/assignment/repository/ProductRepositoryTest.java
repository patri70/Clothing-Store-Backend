package ro.ubbcluj.assignment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ro.ubbcluj.assignment.model.Product;
import ro.ubbcluj.assignment.model.ProductSize;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private Product createProduct(String name, String category, Double price, List<String> sizes, Double rating, Integer sales) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setRating(rating);
        p.setSales(sales);
        for (String size : sizes) {
            p.addSize(new ProductSize(size));
        }
        return p;
    }

    @Test
    void testSaveAssignsIdAndSaves() {
        Product p = createProduct("Test", "Cat", 10.0, List.of("M"), 5.0, 100);
        Product saved = repository.save(p);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
        assertEquals(1, repository.count());
    }

    @Test
    void testFindById() {
        Product p = repository.save(createProduct("Test", "Cat", 10.0, List.of("M"), 5.0, 100));
        Optional<Product> found = repository.findById(p.getId());
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getName());

        assertFalse(repository.findById(99999L).isPresent());
    }

    @Test
    void testFindAll() {
        repository.save(createProduct("P1", "Cat", 10.0, List.of("M"), 5.0, 100));
        repository.save(createProduct("P2", "Cat", 20.0, List.of("S"), 4.0, 200));
        List<Product> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testPagination() {
        for (int i = 0; i < 25; i++) {
            repository.save(createProduct("P" + i, "Cat", 10.0, List.of(), 5.0, 100));
        }

        Page<Product> page0 = repository.findAll(PageRequest.of(0, 10));
        assertEquals(10, page0.getContent().size());
        assertEquals(25, page0.getTotalElements());
        assertEquals(3, page0.getTotalPages());

        Page<Product> page2 = repository.findAll(PageRequest.of(2, 10));
        assertEquals(5, page2.getContent().size());
    }

    @Test
    void testDeleteById() {
        Product p = repository.save(createProduct("Test", "Cat", 10.0, List.of(), 5.0, 100));
        assertEquals(1, repository.count());

        repository.deleteById(p.getId());
        assertEquals(0, repository.count());
        assertFalse(repository.findById(p.getId()).isPresent());
    }

    @Test
    void testDeleteAll() {
        repository.save(createProduct("Test", "Cat", 10.0, List.of(), 5.0, 100));
        repository.deleteAll();
        assertEquals(0, repository.count());
    }

    @Test
    void testSizesAreSavedWithProduct() {
        Product p = createProduct("Test", "Cat", 10.0, List.of("S", "M", "L"), 5.0, 100);
        Product saved = repository.save(p);

        Product found = repository.findById(saved.getId()).orElseThrow();
        assertEquals(3, found.getSizes().size());
    }

    @Test
    void testCascadeDeleteRemovesSizes() {
        Product p = createProduct("Test", "Cat", 10.0, List.of("S", "M"), 5.0, 100);
        Product saved = repository.save(p);
        Long id = saved.getId();

        repository.deleteById(id);
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    void testFindByCategory() {
        repository.save(createProduct("P1", "Dress", 10.0, List.of(), 5.0, 100));
        repository.save(createProduct("P2", "Jacket", 20.0, List.of(), 4.0, 200));
        repository.save(createProduct("P3", "Dress", 30.0, List.of(), 4.5, 150));

        List<Product> dresses = repository.findByCategory("Dress");
        assertEquals(2, dresses.size());
        assertTrue(dresses.stream().allMatch(p -> p.getCategory().equals("Dress")));

        List<Product> jackets = repository.findByCategory("Jacket");
        assertEquals(1, jackets.size());
    }

    @Test
    void testFindByPriceBetween() {
        repository.save(createProduct("P1", "Cat", 10.0, List.of(), 5.0, 100));
        repository.save(createProduct("P2", "Cat", 50.0, List.of(), 4.0, 200));
        repository.save(createProduct("P3", "Cat", 100.0, List.of(), 4.5, 150));

        List<Product> inRange = repository.findByPriceBetween(10.0, 50.0);
        assertEquals(2, inRange.size());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        repository.save(createProduct("Winter Jacket", "Jacket", 100.0, List.of(), 5.0, 100));
        repository.save(createProduct("Summer Dress", "Dress", 50.0, List.of(), 4.0, 200));

        List<Product> results = repository.findByNameContainingIgnoreCase("winter");
        assertEquals(1, results.size());
        assertEquals("Winter Jacket", results.get(0).getName());

        List<Product> results2 = repository.findByNameContainingIgnoreCase("ER");
        assertEquals(2, results2.size()); // "wintER" and "summER"
    }

    @Test
    void testFindAveragePrice() {
        repository.save(createProduct("P1", "Cat", 100.0, List.of(), 5.0, 100));
        repository.save(createProduct("P2", "Cat", 200.0, List.of(), 4.0, 200));

        Double avg = repository.findAveragePrice();
        assertEquals(150.0, avg);
    }

    @Test
    void testFindAveragePriceEmpty() {
        Double avg = repository.findAveragePrice();
        assertNull(avg);
    }

    @Test
    void testCountByCategory() {
        repository.save(createProduct("P1", "Dress", 10.0, List.of(), 5.0, 100));
        repository.save(createProduct("P2", "Jacket", 20.0, List.of(), 4.0, 200));
        repository.save(createProduct("P3", "Dress", 30.0, List.of(), 4.5, 150));

        List<Object[]> counts = repository.countByCategory();
        assertEquals(2, counts.size());

        boolean foundDress = false;
        boolean foundJacket = false;
        for (Object[] row : counts) {
            if ("Dress".equals(row[0])) {
                assertEquals(2L, row[1]);
                foundDress = true;
            }
            if ("Jacket".equals(row[0])) {
                assertEquals(1L, row[1]);
                foundJacket = true;
            }
        }
        assertTrue(foundDress);
        assertTrue(foundJacket);
    }
}
