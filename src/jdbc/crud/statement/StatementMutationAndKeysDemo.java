package src.jdbc.crud.statement;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementMutationAndKeysDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            StatementDemoSupport.recreateTable(connection);
            StatementDemoSupport.seedRows(connection);
            updateRows(connection);
            deleteRows(connection);
            insertWithGeneratedKeys(connection);
            StatementDemoSupport.printRows(connection, "SELECT * FROM " + StatementDemoSupport.TABLE + " ORDER BY id");
            StatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void updateRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(
                "UPDATE " + StatementDemoSupport.TABLE + " SET salary = salary + 5000 WHERE department = 'Engineering'"
            );
            System.out.println("Updated rows: " + rows);
        }
        connection.commit();
    }

    private static void deleteRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(
                "DELETE FROM " + StatementDemoSupport.TABLE + " WHERE department = 'HR'"
            );
            System.out.println("Deleted rows: " + rows);
        }
        connection.commit();
    }

    private static void insertWithGeneratedKeys(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(
                "INSERT INTO " + StatementDemoSupport.TABLE + " (name, department, salary, hire_date) VALUES ('Ivy', 'Sales', 67000.00, '2024-01-05')",
                Statement.RETURN_GENERATED_KEYS
            );
            System.out.println("Generated-key insert count: " + rows);
            try (ResultSet keys = statement.getGeneratedKeys()) {
                while (keys.next()) {
                    System.out.println("Generated id: " + keys.getInt(1));
                }
            }
        }
        connection.commit();
    }
}