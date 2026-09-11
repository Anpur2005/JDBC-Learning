package src.jdbc.transactions;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class TransactionDemoSupport {

    static final String TABLE = "jdbc_accounts";

    private TransactionDemoSupport() {
    }

    static void setup(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " id INT IDENTITY(1,1) PRIMARY KEY," +
                " owner VARCHAR(50) NOT NULL," +
                " balance DECIMAL(12,2) NOT NULL" +
                ")"
            );
            statement.executeUpdate("INSERT INTO " + TABLE + " (owner, balance) VALUES ('Alice', 1000.00)");
            statement.executeUpdate("INSERT INTO " + TABLE + " (owner, balance) VALUES ('Bob', 500.00)");
        }
    }

    static void printBalances(Connection connection, String label) throws SQLException {
        System.out.println(label);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT owner, balance FROM " + TABLE + " ORDER BY id")) {
            while (resultSet.next()) {
                System.out.printf("  %s -> %.2f%n", resultSet.getString("owner"), resultSet.getBigDecimal("balance"));
            }
        }
    }

    static void teardown(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE " + TABLE);
        }
    }
}