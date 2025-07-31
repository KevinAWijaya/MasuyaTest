package salesapp.repository;

public enum TransactionStatus {
    DRAFT("draft"),
    SUBMIT("submit"),
    VOID("void");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransactionStatus fromString(String text) {
        for (TransactionStatus s : TransactionStatus.values()) {
            if (s.value.equalsIgnoreCase(text)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
