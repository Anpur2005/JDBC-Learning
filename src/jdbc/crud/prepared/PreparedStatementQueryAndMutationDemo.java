package src.jdbc.crud.prepared;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedStatementQueryAndMutationDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            PreparedStatementDemoSupport.recreateTable(connection);
            PreparedStatementDemoSupport.seedRows(connection);
            selectWithParameters(connection);
            updateWithParameters(connection);
            deleteWithParameters(connection);
            demonstrateReuseAndClearParameters(connection);
            PreparedStatementDemoSupport.printRows(connection);
            PreparedStatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void selectWithParameters(Connection connection) throws SQLException {
        String sql = "SELECT id, name, price FROM " + PreparedStatementDemoSupport.TABLE + " WHERE category = ? AND price < ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Electronics");
            statement.setDouble(2, 1000.00);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.printf("  Query match -> [%d] %s %.2f%n",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"));
                }
            }
        }
    }

    private static void updateWithParameters(Connection connection) throws SQLException {
        String sql = "UPDATE " + PreparedStatementDemoSupport.TABLE + " SET price = price * ? WHERE category = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, 1.10);
            statement.setString(2, "Electronics");
            System.out.println("Updated rows: " + statement.executeUpdate());
        }
        connection.commit();
    }

    private static void deleteWithParameters(Connection connection) throws SQLException {
        String sql = "DELETE FROM " + PreparedStatementDemoSupport.TABLE + " WHERE in_stock = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, false);
            System.out.println("Deleted rows: " + statement.executeUpdate());
        }
        connection.commit();
    }

    private static void demonstrateReuseAndClearParameters(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE + " (name, category, price, in_stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Webcam");
            statement.setString(2, "Electronics");
            statement.setDouble(3, 199.00);
            statement.setBoolean(4, true);
            statement.executeUpdate();

            statement.clearParameters();
            statement.setString(1, "Notebook");
            statement.setString(2, "Office");
            statement.setDouble(3, 12.50);
            statement.setBoolean(4, true);
            statement.executeUpdate();
        }
        connection.commit();
    }
}