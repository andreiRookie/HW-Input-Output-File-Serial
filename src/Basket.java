import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Basket {
    private Product[] products;

    public Basket(Product[] products) {
        this.products = products;
    }

    public void saveTxt(File txtFile) throws IOException {
        try (FileWriter writer = new FileWriter(txtFile)) {
            for (Product p : products) {
                writer.write(
                        p.getProductId() + "," +
                        p.getName() + "," +
                        p.getPrice() + "," +
                        p.getProductCount()
                );
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static Basket loadFromTxtFile(File txtFile) {
        List<Product> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
            String str;

            while ((str = reader.readLine()) != null) {
                String[] parts = str.split(",");
                Product product = new Product(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        Integer.parseInt(parts[2])
                );
                product.setProductCount(Integer.parseInt(parts[3]));
                list.add(product);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new Basket(list.toArray(new Product[0]));
    }

    public boolean addToCart(int productNum, int amount) {
        for (Product p : products) {
            if (p.getProductId() == productNum) {
                p.setProductCount(p.getProductCount() + amount);
                return true;
            }
        }
        return false;
    }

    public String printCart() {
        StringBuilder sb = new StringBuilder();
        for (Product p : products) {
            if (p.getProductCount() > 0) {
                sb.append(p);
            }
        }
        return sb.toString();
    }

    public int getCartTotalValue() {
        int cartTotalValue = 0;
        for (Product p : products) {
            cartTotalValue += p.getPrice() * p.getProductCount();
        }
        return cartTotalValue;
    }

    public Product[] getProducts() {
        return products;
    }
}
