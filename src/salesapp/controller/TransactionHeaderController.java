package salesapp.controller;

import java.sql.SQLException;
import java.util.Date;
import salesapp.model.TransactionHeader;
import salesapp.repository.TransactionHeaderRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import salesapp.model.TransactionLine;
import salesapp.repository.TransactionStatus;

public class TransactionHeaderController {

    private final TransactionHeaderRepository repository;

    public TransactionHeaderController() {
        repository = new TransactionHeaderRepository();
    }

    public boolean saveTransactionHeader(TransactionHeader header) {
        return repository.addTransactionHeader(header);
    }

    public TransactionHeader getTransactionHeaderByInvoice(String invoiceNumber) {
        return repository.getByInvoiceNumber(invoiceNumber);
    }

    public TransactionHeader getTransactionHeaderById(int id) {
        return repository.getById(id);
    }

    public List<TransactionHeader> getAllTransactionHeaders() {
        return repository.getAllHeaders();
    }

    public String generateInvoiceNumber(Date date) {
        return repository.generateInvoiceNumber(date);
    }

    public boolean updateTotalAndStatus(int transactionID, double total, String status) {
        return repository.updateTotalAndStatus(transactionID, total, status);
    }

    public boolean updateTransactionHeader(TransactionHeader header) {
        return repository.update(header);
    }

    public boolean submitTransaction(TransactionHeader header, double total) {
        header.setStatus(TransactionStatus.SUBMIT.getValue());
        header.setTotal(total);
        return repository.update(header);
    }

    public boolean saveFullTransaction(TransactionHeader header, List<TransactionLine> lines) {
        try {
            return repository.saveFullTransaction(header, lines);
        } catch (SQLException ex) {
            Logger.getLogger(TransactionHeaderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<TransactionHeader> getAllTransactionHeadersWithCustomerName() {
        return repository.getAllHeadersWithCustomerName();
    }
}
