package ro.ubbcluj.assignment.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.assignment.dto.ProductDTO;
import ro.ubbcluj.assignment.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin // Allows any frontend (like localhost:3000) to fetch from this backend
@Validated // Enforces class-level best practices for validation
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.create(productDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Sanitize strictly
        if (page < 0)
            page = 0;
        if (size <= 0)
            size = 10;

        List<ProductDTO> products = productService.getAll(page, size);
        long totalCount = productService.count();

        Map<String, Object> response = new HashMap<>();
        response.put("content", products);
        response.put("totalElements", totalCount);
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", (int) Math.ceil((double) totalCount / size));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getById(id);
        if (productDTO != null) {
            return ResponseEntity.ok(productDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.update(id, productDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ---- Statistics Endpoints ----

    @GetMapping("/statistics/average-price")
    public ResponseEntity<Map<String, Double>> getAveragePrice() {
        Double avg = productService.getAveragePrice();
        Map<String, Double> response = new HashMap<>();
        response.put("averagePrice", avg);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/count-by-category")
    public ResponseEntity<Map<String, Long>> getCountByCategory() {
        return ResponseEntity.ok(productService.getCountByCategory());
    }

    // ---- Filter Endpoints ----

    @GetMapping("/filter/category")
    public ResponseEntity<List<ProductDTO>> filterByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getByCategory(category));
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<ProductDTO>> filterByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(productService.getByPriceRange(min, max));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }
}
