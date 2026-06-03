package ro.ubbcluj.assignment.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductSize> sizes = new ArrayList<>();

    @Column
    private Double rating;

    @Column
    private Integer sales;

    @Column
    private String image;

    public Product() {
    }

    public Product(Long id, String name, String category, Double price, List<ProductSize> sizes, Double rating,
            Integer sales, String image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.sizes = sizes != null ? sizes : new ArrayList<>();
        this.rating = rating;
        this.sales = sales;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<ProductSize> getSizes() {
        return sizes;
    }

    public void setSizes(List<ProductSize> sizes) {
        this.sizes = sizes;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Helper method to add a size and maintain bidirectional relationship
    public void addSize(ProductSize size) {
        sizes.add(size);
        size.setProduct(this);
    }

    // Helper method to remove a size
    public void removeSize(ProductSize size) {
        sizes.remove(size);
        size.setProduct(null);
    }
}
