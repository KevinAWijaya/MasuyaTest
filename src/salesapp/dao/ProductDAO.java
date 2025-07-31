package salesapp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import salesapp.model.Product;
import salesapp.utils.database.DBHelper;

public class ProductDAO {

    public boolean insert(Product product) {
        String sql = "INSERT INTO Products (ProductCode, ProductName, Price, Stock) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Insert Product Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Product product) {
        String sql = "UPDATE Products SET ProductName = ?, Price = ?, Stock = ? WHERE ProductCode = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getCode());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Update Product Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String productCode) {
        String sql = "DELETE FROM Products WHERE ProductCode = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key") || e.getMessage().toLowerCase().contains("constraint")) {
                throw new RuntimeException("Product tidak bisa dihapus karena sudah memiliki transaksi.");
            }

            System.err.println("Delete Product Failed:");
            e.printStackTrace();
            throw new RuntimeException("Terjadi kesalahan saat menghapus Product.");
        }
    }

    public Product findByCode(String code) {
        String sql = "SELECT * FROM Products WHERE ProductCode = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("FindByCode Product Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public Product findById(int id) {
        String sql = "SELECT * FROM Products WHERE ProductId = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("FindById Product Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products ORDER BY ProductCode";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("GetAll Product Failed:");
            e.printStackTrace();
        }

        return list;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("ProductId"),
                rs.getString("ProductCode"),
                rs.getString("ProductName"),
                rs.getDouble("Price"),
                rs.getInt("Stock")
        );
    }

    public boolean reduceStock(int productId, int quantity) {
        String sql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Reduce stock failed:");
            e.printStackTrace();
            return false;
        }
    }
}
