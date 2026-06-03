package ro.ubbcluj.assignment.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.ubbcluj.assignment.model.Product;
import ro.ubbcluj.assignment.model.ProductSize;
import ro.ubbcluj.assignment.repository.ProductRepository;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ro.ubbcluj.assignment.repository.UserRepository userRepository;
    private final ro.ubbcluj.assignment.repository.RoleRepository roleRepository;
    private final ro.ubbcluj.assignment.repository.PermissionRepository permissionRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataSeeder(ProductRepository productRepository, 
                      ro.ubbcluj.assignment.repository.UserRepository userRepository,
                      ro.ubbcluj.assignment.repository.RoleRepository roleRepository,
                      ro.ubbcluj.assignment.repository.PermissionRepository permissionRepository,
                      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAuthData();
        seedProductData();
    }

    private void seedAuthData() {
        // Ensure Roles exist
        ro.ubbcluj.assignment.model.Permission readPerm = permissionRepository.findByName("READ_PRIVILEGE")
                .orElseGet(() -> permissionRepository.save(new ro.ubbcluj.assignment.model.Permission("READ_PRIVILEGE")));
        ro.ubbcluj.assignment.model.Permission writePerm = permissionRepository.findByName("WRITE_PRIVILEGE")
                .orElseGet(() -> permissionRepository.save(new ro.ubbcluj.assignment.model.Permission("WRITE_PRIVILEGE")));
        ro.ubbcluj.assignment.model.Permission deletePerm = permissionRepository.findByName("DELETE_PRIVILEGE")
                .orElseGet(() -> permissionRepository.save(new ro.ubbcluj.assignment.model.Permission("DELETE_PRIVILEGE")));

        ro.ubbcluj.assignment.model.Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    ro.ubbcluj.assignment.model.Role r = new ro.ubbcluj.assignment.model.Role("ROLE_ADMIN");
                    r.getPermissions().add(readPerm);
                    r.getPermissions().add(writePerm);
                    r.getPermissions().add(deletePerm);
                    return roleRepository.save(r);
                });

        ro.ubbcluj.assignment.model.Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    ro.ubbcluj.assignment.model.Role r = new ro.ubbcluj.assignment.model.Role("ROLE_USER");
                    r.getPermissions().add(readPerm);
                    return roleRepository.save(r);
                });

        // Ensure Users exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new ro.ubbcluj.assignment.model.User("admin", passwordEncoder.encode("admin"), "admin@vanes.com", adminRole));
        }
 else {
            // Update password for existing user to be hashed
            ro.ubbcluj.assignment.model.User admin = userRepository.findByUsername("admin").get();
            admin.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(admin);
        }
        
        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(new ro.ubbcluj.assignment.model.User("user", passwordEncoder.encode("user"), "user@vanes.com", userRole));
        } else {
            // Update password for existing user to be hashed
            ro.ubbcluj.assignment.model.User user = userRepository.findByUsername("user").get();
            user.setPassword(passwordEncoder.encode("user"));
            userRepository.save(user);
        }
        
        System.out.println("Auth data check completed.");
    }

    private void seedProductData() {
        // Only seed if the database is empty
        if (productRepository.count() > 0) {
            return;
        }

        List<ProductData> seedData = List.of(
                new ProductData("Arabella", "Dress", 110.0, List.of("36", "38", "40"), 4.8, 145, "https://images.unsplash.com/photo-1539008835154-33321daefeab?q=80&w=1974&auto=format&fit=crop"),
                new ProductData("Miara", "Jeans", 45.0, List.of("36", "40"), 4.6, 203, "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?q=80&w=1974&auto=format&fit=crop"),
                new ProductData("Cartia", "Jacket", 180.0, List.of("36", "38", "42"), 4.9, 89, "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?q=80&w=2072&auto=format&fit=crop"),
                new ProductData("Laurna Puff", "Jacket", 230.0, List.of("36", "38", "40"), 4.7, 67, "https://images.unsplash.com/photo-1544022613-e87ca75a784a?q=80&w=1974&auto=format&fit=crop"),
                new ProductData("Ariadna", "Jacket", 170.0, List.of("36", "38", "40", "42"), 4.5, 98, "https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3?q=80&w=2070&auto=format&fit=crop"),
                new ProductData("Leandra", "Accessories", 11.0, List.of("One Size"), 4.3, 312, "https://images.unsplash.com/photo-1523206489230-c012c64b2b48?q=80&w=1974&auto=format&fit=crop")
        );

        for (ProductData data : seedData) {
            Product product = new Product();
            product.setName(data.name);
            product.setCategory(data.category);
            product.setPrice(data.price);
            product.setRating(data.rating);
            product.setSales(data.sales);
            product.setImage(data.image);

            for (String size : data.sizes) {
                product.addSize(new ProductSize(size));
            }

            productRepository.save(product);
        }

        System.out.println("Database seeded with " + seedData.size() + " products.");
    }

    private record ProductData(String name, String category, Double price, List<String> sizes, Double rating,
            Integer sales, String image) {
    }
}
