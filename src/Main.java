import log.ClientLog;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        Scanner scanner = new Scanner(System.in);

        Product[] products = getProductArray();
        System.out.println("<<<Список возможных товаров для покупки>>>");
        printAvailableProducts(products);

        // Log
        ClientLog clientLog = new ClientLog();
        ClientLog.makeClientLogDir(ClientLog.LOG_DIR_NAME);
        File logFile = new File(ClientLog.LOG_CSV_FILE_NAME);

        // Basket dir & files
        Basket.makeBasketDir(Basket.BASKET_DIR_NAME);
        File basketJson = new File(Basket.BASKET_JSON_BY_GSON_FILE_NAME);
        File basketText = new File(Basket.BASKET_TXT_FILE_NAME);


        // XML
        ConfigXmlReader.makeXmlConfigDir(ConfigXmlReader.XML_CONFIG_DIR_NAME);
        ConfigXmlReader configXmlReader = new ConfigXmlReader();
        File xmlConfigFile = new File(ConfigXmlReader.SHOP_XML_FILE);
        Document doc = configXmlReader.buildDocument(xmlConfigFile);

        HashMap<String, String> loadOptions;
        HashMap<String, String> saveOptions;
        HashMap<String, String> logOptions;

        loadOptions = configXmlReader.getLoadOptions();
        saveOptions = configXmlReader.getSaveOptions();
        logOptions = configXmlReader.getLogOptions();

        Basket basket;

        boolean isLoadEnabled = Boolean.parseBoolean(loadOptions.get(ConfigXmlReader.ENABLED));
        boolean isLoadFormatJson = loadOptions.get(ConfigXmlReader.FORMAT).equals(ConfigXmlReader.FORMAT_JSON);
        boolean isLoadFormatText = loadOptions.get(ConfigXmlReader.FORMAT).equals(ConfigXmlReader.FORMAT_TEXT);

        boolean isSaveEnabled = Boolean.parseBoolean(saveOptions.get(ConfigXmlReader.ENABLED));
        boolean isSaveFormatJson = saveOptions.get(ConfigXmlReader.FORMAT).equals(ConfigXmlReader.FORMAT_JSON);
        boolean isSaveFormatText = saveOptions.get(ConfigXmlReader.FORMAT).equals(ConfigXmlReader.FORMAT_TEXT);

        if (isLoadEnabled) {
            if (basketJson.exists() && isLoadFormatJson) {
                basket = Basket.loadFromJsonFile(basketJson);
                System.out.println("Loaded from basketJson");
            } else if (basketText.exists() && isLoadFormatText) {
                basket = Basket.loadFromTxtFile(basketText);
                System.out.println("Loaded from basketText");
            } else {
                basket = new Basket(products);
            }
        } else {
            basket = new Basket(products);
        }

        while (true) {
            showMenu();
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("log")) {
                System.out.println(clientLog);
                continue;
            }

            if (input.equalsIgnoreCase("txt")) {
                basket.saveTxt(basketText);
                continue;
            }
            if (input.equalsIgnoreCase("jsn")) {
                basket.saveJson(basketJson);
                continue;
            }

            if (input.equalsIgnoreCase("cart")) {
                printTotalCart(basket);
                continue;
            }

            if (input.equalsIgnoreCase("exit")) {
                if (Boolean.parseBoolean(logOptions.get(ConfigXmlReader.ENABLED))) {
                    clientLog.exportAsCSV(new File("./client log/" + logOptions.get(ConfigXmlReader.FILENAME)));
                    System.out.println("Logging enabled, logFile saved");
                }
                printTotalCart(basket);
                break;
            }

            String[] productIdAndCount = input.split(" ");

            if (productIdAndCount.length != 2) {
                System.out.println("Ошибка ввода: не введён номер товара или его количество");
                continue;
            }

            int productId;
            int productCount;
            try {
                productId = Integer.parseInt(productIdAndCount[0]);
                productCount = Integer.parseInt(productIdAndCount[1]);
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод. Введите целыми числами номер товара и его кол-во");
                continue;
            }

            if (productId < 1 || productId > products.length) {
                System.out.println("Неверный номер товара: " + productId);
                continue;
            }

            if (productCount < 1) {
                System.out.println("Некорректное кол-во товара: " + productCount);
                continue;
            }

            addToCartAndPrintResult(basket, productId, productCount);
            clientLog.log(productId, productCount);

            if (isSaveEnabled && isSaveFormatJson) {
                basket.saveJson(basketJson);
                System.out.println("Saving enabled, json saved");
            }
            if (isSaveEnabled && isSaveFormatText) {
                basket.saveTxt(basketText);
                System.out.println("Saving enabled, txt saved");
            }

        }
    }

    private static void showMenu() {
        System.out.print("""
                Введите номер товара и его количество,
                `txt` - для сохранния корзины в файл .txt,
                `jsn` - для сохранния корзины в файл .json,
                `cart` - для вывода итоговой корзины:
                `log` - для вывода журанала покупок
                `exit` - для сохранения log и выхода
                >>>""");
    }

    private static void printTotalCart(Basket basket) {
        System.out.print("Корзина:\n" + basket.printCart());
        System.out.println("Итого: " + basket.getCartTotalValue() + "руб\n");
    }

    private static void addToCartAndPrintResult(Basket basket, int productId, int productCount) {
        if (basket.addToCart(productId, productCount)) {
            System.out.println("Продукт добавлен в корзину\n");
        } else {
            System.out.println("Не получилось добавить продукт\n");
        }
    }

    private static void printAvailableProducts(Product[] products) {
        for (Product product : products) {
            System.out.println(product.getProductId() + ". " +
                    product.getName() + " - " + product.getPrice() + "руб/шт");
        }
    }

    private static Product[] getProductArray() {
        return new Product[]{new Product("Хлеб", 40),
                new Product("Молоко", 90),
                new Product("Гречневая крупа", 70),
                new Product("Бананы", 80),
                new Product("Манная крупа", 60)};
    }


    // метод чтения из лекции, рекурсивно по атрибутам
    private static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                System.out.println("Текущий узел: " + node_.getNodeName());
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();
                for (int a = 0; a < map.getLength(); a++) {
                    String attrName = map.item(a).getNodeName();
                    String attrValue = map.item(a).getNodeValue();
                    System.out.println("Атрибут: " + attrName + "; значение: " + attrValue);
                }
                read(node_);
            }
        }
    }

}