package src.jdbc.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbcDemoSupport {

    private JdbcDemoSupport() {
    }

    public static void printConnectionSummary(Connection connection) throws SQLException {
        // System.out.println("URL               : " + connection.getMetaData().getURL());
        System.out.println("Driver            : " + connection.getMetaData().getDriverName());
        System.out.println("Driver Version    : " + connection.getMetaData().getDriverVersion());
        System.out.println("Database Product  : " + connection.getMetaData().getDatabaseProductName());
        System.out.println("Database Version  : " + connection.getMetaData().getDatabaseProductVersion());
        System.out.println("Auto Commit       : " + connection.getAutoCommit());
        System.out.println("Transaction Level : " + connection.getTransactionIsolation());
    }

    public static void printTables(ResultSet tables) throws SQLException {
        while (tables.next()) {
            System.out.println("  - " + tables.getString("TABLE_NAME"));
        }
    }

    public static void printSqlException(SQLException exception) {
        SQLException current = exception;
        while (current != null) {
            System.out.println("Message   : " + current.getMessage());
            System.out.println("SQLState  : " + current.getSQLState());
            System.out.println("ErrorCode : " + current.getErrorCode());
            current = current.getNextException();
            if (current != null) {
                System.out.println();
            }
        }
    }

    public static void printResultSetShape(ResultSetMetaData metaData) throws SQLException {
        for (int index = 1; index <= metaData.getColumnCount(); index++) {
            System.out.printf("  [%d] %s (%s)%n", index, metaData.getColumnName(index), metaData.getColumnTypeName(index));
        }
    }

    public static void executeIgnoringFailure(Connection connection, String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
    }
}