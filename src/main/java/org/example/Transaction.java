package org.example;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public abstract class Transaction {
    private int id; // Унікальний ідентифікатор транзакції (з бази даних)
    private double amount; // Сума транзакції
    private String description; // Опис транзакції (наприклад, "зарплата", "покупка продуктів")
    private LocalDate date; // Дата транзакції
    private int userId; // Ідентифікатор користувача, якому належить транзакція

    // Конструктор для ініціалізації основних полів транзакції
    public Transaction(double amount, String description, LocalDate date, int userId) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.userId = userId;
    }

    // Абстрактний метод для отримання типу транзакції
    // Реалізується у підкласах (наприклад, "INCOME" або "EXPENSE")
    abstract String getType();
}
