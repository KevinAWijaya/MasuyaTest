package salesapp.dao;

import salesapp.model.TransactionHeader;
import salesapp.utils.database.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHeaderDAO {

    public boolean insert(TransactionHeader header) {
        String sql = "INSERT INTO TransactionHeader (InvoiceNumber, CustomerID, InvoiceDate, Total, Status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, header.getInvoiceNumber());
            stmt.setInt(2, header.getCustomerID());
            stmt.setDate(3, new java.sql.Date(header.getInvoiceDate().getTime()));
            stmt.setDouble(4, header.getTotal());
            stmt.setString(5, header.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Insert TransactionHeader Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public TransactionHeader findByInvoiceNumber(String invoiceNumber) {
        String sql = "SELECT * FROM TransactionHeader WHERE InvoiceNumber = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTransactionHeader(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("FindByInvoiceNumber Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public TransactionHeader findById(int id) {
        String sql = "SELECT * FROM TransactionHeader WHERE TransactionID = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTransactionHeader(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("FindById TransactionHeader Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public List<TransactionHeader> getAll() {
        List<TransactionHeader> list = new ArrayList<>();
        String sql = "SELECT * FROM TransactionHeader ORDER BY InvoiceDate DESC";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractTransactionHeader(rs));
            }

        } catch (SQLException e) {
            System.err.println("GetAll TransactionHeader Failed:");
            e.printStackTrace();
        }

        return list;
    }

    private TransactionHeader extractTransactionHeader(ResultSet rs) throws SQLException {
        TransactionHeader header = new TransactionHeader();
        header.setTransactionID(rs.getInt("TransactionID"));
        header.setInvoiceNumber(rs.getString("InvoiceNumber"));
        header.setCustomerID(rs.getInt("CustomerID"));
        header.setInvoiceDate(rs.getDate("InvoiceDate"));
        header.setTotal(rs.getDouble("Total"));
        header.setStatus(rs.getString("Status"));
        return header;
    }

    public String getLastInvoiceNumberInMonth(int year, int month) {
        String sql = "SELECT TOP 1 InvoiceNumber FROM TransactionHeader "
                + "WHERE YEAR(InvoiceDate) = ? AND MONTH(InvoiceDate) = ? "
                + "ORDER BY InvoiceNumber DESC";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("InvoiceNumber");
                }
            }

        } catch (SQLException e) {
            System.err.println("GetLastInvoiceNumberInMonth Failed:");
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateTotalAndStatus(int transactionID, double total, String status) {
        String sql = "UPDATE TransactionHeader SET Total = ?, Status = ? WHERE TransactionID = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, total);
            stmt.setString(2, status);
            stmt.setInt(3, transactionID);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Update Total & Status Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(TransactionHeader header) {
        String sql = "UPDATE TransactionHeader SET CustomerID = ?, InvoiceDate = ? WHERE InvoiceNumber = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, header.getCustomerID());
            stmt.setDate(2, new java.sql.Date(header.getInvoiceDate().getTime()));
            stmt.setString(3, header.getInvoiceNumber());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Update TransactionHeader Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public List<TransactionHeader> getAllWithCustomerName() {
        List<TransactionHeader> list = new ArrayList<>();
        String sql = "SELECT th.*, c.CustomerName "
                + "FROM TransactionHeader th "
                + "JOIN Customers c ON th.CustomerID = c.CustomerID "
                + "ORDER BY th.InvoiceDate DESC";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TransactionHeader header = extractTransactionHeader(rs);
                header.setCustomerName(rs.getString("CustomerName")); // ← dari JOIN
                list.add(header);
            }

        } catch (SQLException e) {
            System.err.println("GetAllWithCustomerName Failed:");
            e.printStackTrace();
        }

        return list;
    }
}
