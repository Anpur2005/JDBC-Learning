package src.jdbc.crud.statement;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementAdvancedFeaturesDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            StatementDemoSupport.recreateTable(connection);
            StatementDemoSupport.seedRows(connection);
            demonstrateExecute(connection);
            demonstrateHints(connection);
            demonstrateUpdatableResultSet(connection);
            StatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void demonstrateExecute(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            boolean hasResultSet = statement.execute("SELECT COUNT(*) AS employee_count FROM " + StatementDemoSupport.TABLE);
            if (hasResultSet) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    resultSet.next();
                    System.out.println("Employee count: " + resultSet.getInt("employee_count"));
                }
            }
        }
    }

    private static void demonstrateHints(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.setMaxRows(2);
            statement.setFetchSize(2);
            statement.setFetchDirection(ResultSet.FETCH_FORWARD);
            statement.setQueryTimeout(5);
            try (ResultSet resultSet = statement.executeQuery(
                "SELECT id, name, department, salary, hire_date FROM " + StatementDemoSupport.TABLE + " ORDER BY id"
            )) {
                System.out.println("Statement hints limited result:");
                while (resultSet.next()) {
                    System.out.println("  " + resultSet.getString("name"));
                }
            }
        }
    }

    private static void demonstrateUpdatableResultSet(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
             ResultSet resultSet = statement.executeQuery(
                 "SELECT id, name, department, salary, hire_date FROM " + StatementDemoSupport.TABLE
             )) {
            if (resultSet.first()) {
                resultSet.updateDouble("salary", resultSet.getDouble("salary") + 2500);
                resultSet.updateRow();
            }

            resultSet.moveToInsertRow();
            resultSet.updateString("name", "Jack");
            resultSet.updateString("department", "Operations");
            resultSet.updateDouble("salary", 59000.00);
            resultSet.updateDate("hire_date", java.sql.Date.valueOf("2024-02-01"));
            resultSet.insertRow();
        }
        connection.commit();
        System.out.println("Updatable ResultSet demo complete.");
        StatementDemoSupport.printRows(
            connection,
            "SELECT id, name, department, salary, hire_date FROM " + StatementDemoSupport.TABLE + " ORDER BY id"
        );
    }
}