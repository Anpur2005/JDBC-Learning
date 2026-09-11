package src.jdbc.crud.statement;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementCreateAndBatchDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            StatementDemoSupport.recreateTable(connection);
            insertSingleRow(connection);
            insertWithBatch(connection);
            StatementDemoSupport.printRows(connection, "SELECT * FROM " + StatementDemoSupport.TABLE + " ORDER BY id");
            StatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void insertSingleRow(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(
                "INSERT INTO " + StatementDemoSupport.TABLE + " (name, department, salary, hire_date) " +
                "VALUES ('Eve', 'Finance', 83000.00, '2023-04-20')"
            );
            System.out.println("Single-row insert count: " + rows);
        }
        connection.commit();
    }

    private static void insertWithBatch(Connection connection) throws SQLException {
        String[] statements = {
            "INSERT INTO " + StatementDemoSupport.TABLE + " (name, department, salary, hire_date) VALUES ('Frank', 'Engineering', 99000.00, '2023-07-01')",
            "INSERT INTO " + StatementDemoSupport.TABLE + " (name, department, salary, hire_date) VALUES ('Grace', 'Support', 61000.00, '2023-08-15')",
            "INSERT INTO " + StatementDemoSupport.TABLE + " (name, department, salary, hire_date) VALUES ('Henry', 'HR', 58000.00, '2023-09-01')"
        };

        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.addBatch(sql);
            }
            int[] counts = statement.executeBatch();
            System.out.println("Batch statement count: " + counts.length);
        }
        connection.commit();
    }
}