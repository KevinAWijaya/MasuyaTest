package salesapp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import salesapp.model.Customer;
import salesapp.utils.database.DBHelper;

public class CustomerDAO {

    public boolean insert(Customer customer) {
        String sql = "INSERT INTO Customers (CustomerCode, CustomerName, Address, Province, City, District, Subdistrict, PostalCode) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCode());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getProvince());
            stmt.setString(5, customer.getCity());
            stmt.setString(6, customer.getDistrict());
            stmt.setString(7, customer.getSubdistrict());
            stmt.setString(8, customer.getPostalCode());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Insert Customer Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Customer customer) {
        String sql = "UPDATE Customers SET CustomerName = ?, Address = ?, Province = ?, City = ?, District = ?, Subdistrict = ?, PostalCode = ? "
                + "WHERE CustomerCode = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getProvince());
            stmt.setString(4, customer.getCity());
            stmt.setString(5, customer.getDistrict());
            stmt.setString(6, customer.getSubdistrict());
            stmt.setString(7, customer.getPostalCode());
            stmt.setString(8, customer.getCode());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Update Customer Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String customerCode) {
        String sql = "DELETE FROM Customers WHERE CustomerCode = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerCode);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key") || e.getMessage().toLowerCase().contains("constraint")) {
                throw new RuntimeException("Customer tidak bisa dihapus karena sudah memiliki transaksi.");
            }

            System.err.println("Delete Customer Failed:");
            e.printStackTrace();
            throw new RuntimeException("Terjadi kesalahan saat menghapus customer.");
        }
    }

    public Customer findByCode(String code) {
        String sql = "SELECT * FROM Customers WHERE CustomerCode = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("FindByCode Customer Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public Customer findById(int id) {
        String sql = "SELECT * FROM Customers WHERE CustomerId = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("FindById Customer Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY CustomerCode";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractCustomer(rs));
            }

        } catch (SQLException e) {
            System.err.println("GetAll Customer Failed:");
            e.printStackTrace();
        }

        return list;
    }

    private Customer extractCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("CustomerId"),
                rs.getString("CustomerCode"),
                rs.getString("CustomerName"),
                rs.getString("Address"),
                rs.getString("Province"),
                rs.getString("City"),
                rs.getString("District"),
                rs.getString("Subdistrict"),
                rs.getString("PostalCode")
        );
    }
}
