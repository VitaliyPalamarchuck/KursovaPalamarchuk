package org.example;

// Інтерфейс для об'єктів, які можуть додавати транзакції
public interface Transactionable {

    // Метод для додавання транзакції
    // Реалізація буде в класах, які імплементують цей інтерфейс
    void addTransaction(Transaction transaction);
}
