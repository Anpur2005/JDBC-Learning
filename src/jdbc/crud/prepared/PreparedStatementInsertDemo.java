package src.jdbc.crud.prepared;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class PreparedStatementInsertDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            PreparedStatementDemoSupport.recreateTable(connection);
            insertWithParameters(connection);
            insertWithNulls(connection);
            insertBatch(connection);
            PreparedStatementDemoSupport.printRows(connection);
            PreparedStatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void insertWithParameters(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE +
            " (name, category, price, in_stock, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Keyboard");
            statement.setString(2, "Electronics");
            statement.setBigDecimal(3, new BigDecimal("89.99"));
            statement.setBoolean(4, true);
            statement.setString(5, "Mechanical keyboard");
            statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            System.out.println("Inserted rows: " + statement.executeUpdate());
        }
        connection.commit();
    }

    private static void insertWithNulls(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE +
            " (name, category, price, in_stock, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Mystery Product");
            statement.setNull(2, Types.VARCHAR);
            statement.setNull(3, Types.DECIMAL);
            statement.setBoolean(4, false);
            statement.setObject(5, null);
            statement.setNull(6, Types.TIMESTAMP);
            statement.executeUpdate();
        }
        connection.commit();
    }

    private static void insertBatch(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE + " (name, category, price, in_stock) VALUES (?, ?, ?, ?)";
        Object[][] rows = {
            {"Mouse", "Electronics", new BigDecimal("49.99"), true},
            {"Desk", "Furniture", new BigDecimal("299.00"), true},
            {"Lamp", "Home", new BigDecimal("39.50"), false}
        };

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Object[] row : rows) {
                statement.setString(1, (String) row[0]);
                statement.setString(2, (String) row[1]);
                statement.setBigDecimal(3, (BigDecimal) row[2]);
                statement.setBoolean(4, (Boolean) row[3]);
                statement.addBatch();
            }
            System.out.println("Batch insert count: " + statement.executeBatch().length);
        }
        connection.commit();
    }
}