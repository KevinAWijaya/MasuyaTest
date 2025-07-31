package salesapp.repository;

import salesapp.dao.ProductDAO;
import salesapp.model.Product;

import java.util.List;
import salesapp.utils.ValidationUtils;

public class ProductRepository {

    private final ProductDAO dao;

    public ProductRepository() {
        dao = new ProductDAO();
    }

    public AddIntoDatabaseResult addProduct(Product product) {
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            return AddIntoDatabaseResult.INVALID;
        }

        if (!ValidationUtils.isAlphanumeric(product.getCode())) {
            return AddIntoDatabaseResult.INVALID;
        }

        if (isCodeExist(product.getCode())) {
            return AddIntoDatabaseResult.DUPLICATE;
        }

        boolean success = dao.insert(product);
        return success ? AddIntoDatabaseResult.SUCCESS : AddIntoDatabaseResult.FAILED;
    }

    public List<Product> getAll() {
        return dao.getAll();
    }

    public Product getByCode(String code) {
        return dao.findByCode(code);
    }

    public Product findById(Integer id) {
        return dao.findById(id);
    }

    public boolean deleteProduct(String code) {
        return dao.delete(code);
    }

    public boolean updateProduct(Product product) {
        return dao.update(product);
    }

    public boolean isCodeExist(String code) {
        return dao.findByCode(code) != null;
    }

    public boolean reduceStock(int productId, int quantity) {
        return dao.reduceStock(productId, quantity);
    }
}
