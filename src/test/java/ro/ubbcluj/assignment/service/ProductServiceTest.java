package ro.ubbcluj.assignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ro.ubbcluj.assignment.dto.ProductDTO;
import ro.ubbcluj.assignment.model.Product;
import ro.ubbcluj.assignment.model.ProductSize;
import ro.ubbcluj.assignment.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    private Product createProduct(Long id, String name, String category, Double price, List<String> sizes, Double rating, Integer sales) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setRating(rating);
        product.setSales(sales);
        for (String size : sizes) {
            product.addSize(new ProductSize(size));
        }
        return product;
    }

    @Test
    void createProduct() {
        ProductDTO dto = new ProductDTO(null, "Test", "Cat", 10.0, List.of("M"), 5.0, 100, null);
        Product savedEntity = createProduct(1L, "Test", "Cat", 10.0, List.of("M"), 5.0, 100);

        when(repository.save(any(Product.class))).thenReturn(savedEntity);

        ProductDTO result = service.create(dto);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void getByIdFound() {
        Product p = createProduct(1L, "T1", "Cat", 10.0, List.of("M"), 5.0, 100);
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        ProductDTO dto = service.getById(1L);
        assertNotNull(dto);
        assertEquals("T1", dto.getName());
        assertEquals(List.of("M"), dto.getSizes());
    }

    @Test
    void getByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertNull(service.getById(999L));
    }

    @Test
    void getAll() {
        List<Product> products = Arrays.asList(
                createProduct(1L, "P1", "Cat", 10.0, List.of("M"), 5.0, 100),
                createProduct(2L, "P2", "Cat", 20.0, List.of("S"), 5.0, 100));
        Page<Product> page = new PageImpl<>(products);

        when(repository.findAll(PageRequest.of(0, 10, Sort.by("id")))).thenReturn(page);

        List<ProductDTO> dtos = service.getAll(0, 10);
        assertEquals(2, dtos.size());
    }

    @Test
    void updateExistingProduct() {
        Product existing = createProduct(1L, "Old", "OldCat", 10.0, List.of("M"), 5.0, 100);
        Product updated = createProduct(1L, "New", "NewCat", 20.0, List.of("L"), 5.0, 100);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class))).thenReturn(updated);

        ProductDTO newDto = new ProductDTO(null, "New", "NewCat", 20.0, List.of("L"), 5.0, 100, null);
        ProductDTO result = service.update(1L, newDto);

        assertNotNull(result);
        assertEquals("New", result.getName());
        assertEquals("NewCat", result.getCategory());
        assertEquals(20.0, result.getPrice());
    }

    @Test
    void updateNonExistingProduct() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertNull(service.update(999L, new ProductDTO()));
    }

    @Test
    void deleteExisting() {
        when(repository.existsById(1L)).thenReturn(true);
        assertTrue(service.delete(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteNonExisting() {
        when(repository.existsById(999L)).thenReturn(false);
        assertFalse(service.delete(999L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void getAveragePrice() {
        when(repository.findAveragePrice()).thenReturn(150.0);
        assertEquals(150.0, service.getAveragePrice());
    }

    @Test
    void getAveragePriceEmpty() {
        when(repository.findAveragePrice()).thenReturn(null);
        assertEquals(0.0, service.getAveragePrice());
    }

    @Test
    void getByCategory() {
        List<Product> products = List.of(
                createProduct(1L, "P1", "Dress", 10.0, List.of(), 5.0, 100));
        when(repository.findByCategory("Dress")).thenReturn(products);

        List<ProductDTO> result = service.getByCategory("Dress");
        assertEquals(1, result.size());
        assertEquals("Dress", result.get(0).getCategory());
    }

    @Test
    void getByPriceRange() {
        List<Product> products = List.of(
                createProduct(1L, "P1", "Cat", 50.0, List.of(), 5.0, 100));
        when(repository.findByPriceBetween(10.0, 100.0)).thenReturn(products);

        List<ProductDTO> result = service.getByPriceRange(10.0, 100.0);
        assertEquals(1, result.size());
    }

    @Test
    void searchByName() {
        List<Product> products = List.of(
                createProduct(1L, "Winter Jacket", "Jacket", 100.0, List.of(), 5.0, 100));
        when(repository.findByNameContainingIgnoreCase("winter")).thenReturn(products);

        List<ProductDTO> result = service.searchByName("winter");
        assertEquals(1, result.size());
        assertEquals("Winter Jacket", result.get(0).getName());
    }

    @Test
    void getCountByCategory() {
        List<Object[]> results = List.of(
                new Object[]{"Dress", 2L},
                new Object[]{"Jacket", 3L});
        when(repository.countByCategory()).thenReturn(results);

        var map = service.getCountByCategory();
        assertEquals(2, map.size());
        assertEquals(2L, map.get("Dress"));
        assertEquals(3L, map.get("Jacket"));
    }
}
