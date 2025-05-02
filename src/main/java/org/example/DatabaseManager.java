package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Константи для підключення до бази даних
    private static final String URL = "jdbc:mysql://localhost:3306/finance_manager"; // URL бази
    private static final String USER = "root"; // Ім'я користувача бази
    private static final String PASSWORD = "12345Vitaliy67890"; // Пароль до бази

    // Метод для отримання з'єднання з базою даних
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Метод для створення нового користувача в базі даних
    public void createUser(String username, String password, String name, double budgetLimit) throws SQLException {
        String sql = "INSERT INTO users (username, password, name, budget_limit, balance) VALUES (?, ?, ?, ?, 0.0)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Підставляємо значення у запит
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);
            stmt.setDouble(4, budgetLimit);
            stmt.executeUpdate(); // Виконуємо запит
        }
    }

    // Метод для зчитування користувача з бази даних за іменем користувача
    public User readUser(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Створюємо об'єкт User на основі даних з бази
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("name"), rs.getDouble("budget_limit"), this);
            }
        }
        return null; // Якщо користувача не знайдено
    }

    // Метод для створення нової транзакції користувача
    public void createTransaction(Transaction transaction) throws SQLException {
        double currentBalance = getCurrentBalance(transaction.getUserId());

        // Якщо це витрата, перевіряємо чи достатньо коштів
        if (transaction.getType().equals("EXPENSE")) {
            double newBalance = currentBalance - transaction.getAmount();
            if (newBalance < 0) {
                throw new SQLException("Помилка: Недостатньо коштів! Баланс: " + currentBalance);
            }
        }

        // Запит на додавання транзакції в базу
        String sql = "INSERT INTO transactions (user_id, amount, description, type, transaction_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getUserId());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getDescription());
            stmt.setString(4, transaction.getType());
            stmt.setDate(5, java.sql.Date.valueOf(transaction.getDate()));
            stmt.executeUpdate(); // Виконуємо запит

            // Після створення транзакції оновлюємо баланс користувача
            double amount = transaction.getType().equals("INCOME") ? transaction.getAmount() : -transaction.getAmount();
            updateBalance(transaction.getUserId(), amount);
        }
    }

    // Метод для зчитування списку всіх транзакцій користувача
    public List<Transaction> readTransactions(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Якщо тип INCOME - створюємо об'єкт Income, інакше Expense
                Transaction t = rs.getString("type").equals("INCOME") ?
                        new Income(rs.getDouble("amount"), rs.getString("description"),
                                rs.getDate("transaction_date").toLocalDate(), userId) :
                        new Expense(rs.getDouble("amount"), rs.getString("description"),
                                rs.getDate("transaction_date").toLocalDate(), userId);
                t.setId(rs.getInt("id")); // Встановлюємо ID транзакції
                transactions.add(t); // Додаємо в список
            }
        }
        return transactions;
    }

    // Приватний метод для оновлення балансу користувача
    private void updateBalance(int userId, double amount) throws SQLException {
        String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount); // Скільки додати або відняти
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // Метод для оновлення ліміту користувача
    public void updateUserLimit(int userId, double limit) throws SQLException {
        String sql = "UPDATE users SET budget_limit = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, limit); // Нове значення ліміту
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // Метод для отримання поточного балансу користувача
    public double getCurrentBalance(int userId) throws SQLException {
        String sql = "SELECT balance FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance"); // Повертаємо баланс
            }
        }
        return 0.0; // Якщо користувача не знайдено або помилка
    }

    // Метод для видалення користувача з бази даних
    public void deleteUser(String username) throws SQLException {
        User user = readUser(username); // Спочатку шукаємо користувача
        if (user == null) {
            throw new SQLException("Користувача з логіном " + username + " не знайдено!");
        }
        int userId = user.getId();

        // Видаляємо всі транзакції користувача
        String transactionSql = "DELETE FROM transactions WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(transactionSql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }

        // Видаляємо самого користувача
        String userSql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
}
