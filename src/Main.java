import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        Product[] products = getProductArray();

        System.out.println("<<<Список возможных товаров для покупки>>>");
        printAvailableProducts(products);

        File basketDir = new File(Basket.BASKET_BIN_DIR_NAME);
        try {
            basketDir.mkdir();
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        }
        File basketFile = new File(Basket.BASKET_BIN_FILE_NAME);

        Basket basket;
        if (!basketFile.createNewFile()) {
            basket = Basket.loadFromBinFile(basketFile);
        } else {
            basket = new Basket(products);
        }

        while (true) {
            System.out.print("Введите номер товара и его количество через пробел, " +
                    "\n`save` - для сохранния корзины в файл и выхода," +
                    "\n`del` - для удаления файла и очищения корзины," +
                    "\n`cart` - для вывода итоговой корзины:\n>>");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("save")) {
                basket.saveBin(basketFile);
                break;
            }

            if (input.equalsIgnoreCase("del")) {
                try {
                    basketFile.delete();
                }catch (SecurityException e) {
                    System.out.println(e.getMessage());
                }
                basket = new Basket(products);
                continue;
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

    private static Product[] getProductArray() {
        return new Product[]{new Product("Хлеб", 40),
                new Product("Молоко", 90),
                new Product("Гречневая крупа", 70),
                new Product("Бананы", 80),
                new Product("Манная крупа", 60)};
    }
}