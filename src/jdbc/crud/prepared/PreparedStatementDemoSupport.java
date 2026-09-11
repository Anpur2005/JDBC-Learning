package src.jdbc.crud.prepared;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class PreparedStatementDemoSupport {

    static final String TABLE = "jdbc_products";

    private PreparedStatementDemoSupport() {
    }

    static void recreateTable(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " id INT IDENTITY(1,1) PRIMARY KEY," +
                " name VARCHAR(100) NOT NULL," +
                " category VARCHAR(50)," +
                " price DECIMAL(10,2)," +
                " in_stock BIT," +
                " description NVARCHAR(MAX)," +
                " created_at DATETIME2," +
                " image_data VARBINARY(MAX)" +
                ")"
            );
        }
        connection.commit();
    }

    static void seedRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                "INSERT INTO " + TABLE + " (name, category, price, in_stock, description, created_at) VALUES " +
                "('Laptop Pro', 'Electronics', 1299.99, 1, 'Workstation laptop', GETDATE())," +
                "('Monitor', 'Electronics', 499.00, 1, '4K monitor', GETDATE())," +
                "('Desk Chair', 'Furniture', 349.00, 0, 'Mesh chair', GETDATE())"
            );
        }
        connection.commit();
    }

    static void printRows(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT id, name, category, price, in_stock FROM " + TABLE + " ORDER BY id"
             )) {
            while (resultSet.next()) {
                System.out.printf(
                    "  [%d] %-15s %-12s %8.2f inStock=%s%n",
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("category"),
                    resultSet.getDouble("price"),
                    resultSet.getBoolean("in_stock")
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