package ro.ubbcluj.assignment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ro.ubbcluj.assignment.dto.ProductDTO;
import ro.ubbcluj.assignment.service.ProductService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private ProductService productService;
    private ProductController controller;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        controller = new ProductController(productService);
    }

    @Test
    void testCreateProduct() {
        ProductDTO dto = new ProductDTO(null, "Test", "Cat", 10.0, List.of(), 5.0, 100, null);
        ProductDTO created = new ProductDTO(1L, "Test", "Cat", 10.0, List.of(), 5.0, 100, null);
        when(productService.create(any(ProductDTO.class))).thenReturn(created);

        ResponseEntity<ProductDTO> response = controller.createProduct(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetAllProducts() {
        when(productService.getAll(0, 10)).thenReturn(Arrays.asList(
                new ProductDTO(1L, "P1", "Cat", 10.0, List.of(), 5.0, 100, null),
                new ProductDTO(2L, "P2", "Cat", 20.0, List.of(), 5.0, 100, null)));
        when(productService.count()).thenReturn(2L);

        ResponseEntity<Map<String, Object>> response = controller.getAllProducts(0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        List<ProductDTO> content = (List<ProductDTO>) response.getBody().get("content");
        assertEquals(2, content.size());
        assertEquals(2L, response.getBody().get("totalElements"));
    }

    @Test
    void testGetProductByIdFound() {
        ProductDTO found = new ProductDTO(1L, "P1", "Cat", 10.0, List.of(), 5.0, 100, null);
        when(productService.getById(1L)).thenReturn(found);

        ResponseEntity<ProductDTO> response = controller.getProductById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("P1", response.getBody().getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productService.getById(999L)).thenReturn(null);
        ResponseEntity<ProductDTO> response = controller.getProductById(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateProductSuccess() {
        ProductDTO updatedDTO = new ProductDTO(1L, "New", "Cat", 20.0, List.of(), 5.0, 100, null);
        when(productService.update(eq(1L), any(ProductDTO.class))).thenReturn(updatedDTO);

        ResponseEntity<ProductDTO> response = controller.updateProduct(1L, new ProductDTO());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New", response.getBody().getName());
    }

    @Test
    void testUpdateProductNotFound() {
        when(productService.update(eq(999L), any(ProductDTO.class))).thenReturn(null);
        ResponseEntity<ProductDTO> response = controller.updateProduct(999L, new ProductDTO());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProductSuccess() {
        when(productService.delete(1L)).thenReturn(true);
        ResponseEntity<Void> response = controller.deleteProduct(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteProductNotFound() {
        when(productService.delete(999L)).thenReturn(false);
        ResponseEntity<Void> response = controller.deleteProduct(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAveragePrice() {
        when(productService.getAveragePrice()).thenReturn(150.5);
        ResponseEntity<Map<String, Double>> response = controller.getAveragePrice();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("averagePrice"));
        assertEquals(150.5, response.getBody().get("averagePrice"));
    }

    @Test
    void testGetCountByCategory() {
        Map<String, Long> counts = Map.of("Dress", 2L, "Jacket", 3L);
        when(productService.getCountByCategory()).thenReturn(counts);

        ResponseEntity<Map<String, Long>> response = controller.getCountByCategory();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody().get("Dress"));
    }

    @Test
    void testFilterByCategory() {
        List<ProductDTO> products = List.of(new ProductDTO(1L, "P1", "Dress", 10.0, List.of(), 5.0, 100, null));
        when(productService.getByCategory("Dress")).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = controller.filterByCategory("Dress");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFilterByPriceRange() {
        List<ProductDTO> products = List.of(new ProductDTO(1L, "P1", "Cat", 50.0, List.of(), 5.0, 100, null));
        when(productService.getByPriceRange(10.0, 100.0)).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = controller.filterByPriceRange(10.0, 100.0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testSearchByName() {
        List<ProductDTO> products = List.of(new ProductDTO(1L, "Winter Jacket", "Jacket", 100.0, List.of(), 5.0, 100, null));
        when(productService.searchByName("winter")).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = controller.searchByName("winter");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
