package org.example;

import java.sql.SQLException;
import java.util.Scanner;

public class FinanceManager {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Об'єкт для зчитування введення користувача
        DatabaseManager dbManager = new DatabaseManager(); // Робота з базою даних


        // Основний цикл програми
        while (true) {
            // Головне меню
            System.out.println("ГОЛОВНЕ МЕНЮ");
            System.out.println("1. Вхід\n2. Реєстрація\n3. Видалити користувача\n4. Вихід");
            try {
                int choice = scanner.nextInt(); // Зчитування вибору користувача
                scanner.nextLine(); // Очищення буфера

                if (choice == 1) {
                    // Вхід користувача
                    User user = InputHandler.login(scanner, dbManager);
                    if (user != null) {
                        // Після успішного входу відкривається меню користувача
                        while (true) {
                            System.out.println("1. Додати транзакцію\n2. Звіт\n3. Встановити ліміт\n4. Вийти");
                            try {
                                int userChoice = scanner.nextInt();
                                scanner.nextLine(); // Очищення буфера

                                if (userChoice == 1) {
                                    // Створення нової транзакції
                                    Transaction transaction = InputHandler.createTransaction(scanner, user.getId());
                                    if (transaction != null) {
                                        user.addTransaction(transaction); // Додавання транзакції до бюджету
                                    }
                                } else if (userChoice == 2) {
                                    // Генерація та виведення фінансового звіту
                                    System.out.println(user.getBudget().generateReport());
                                } else if (userChoice == 3) {
                                    // Зміна ліміту бюджету
                                    System.out.println("Введіть новий ліміт витрат: ");
                                    double limit = scanner.nextDouble();
                                    user.getBudget().setLimit(limit, user.getId());
                                    System.out.println("Новий ліміт витрат встановлено: " + limit);
                                } else if (userChoice == 4) {
                                    // Вихід до головного меню
                                    break;
                                } else {
                                    System.out.println("Невірний вибір!");
                                }
                            } catch (Exception e) {
                                // Обробка помилок вводу користувача
                                System.out.println("Помилка: " + e.getMessage());
                                scanner.nextLine(); // Очищення буфера після помилки
                            }
                        }
                    }
                } else if (choice == 2) {
                    // Реєстрація нового користувача
                    System.out.println("Введіть логін: ");
                    String username = scanner.nextLine();
                    System.out.println("Введіть пароль: ");
                    String password = scanner.nextLine();
                    System.out.println("Введіть ім'я: ");
                    String name = scanner.nextLine();
                    System.out.println("Введіть ліміт витрат бюджету: ");
                    double limit = scanner.nextDouble();
                    try {
                        dbManager.createUser(username, password, name, limit); // Створення користувача в базі
                        System.out.println("Реєстрація успішна!");
                    } catch (SQLException e) {
                        // Обробка помилок бази даних при реєстрації
                        System.out.println("Помилка бази даних: " + e.getMessage());
                    }
                } else if (choice == 3) {
                    // Видалення користувача
                    InputHandler.deleteUser(scanner, dbManager);
                } else if (choice == 4) {
                    // Завершення роботи програми
                    System.out.println("Програма завершена!");
                    break;
                } else {
                    // Якщо вибір не відповідає жодному пункту меню
                    System.out.println("Невірний вибір!");
                }
            } catch (Exception e) {
                // Обробка загальних помилок вводу
                System.out.println("Помилка: " + e.getMessage());
                scanner.nextLine(); // Очищення буфера після помилки
            }
        }

        scanner.close(); // Закриваємо Scanner перед виходом
    }
}
