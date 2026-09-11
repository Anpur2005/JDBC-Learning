package src.jdbc.crud.prepared;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;

public class PreparedStatementAdvancedBindingDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            PreparedStatementDemoSupport.recreateTable(connection);
            demonstrateMultipleSetters(connection);
            demonstrateGeneratedKeys(connection);
            PreparedStatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void demonstrateMultipleSetters(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE +
            " (name, category, price, in_stock, description, created_at, image_data) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Tablet");
            statement.setNString(2, "Electronics");
            statement.setBigDecimal(3, new BigDecimal("799.90"));
            statement.setBoolean(4, true);
            statement.setCharacterStream(5, new StringReader("Large description written via stream"));
            statement.setTimestamp(6, Timestamp.valueOf("2024-06-15 10:30:00"));
            statement.setBinaryStream(7, new ByteArrayInputStream(new byte[]{1, 2, 3, 4}));
            statement.executeUpdate();

            statement.clearParameters();
            statement.setString(1, "Clock");
            statement.setString(2, "Home");
            statement.setObject(3, new BigDecimal("35.75"), Types.DECIMAL);
            statement.setBoolean(4, false);
            statement.setString(5, "Uses setObject and standard setters together");
            statement.setObject(6, Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)));
            statement.setBytes(7, new byte[]{10, 20, 30});
            statement.executeUpdate();
        }
        connection.commit();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT name, created_at FROM " + PreparedStatementDemoSupport.TABLE + " ORDER BY id"
             );
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                System.out.println("Bound row -> " + resultSet.getString("name") + " at " + resultSet.getTimestamp("created_at"));
            }
        }

        Time sampleTime = Time.valueOf(LocalTime.of(9, 15));
        System.out.println("Additional setter example with Time: " + sampleTime);
    }

    private static void demonstrateGeneratedKeys(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE + " (name, category, price, in_stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Camera");
            statement.setString(2, "Electronics");
            statement.setDouble(3, 549.00);
            statement.setBoolean(4, true);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                while (keys.next()) {
                    System.out.println("PreparedStatement generated id: " + keys.getInt(1));
                }
            }
        }
        connection.commit();
    }
}