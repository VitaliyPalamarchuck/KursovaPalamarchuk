package org.example;

import java.sql.SQLException;
import java.util.Scanner;

public class InputHandler {

    // Метод для створення нової транзакції
    public static Transaction createTransaction(Scanner scanner, int userId) {
        try {
            System.out.println("Тип (1 - Дохід, 2 - Витрата): ");
            int type = scanner.nextInt(); // Зчитування типу транзакції
            System.out.println("Сума: ");
            double amount = scanner.nextDouble(); // Зчитування суми транзакції
            scanner.nextLine(); // Очищення буфера після nextDouble()
            System.out.println("Опис: ");
            String description = scanner.nextLine(); // Зчитування опису транзакції

            // В залежності від вибору створюємо дохід або витрату
            switch (type) {
                case 1:
                    // Створення об'єкта доходу
                    return new Income(amount, description, java.time.LocalDate.now(), userId);
                case 2:
                    // Створення об'єкта витрати
                    return new Expense(amount, description, java.time.LocalDate.now(), userId);
                default:
                    // Якщо вибрано неправильний тип
                    return null;
            }
        } catch (Exception e) {
            // Обробка помилки при введенні даних
            System.out.println("Помилка: " + e.getMessage());
            scanner.nextLine(); // Очищення буфера після помилки
            return null;
        }
    }

    // Метод для авторизації користувача
    public static User login(Scanner scanner, DatabaseManager dbManager) {
        System.out.println("Логін: ");
        String username = scanner.next(); // Зчитування логіну
        System.out.println("Пароль: ");
        String password = scanner.next(); // Зчитування пароля

        try {
            User user = dbManager.readUser(username); // Пошук користувача в базі
            if (user != null && user.authenticate(password)) {
                // Якщо користувач знайдений і пароль правильний
                System.out.println("Вхід успішний!");
                user.getBudget().loadTransactions(user.getId()); // Завантаження транзакцій користувача
                return user;
            }
            System.out.println("Невірний логін або пароль!");
            return null; // Невдала авторизація
        } catch (SQLException e) {
            // Обробка помилки бази даних
            System.out.println("Помилка бази: " + e.getMessage());
            return null;
        }
    }

    // Метод для видалення користувача
    public static void deleteUser(Scanner scanner, DatabaseManager dbManager) {
        System.out.println("Логін користувача для видалення: ");
        String username = scanner.next(); // Зчитування логіну користувача для видалення
        System.out.println("Пароль для підтвердження: ");
        String password = scanner.next(); // Зчитування пароля

        try {
            User user = dbManager.readUser(username); // Пошук користувача в базі
            if (user == null || !user.authenticate(password)) {
                // Якщо користувача не знайдено або неправильний пароль
                System.out.println("Невірний логін або пароль!");
                return;
            }

            System.out.println("Введіть логін користувача для підтвердження видалення: ");
            String confirmUsername = scanner.next(); // Підтвердження логіну
            if (!confirmUsername.equals(username)) {
                // Якщо підтвердження не збігається
                System.out.println("Логін не співпадає! Видалення скасовано.");
                return;
            }

            // Видалення користувача з бази
            dbManager.deleteUser(username);
            System.out.println("Користувача " + username + " та всі його транзакції успішно видалено!");
        } catch (SQLException e) {
            // Обробка помилок бази даних
            System.out.println("Помилка: " + e.getMessage());
        }
    }
}
