
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Basket {

    public static final String BASKET_DIR_NAME = "./basket";
    public static final String BASKET_TXT_FILE_NAME = "./basket/basket.txt";
    public static final String BASKET_JSON_FILE_NAME = "./basket/basket.json";
    public static final String PRODUCTS = "products";
    private Product[] products;

    public Basket(Product[] products) {
        this.products = products;
    }

    public void saveJson(File jsonFile) {
        JSONObject basketObj = basketToJSONObject();
        try (FileWriter writer  = new FileWriter(jsonFile)){
            writer.write(basketObj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Product[] loadFromJsonFile(File jsonFile) {
        JSONObject jsonObject = new JSONObject();
        JSONParser parser = new JSONParser();

        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(jsonFile));


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = (JSONArray) jsonObject.get(PRODUCTS);

        Product[] productsArray = new Product[jsonArray.size()];

        try {
            for (int i =0; i< jsonArray.size(); i++) {

                JSONObject prodObj = (JSONObject) jsonArray.get(i);

                Product product = ProductService.fromJSONObject(prodObj);
                productsArray[i] = product;
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return productsArray;
    }

    public JSONObject basketToJSONObject() {
        JSONObject basketObj = new JSONObject();
        JSONArray productsArray = productsToJSONOArray();
        basketObj.put(PRODUCTS, productsArray);
        return basketObj;
    }

    private JSONArray productsToJSONOArray() {
        JSONArray productsArray = new JSONArray();
        for (Product product : products) {
            productsArray.add(ProductService.toJSONObject(product));
        }
        return productsArray;
    }

    public void saveTxt(File txtFile) {
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
            e.printStackTrace();
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

    public static void makeBasketDir(String dirName) {
        File dir = new File(dirName);
        try {
            dir.mkdir();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
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
