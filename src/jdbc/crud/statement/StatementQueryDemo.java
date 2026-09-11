package src.jdbc.crud.statement;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementQueryDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            StatementDemoSupport.recreateTable(connection);
            StatementDemoSupport.seedRows(connection);
            selectForwardOnly(connection);
            selectScrollable(connection);
            StatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void selectForwardOnly(Connection connection) throws SQLException {
        System.out.println("=== Forward-only ResultSet ===");
        StatementDemoSupport.printRows(
            connection,
            "SELECT id, name, department, salary, hire_date FROM " + StatementDemoSupport.TABLE + " ORDER BY id"
        );
    }

    private static void selectScrollable(Connection connection) throws SQLException {
        System.out.println("=== Scrollable ResultSet ===");
        try (Statement statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(
                 "SELECT id, name FROM " + StatementDemoSupport.TABLE + " ORDER BY id"
             )) {
            if (resultSet.last()) {
                System.out.println("Last row: " + resultSet.getString("name"));
            }
            if (resultSet.absolute(2)) {
                System.out.println("Absolute(2): " + resultSet.getString("name"));
            }
            while (resultSet.previous()) {
                System.out.println("Reverse scan: " + resultSet.getString("name"));
            }
        }
    }
}