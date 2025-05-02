package org.example;

import java.time.LocalDate;

// Клас Income успадковує від Transaction і представляє дохід
public class Income extends Transaction {

    // Конструктор класу, що передає значення в конструктор батьківського класу Transaction
    public Income(double amount, String description, LocalDate date, int userId) {
        super(amount, description, date, userId);  // Викликаємо конструктор батьківського класу
    }

    // Переозначуємо метод getType для повернення типу транзакції
    @Override
    String getType() {
        return "INCOME";  // Тип транзакції - дохід
    }
}
