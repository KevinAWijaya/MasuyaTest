package salesapp.model;

public class TransactionLine {

    private int transactionLineID;
    private int transactionID;
    private int productID;
    private int quantity;
    private double price;
    private double disc1;
    private double disc2;
    private double disc3;
    private double netPrice;
    private double amount;

    // Getters & Setters
    public int getTransactionLineID() {
        return transactionLineID;
    }

    public void setTransactionLineID(int transactionLineID) {
        this.transactionLineID = transactionLineID;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDisc1() {
        return disc1;
    }

    public void setDisc1(double disc1) {
        this.disc1 = disc1;
    }

    public double getDisc2() {
        return disc2;
    }

    public void setDisc2(double disc2) {
        this.disc2 = disc2;
    }

    public double getDisc3() {
        return disc3;
    }

    public void setDisc3(double disc3) {
        this.disc3 = disc3;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
