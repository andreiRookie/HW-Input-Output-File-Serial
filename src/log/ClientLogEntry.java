package log;

public class ClientLogEntry {

    private int productNum;
    private int amount;

    public ClientLogEntry(int productNum, int amount) {
        this.productNum = productNum;
        this.amount = amount;
    }

    public String[] toStringArray() {
        return new String[]{String.valueOf(this.productNum), String.valueOf(this.amount)};
    }

    public int getProductNum() {
        return productNum;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "ClientLogEntry{" +
                "\n\tproductNum='" + productNum + "', " +
                "amount='" + amount +
                "'}";

    }
}
