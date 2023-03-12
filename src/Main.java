import log.ClientLog;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        Product[] products = getProductArray();
        System.out.println("<<<Список возможных товаров для покупки>>>");
        printAvailableProducts(products);

        ClientLog clientLog = new ClientLog();
        ClientLog.makeClientLogDir(ClientLog.LOG_DIR_NAME);
        File logFile = new File(ClientLog.LOG_CSV_FILE_NAME);

        Basket.makeBasketDir(Basket.BASKET_DIR_NAME);
        File basketFile = new File(Basket.BASKET_TXT_FILE_NAME);
        Basket basket;
        if (basketFile.exists()) {
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

            addToCartAndPrintResult(basket,productId, productCount);
            clientLog.log(productId, productCount);

        }
    }

//    private static void inputChecks(String input, Basket basket, ClientLog clientLog) {
//
//        if (input.equalsIgnoreCase("log")) {
//            System.out.println(clientLog);
//            continue;
//        }
//
//        if (input.equalsIgnoreCase("txt")) {
//            basket.saveTxt(basketFile);
//            continue;
//        }
//
//        if (input.equalsIgnoreCase("cart")) {
//            printTotalCart(basket);
//            continue;
//        }
//
//        if (input.equalsIgnoreCase("exit")) {
//            printTotalCart(basket);
//            break;
//        }
//    }

    private static void showMenu() {
        System.out.print("""
                Введите номер товара и его количество,
                `txt` - для сохранния корзины в файл .txt,
                `cart` - для вывода итоговой корзины:
                `log` - для вывода журанала покупок
                `exit` - для выхода
                >>>""");
    }

    private static void printTotalCart(Basket basket){
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
}