import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        Product[] products = {
                new Product("Хлеб", 40),
                new Product("Молоко", 90),
                new Product("Гречневая крупа", 70),
                new Product("Бананы", 80),
                new Product("Манная крупа", 60)};

        System.out.println("<<<Список возможных товаров для покупки>>>");
        printAvailableProducts(products);

        File basketFile = new File("basket.txt");

        Basket basket;
        if (basketFile.exists()) {
            basket = Basket.loadFromTxtFile(basketFile);
        } else {
            basket = new Basket(products);
        }

        while (true) {
            System.out.print("Введите номер товара и его количество, " +
                    "\n`save` - для сохранния корзины в файл и выхода,\n" +
                    "`cart` - для вывода итоговой корзины:\n>>");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("save")) {
                basket.saveTxt(basketFile);
                break;
            }

            if (input.equalsIgnoreCase("cart")) {
                System.out.print("Корзина:\n" + basket.printCart());
                System.out.println("Итого: " + basket.getCartTotalValue() + "руб\n");
                continue;
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

            addToCart(basket, productId, productCount);

        }
    }

    private static void addToCart(Basket basket, int productId, int productCount) {
        if (basket.addToCart(productId, productCount)) {
            System.out.println("Продукт добавлен в корзину\n");
        } else {
            System.out.println("Не получилось добавить продукт\n");
        }
    }

    private static void printAvailableProducts(Product[] products) {
        for (int i = 0; i < products.length; i++) {
            System.out.println(products[i].getProductId() + ". " +
                    products[i].getName() + " - " + products[i].getPrice() + "руб/шт");
        }
    }
}