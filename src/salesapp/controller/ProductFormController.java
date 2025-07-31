package salesapp.controller;

import salesapp.model.Product;
import salesapp.repository.ProductRepository;
import salesapp.repository.AddIntoDatabaseResult;
import salesapp.utils.dialog.LoadingUtils;

import javax.swing.*;

public class ProductFormController {

    private final ProductRepository repository = new ProductRepository();

    public boolean reduceStock(int productId, int quantity) {
        return repository.reduceStock(productId, quantity);
    }

    public Product getProductById(Integer id) {
        return repository.findById(id);
    }

public void saveProduct(Product product, Runnable onSuccessClearForm) {
    if (product == null) {
        showError("Product is invalid.");
        return;
    }

    // Validasi minimal
    if (product.getCode().isEmpty() || product.getName().isEmpty()) {
        showError("Code and name are required!");
        return;
    }

    showLoading("Saving product...", () -> {
        AddIntoDatabaseResult result = repository.addProduct(product);
        SwingUtilities.invokeLater(() -> {
            switch (result) {
                case SUCCESS:
                    showSuccess("Product saved successfully!");
                    onSuccessClearForm.run();
                    break;
                case DUPLICATE:
                    showError("Product code already exists.");
                    break;
                case INVALID:
                    showError("Invalid product data (e.g., empty or non-alphanumeric code).");
                    break;
                case FAILED:
                default:
                    showError("Failed to save product.");
                    break;
            }
        });
    });
}

    public void updateProduct(Product product, Runnable onSuccess) {
        if (product.getCode().isEmpty() || product.getName().isEmpty()) {
            showError("Code and name are required!");
            return;
        }
        showLoading("Updating product...", () -> {
            boolean success = repository.updateProduct(product);
            if (success) {
                showSuccess("Product updated successfully!");
                onSuccess.run();
            } else {
                showError("Failed to update product.");
            }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLoading(String message, Runnable task) {
        JDialog[] loadingDialog = new JDialog[1];
        SwingUtilities.invokeLater(() -> {
            loadingDialog[0] = LoadingUtils.showLoadingDialog(message);
            new Thread(() -> {
                task.run();
                SwingUtilities.invokeLater(() -> LoadingUtils.closeDialog(loadingDialog[0]));
            }).start();
        });
    }

    public Product buildProductFromForm(String code, String name, String priceText, String stockText, int id) {
        Product product = new Product();
        product.setId(id);
        product.setCode(code.trim());
        product.setName(name.trim());
        try {
            product.setPrice(Double.parseDouble(priceText.trim()));
            product.setStock(Integer.parseInt(stockText.trim()));
        } catch (NumberFormatException e) {
            showError("Price and Stock must be numeric.");
            return null;
        }
        return product;
    }
}
