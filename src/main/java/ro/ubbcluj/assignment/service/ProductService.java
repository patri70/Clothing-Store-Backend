package ro.ubbcluj.assignment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ubbcluj.assignment.dto.ProductDTO;
import ro.ubbcluj.assignment.model.Product;
import ro.ubbcluj.assignment.model.ProductSize;
import ro.ubbcluj.assignment.repository.ProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private ProductDTO toDTO(Product product) {
        List<String> sizeStrings = product.getSizes().stream()
                .map(ProductSize::getSize)
                .collect(Collectors.toList());

        return new ProductDTO(
                product.getId(), product.getName(), product.getCategory(),
                product.getPrice(), sizeStrings, product.getRating(), product.getSales(), product.getImage());
    }

    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setRating(dto.getRating());
        product.setSales(dto.getSales());
        product.setImage(dto.getImage());

        if (dto.getSizes() != null) {
            for (String size : dto.getSizes()) {
                product.addSize(new ProductSize(size));
            }
        }

        return product;
    }

    @Transactional
    public ProductDTO create(ProductDTO productDTO) {
        Product product = toEntity(productDTO);
        product.setId(null); // ensure auto-generation
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    public ProductDTO getById(Long id) {
        return productRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public List<ProductDTO> getAll(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id")));
        return productPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long count() {
        return productRepository.count();
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(productDTO.getName());
            existing.setCategory(productDTO.getCategory());
            existing.setPrice(productDTO.getPrice());
            existing.setRating(productDTO.getRating());
            existing.setSales(productDTO.getSales());
            existing.setImage(productDTO.getImage());

            // Clear old sizes and add new ones
            existing.getSizes().clear();
            if (productDTO.getSizes() != null) {
                for (String size : productDTO.getSizes()) {
                    existing.addSize(new ProductSize(size));
                }
            }

            Product updated = productRepository.save(existing);
            return toDTO(updated);
        }).orElse(null);
    }

    @Transactional
    public boolean delete(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Statistics
    public Double getAveragePrice() {
        Double avg = productRepository.findAveragePrice();
        return avg != null ? avg : 0.0;
    }

    public Map<String, Long> getCountByCategory() {
        List<Object[]> results = productRepository.countByCategory();
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }

    // Filters
    public List<ProductDTO> getByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
