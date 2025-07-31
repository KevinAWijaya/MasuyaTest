package salesapp.dao;

import salesapp.model.TransactionLine;
import salesapp.utils.database.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionLineDAO {

    public boolean insert(TransactionLine line) {
        String sql = "INSERT INTO TransactionLine (TransactionID, ProductID, Quantity, Price, Disc1, Disc2, Disc3, NetPrice, Amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, line.getTransactionID());
            stmt.setInt(2, line.getProductID());
            stmt.setInt(3, line.getQuantity());
            stmt.setDouble(4, line.getPrice());
            stmt.setDouble(5, line.getDisc1());
            stmt.setDouble(6, line.getDisc2());
            stmt.setDouble(7, line.getDisc3());
            stmt.setDouble(8, line.getNetPrice());
            stmt.setDouble(9, line.getAmount());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Insert TransactionLine Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(TransactionLine line) {
        String sql = "UPDATE TransactionLine SET ProductID=?, Quantity=?, Price=?, Disc1=?, Disc2=?, Disc3=?, NetPrice=?, Amount=? "
                + "WHERE TransactionLineID=?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, line.getProductID());
            stmt.setInt(2, line.getQuantity());
            stmt.setDouble(3, line.getPrice());
            stmt.setDouble(4, line.getDisc1());
            stmt.setDouble(5, line.getDisc2());
            stmt.setDouble(6, line.getDisc3());
            stmt.setDouble(7, line.getNetPrice());
            stmt.setDouble(8, line.getAmount());
            stmt.setInt(9, line.getTransactionLineID());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TransactionLine> getByTransactionID(int transactionID) {
        List<TransactionLine> list = new ArrayList<>();
        String sql = "SELECT * FROM TransactionLine WHERE TransactionID = ? ORDER BY TransactionLineID";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractTransactionLine(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("GetByTransactionID Failed:");
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteByTransactionID(int transactionID) {
        String sql = "DELETE FROM TransactionLine WHERE TransactionID = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Delete TransactionLines Failed:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLineById(int id) {
        String sql = "DELETE FROM TransactionLine WHERE TransactionLineId = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Delete TransactionLines Failed:");
            e.printStackTrace();
            return false;
        }
    }

    private TransactionLine extractTransactionLine(ResultSet rs) throws SQLException {
        TransactionLine line = new TransactionLine();
        line.setTransactionLineID(rs.getInt("TransactionLineID"));
        line.setTransactionID(rs.getInt("TransactionID"));
        line.setProductID(rs.getInt("ProductID"));
        line.setQuantity(rs.getInt("Quantity"));
        line.setPrice(rs.getDouble("Price"));
        line.setDisc1(rs.getDouble("Disc1"));
        line.setDisc2(rs.getDouble("Disc2"));
        line.setDisc3(rs.getDouble("Disc3"));
        line.setNetPrice(rs.getDouble("NetPrice"));
        line.setAmount(rs.getDouble("Amount"));
        return line;
    }

    public int getUsedQuantityForTransaction(int transactionId, int productId) {
        int totalQty = 0;
        String sql = "SELECT SUM(Quantity) FROM TransactionLine WHERE TransactionID = ? AND ProductID = ?";

        try (
                Connection conn = DBHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            stmt.setInt(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalQty = rs.getInt(1); // bisa null
                }
            }

        } catch (SQLException e) {
            System.err.println("Get Used Quantity Failed:");
            e.printStackTrace();
        }

        return totalQty;
    }
}
