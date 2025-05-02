package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private int id; // Унікальний ідентифікатор користувача
    private String username; // Логін користувача
    private String password; // Пароль користувача
    private String name; // Ім'я користувача
    private Budget budget; // Об'єкт бюджету, пов'язаний із користувачем

    // Конструктор для ініціалізації користувача
    public User(int id, String username, String password, String name, double budgetLimit, DatabaseManager dbManager) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.budget = new Budget(budgetLimit, 0.0, dbManager); // Створюємо новий бюджет для користувача
    }

    // Метод для перевірки правильності введеного пароля
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    // Метод для додавання транзакції в бюджет користувача
    public void addTransaction(Transaction transaction) {
        budget.addTransaction(transaction);
    }
}
