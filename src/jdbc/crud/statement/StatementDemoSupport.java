package src.jdbc.crud.statement;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class StatementDemoSupport {

    static final String TABLE = "jdbc_employees";

    private StatementDemoSupport() {
    }

    static void recreateTable(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " id INT IDENTITY(1,1) PRIMARY KEY," +
                " name VARCHAR(100) NOT NULL," +
                " department VARCHAR(50)," +
                " salary DECIMAL(10,2)," +
                " hire_date DATE" +
                ")"
            );
        }
        connection.commit();
    }

    static void seedRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                "INSERT INTO " + TABLE + " (name, department, salary, hire_date) VALUES " +
                "('Alice', 'Engineering', 95000.00, '2021-03-15')," +
                "('Bob', 'Marketing', 72000.00, '2022-01-10')," +
                "('Carol', 'Engineering', 88000.00, '2022-03-01')," +
                "('Dave', 'HR', 65000.00, '2022-06-15')"
            );
        }
        connection.commit();
    }

    static void printRows(Connection connection, String query) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.printf(
                    "  [%d] %-8s %-12s %8.2f %s%n",
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("department"),
                    resultSet.getDouble("salary"),
                    resultSet.getDate("hire_date")
                );
            }
        }
    }

    static void dropTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE " + TABLE);
        }
        connection.commit();
    }
}