package salesapp.controller;

import salesapp.model.Product;
import salesapp.repository.ProductRepository;

import java.util.List;

public class ProductListController {

    private final ProductRepository repository = new ProductRepository();

    public List<Product> getAllProducts() {
        return repository.getAll();
    }

    public Product getProductByCode(String code) {
        return repository.getByCode(code);
    }

    public boolean deleteProduct(String code) {
        return repository.deleteProduct(code);
    }
}
