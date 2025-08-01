package salesapp.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import salesapp.model.TransactionHeader;

import java.util.List;
import salesapp.dao.TransactionHeaderDAO;
import salesapp.dao.TransactionLineDAO;
import salesapp.model.TransactionLine;
import salesapp.utils.database.DBHelper;

public class TransactionHeaderRepository {

    private final TransactionHeaderDAO dao;

    public TransactionHeaderRepository() {
        dao = new TransactionHeaderDAO();
    }

    public boolean addTransactionHeader(TransactionHeader header) {
        return dao.insert(header);
    }

    public TransactionHeader getByInvoiceNumber(String invoiceNumber) {
        return dao.findByInvoiceNumber(invoiceNumber);
    }

    public List<TransactionHeader> getAllHeaders() {
        return dao.getAll();
    }

    public TransactionHeader getById(int id) {
        return dao.findById(id);
    }

    public boolean updateTotalAndStatus(int transactionID, double total, String status) {
        return dao.updateTotalAndStatus(transactionID, total, status);
    }

    public boolean update(TransactionHeader header) {
        return dao.update(header);
    }

    public String generateInvoiceNumber(Date invoiceDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(invoiceDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;

        String prefix = "INV";
        String yyMM = new SimpleDateFormat("yyMM").format(invoiceDate);

        String lastInvoice = dao.getLastInvoiceNumberInMonth(year, month);
        int nextNumber = 1;

        if (lastInvoice != null && lastInvoice.startsWith("INV/" + yyMM)) {
            String[] parts = lastInvoice.split("/");
            if (parts.length == 3) {
                try {
                    nextNumber = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        String formattedNumber = String.format("%04d", nextNumber);
        return prefix + "/" + yyMM + "/" + formattedNumber;
    }

    public boolean saveFullTransaction(TransactionHeader header, List<TransactionLine> lines) throws SQLException {
        Connection conn = DBHelper.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            TransactionHeaderDAO headerDAO = new TransactionHeaderDAO();
            TransactionLineDAO lineDAO = new TransactionLineDAO();

            boolean headerInserted = headerDAO.insert(header);
            if (!headerInserted) {
                conn.rollback();
                return false;
            }

            // Ambil ID header yang baru saja dimasukkan
            TransactionHeader inserted = headerDAO.findByInvoiceNumber(header.getInvoiceNumber());
            if (inserted == null) {
                conn.rollback();
                return false;
            }

            for (TransactionLine line : lines) {
                line.setTransactionID(inserted.getTransactionID());
                boolean lineInserted = lineDAO.insert(line);
                if (!lineInserted) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<TransactionHeader> getAllHeadersWithCustomerName() {
        return dao.getAllWithCustomerName();
    }
}
