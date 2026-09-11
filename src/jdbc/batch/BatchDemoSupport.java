package src.jdbc.batch;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class BatchDemoSupport {

    static final String TABLE = "jdbc_batch_items";

    private BatchDemoSupport() {
    }

    static void setup(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " id INT IDENTITY(1,1) PRIMARY KEY," +
                " code VARCHAR(10) NOT NULL UNIQUE," +
                " value DECIMAL(10,2)" +
                ")"
            );
        }
    }

    static void printRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT code, value FROM " + TABLE + " ORDER BY id")) {
            while (resultSet.next()) {
                System.out.printf("  %s -> %.2f%n", resultSet.getString("code"), resultSet.getBigDecimal("value"));
            }
        }
    }

    static void teardown(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE " + TABLE);
        }
    }
}