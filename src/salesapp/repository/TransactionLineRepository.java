package salesapp.repository;

import salesapp.dao.TransactionLineDAO;
import salesapp.model.TransactionLine;

import java.util.List;
import salesapp.model.Product;

public class TransactionLineRepository {

    private final TransactionLineDAO dao;

    public TransactionLineRepository() {
        dao = new TransactionLineDAO();
    }

    public List<TransactionLine> getLinesByTransactionID(int transactionID) {
        return dao.getByTransactionID(transactionID);
    }

    public boolean deleteLinesByTransactionID(int transactionID) {
        return dao.deleteByTransactionID(transactionID);
    }

    public boolean deleteLineById(int id) {
        return dao.deleteLineById(id);
    }

    public boolean save(TransactionLine line) {
        if (line.getTransactionLineID() > 0) {
            return dao.update(line); // UPDATE
        } else {
            return dao.insert(line); // INSERT
        }
    }

    public int getRemainingStock(int transactionId, Product product, TransactionLine editingLine) {
        int usedQty = dao.getUsedQuantityForTransaction(transactionId, product.getId());
        int stock = product.getStock();

        // Jika sedang edit, tambahkan kembali qty dari baris yang sedang diedit
        if (editingLine != null && editingLine.getProductID() == product.getId()) {
            usedQty -= editingLine.getQuantity();
        }

        return stock - usedQty;
    }
}
