package salesapp.controller;

import salesapp.model.TransactionLine;
import salesapp.repository.TransactionLineRepository;

import java.util.List;
import salesapp.model.Product;

public class TransactionLineController {

    private final TransactionLineRepository repository;

    public TransactionLineController() {
        repository = new TransactionLineRepository();
    }

    public boolean saveTransactionLine(TransactionLine line) {
        if (line.getTransactionLineID() > 0) {
            return updateTransactionLine(line); // update jika ID ada
        } else {
            return addTransactionLine(line); // insert jika ID belum ada
        }
    }

    public List<TransactionLine> getTransactionLinesByTransactionID(int transactionID) {
        return repository.getLinesByTransactionID(transactionID);
    }

    public boolean deleteTransactionLinesByTransactionID(int transactionID) {
        return repository.deleteLinesByTransactionID(transactionID);
    }

    public boolean deleteTransactionLineById(int id) {
        return repository.deleteLineById(id);
    }

    public boolean addTransactionLine(TransactionLine line) {
        return repository.save(line);
    }

    public boolean updateTransactionLine(TransactionLine line) {
        return repository.save(line);
    }

    public int getRemainingStock(int transactionId, Product product, TransactionLine editingLine) {
        return repository.getRemainingStock(transactionId, product, editingLine);
    }

}
