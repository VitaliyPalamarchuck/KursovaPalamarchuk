package org.example;

import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Клас Budget реалізує інтерфейси Transactionable та Reportable
@Getter
@Setter
public class Budget implements Transactionable, Reportable {
    private double limit;  // Ліміт витрат
    private double balance;  // Поточний баланс
    private List<Transaction> transactions;  // Список транзакцій
    private DatabaseManager dbManager;  // Клас для роботи з базою даних

    // Конструктор класу Budget
    public Budget(double limit, double balance, DatabaseManager dbManager) {
        this.limit = limit;
        this.balance = balance;
        this.transactions = new ArrayList<>();  // Ініціалізуємо список транзакцій
        this.dbManager = dbManager;
    }

    // Метод для встановлення нового ліміту витрат для користувача
    public void setLimit(double limit, int userId) {
        this.limit = limit;  // Оновлюємо ліміт
        try {
            dbManager.updateUserLimit(userId, limit);  // Оновлюємо ліміт у базі даних
        } catch (SQLException e) {
            System.out.println("Помилка оновлення ліміту в базі: " + e.getMessage());
        }
    }

    // Метод для отримання поточного балансу користувача з бази даних
    public double getCurrentBalance(int userId) {
        try {
            return dbManager.getCurrentBalance(userId);  // Отримуємо баланс з бази даних
        } catch (SQLException e) {
            System.out.println("Помилка бази: " + e.getMessage());
            return balance;  // Повертаємо локальний баланс, якщо сталася помилка
        }
    }

    // Реалізація методу addTransaction з інтерфейсу Transactionable
    @Override
    public void addTransaction(Transaction transaction) {
        try {
            double currentBalance = getCurrentBalance(transaction.getUserId());  // Отримуємо поточний баланс
            if (transaction.getType().equals("EXPENSE")) {  // Якщо транзакція - витрата
                // Перевіряємо, чи є достатньо коштів на балансі для цієї витрати
                Predicate<Double> hasEnoughFunds = amount -> currentBalance - amount >= 0;
                // Перевіряємо, чи не перевищує витрата ліміт
                Predicate<Double> withinLimit = amount -> amount <= limit;

                if (!hasEnoughFunds.test(transaction.getAmount())) {  // Якщо недостатньо коштів
                    System.out.println("Помилка: Немає достатньо коштів! Баланс: " + currentBalance);
                    return;
                }
                if (!withinLimit.test(transaction.getAmount())) {  // Якщо витрата перевищує ліміт
                    System.out.println("Помилка: Витрата перевищує ліміт витрат! Ліміт: " + limit);
                    return;
                }
            }
            transactions.add(transaction);  // Додаємо транзакцію до списку
            dbManager.createTransaction(transaction);  // Додаємо транзакцію в базу даних
            balance = getCurrentBalance(transaction.getUserId());  // Оновлюємо баланс
            System.out.println("Транзакція успішно додана!");
        } catch (SQLException e) {
            System.out.println("Помилка бази: " + e.getMessage());
        }
    }

    // Реалізація методу generateReport з інтерфейсу Reportable
    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder("Звіт:\n");
        report.append("Ліміт: ").append(limit).append("\n");  // Виводимо ліміт
        report.append("Баланс: ").append(balance).append("\n");  // Виводимо баланс
        if (balance <= 100) {  // Якщо баланс менший або рівний 100
            report.append("Попередження: Баланс близький до нуля!\n");
        }

        // Підраховуємо загальний дохід
        double incomeTotal = transactions.stream()
                .filter(t -> t.getType().equals("INCOME"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        // Підраховуємо загальні витрати
        double expenseTotal = transactions.stream()
                .filter(t -> t.getType().equals("EXPENSE"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        report.append("Дохід: ").append(incomeTotal).append("\n");  // Виводимо дохід
        report.append("Витрати: ").append(expenseTotal).append("\n");  // Виводимо витрати
        return report.toString();
    }

    // Метод для завантаження транзакцій користувача з бази даних
    public void loadTransactions(int userId) {
        try {
            transactions = dbManager.readTransactions(userId);  // Завантажуємо транзакції з бази даних
            balance = getCurrentBalance(userId);  // Оновлюємо баланс користувача
        } catch (SQLException e) {
            System.out.println("Помилка бази: " + e.getMessage());
        }
    }
}
