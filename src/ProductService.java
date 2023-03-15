import org.json.simple.JSONObject;

public class ProductService {
    private static final String PRODUCT_ID = "productId";
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String PRODUCT_COUNT = "productCount";

    public static JSONObject toJSONObject(Product product) {
        String productId = String.valueOf(product.getProductId());
        String name = product.getName();
        String price = String.valueOf(product.getPrice());
        String productCount = String.valueOf(product.getProductCount());

        JSONObject productObj = new JSONObject();
        productObj.put(PRODUCT_ID, productId);
        productObj.put(NAME, name);
        productObj.put(PRICE, price);
        productObj.put(PRODUCT_COUNT, productCount);
        return productObj;
    }

    public static Product fromJSONObject(JSONObject object) {

        String productId = (String) object.get(PRODUCT_ID);
        String name = (String) object.get(NAME);
        String price= (String) object.get(PRICE);
        String productCount = (String) object.get(PRODUCT_COUNT);

        Product product = new Product(
                Integer.parseInt(productId),
                name,
                Integer.parseInt(price)
                );
        product.setProductCount(Integer.parseInt(productCount));
        return product;
    }
}
