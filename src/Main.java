import log.ClientLog;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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
        File basketJson = new File(Basket.BASKET_JSON_FILE_NAME);
        File basketFile = new File(Basket.BASKET_TXT_FILE_NAME);

        // XML
        ShopXmlReader.makeXmlConfigDir(ShopXmlReader.XML_CONFIG_DIR_NAME);
        ShopXmlReader shopReader = new ShopXmlReader();
        File xmlFile = new File(ShopXmlReader.SHOP_XML_FILE);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("shop.xml"));
        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент: " + root.getNodeName());
        read(root);

        if (xmlFile.exists()) {
            Document doc1 = shopReader.buildDocument(xmlFile);

            Node rootNode = shopReader.getRootNode();

            read(doc1.getDocumentElement());
            String attr = shopReader.getAttrVal(rootNode, "load", "enabled");


        }

        Basket basket;
        if (basketJson.exists()) {
            basket = new Basket(Basket.loadFromJsonFile(basketJson));
        } else if (basketFile.exists()) {
            basket = Basket.loadFromTxtFile(basketFile);
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
                basket.saveTxt(basketFile);
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
                clientLog.exportAsCSV(logFile);
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