package org.example;

import java.time.LocalDate;

// Клас Expense успадковує від Transaction і представляє витрати
public class Expense extends Transaction {

    // Конструктор класу, що передає значення в конструктор батьківського класу Transaction
    public Expense(double amount, String description, LocalDate date, int userId) {
        super(amount, description, date, userId);  // Викликаємо конструктор батьківського класу
    }

    // Переозначуємо метод getType для повернення типу транзакції
    @Override
    String getType() {
        return "EXPENSE";  // Тип транзакції - витрати
    }
}
