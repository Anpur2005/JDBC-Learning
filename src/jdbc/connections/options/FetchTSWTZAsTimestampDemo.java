package src.jdbc.connections.options;

import com.ddtek.jdbcx.sqlserver.SQLServerDataSource;

import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class FetchTSWTZAsTimestampDemo {

    private static final String FETCH_SQL =
        "SELECT CAST('2026-09-08 10:20:30.1234567 +05:30' AS datetimeoffset) AS sample_value";

    public static void main(String[] args) {
        testUrlConnections();
        testPropertyConnections();
        testDataSourceConnections();
    }

    private static void testUrlConnections() {
        System.out.println("\n=== URL Connection Option ===");
        run("false", () -> DriverManager.getConnection(
            SqlServerConnectionConfig.urlWithCredentials() + "FetchTSWTZAsTimestamp=false;"
        ));
        run("true", () -> DriverManager.getConnection(
            SqlServerConnectionConfig.urlWithCredentials() + "FetchTSWTZAsTimestamp=true;"
        ));
    }

    private static void testPropertyConnections() {
        System.out.println("\n=== URL + Properties Connection Option ===");
        run("false", () -> {
            Properties properties = SqlServerConnectionConfig.propertiesWithOverrides(
                new String[][]{{"FetchTSWTZAsTimestamp", "false"}}
            );
            return SqlServerConnectionConfig.openWithProperties(properties);
        });
        run("true", () -> {
            Properties properties = SqlServerConnectionConfig.propertiesWithOverrides(
                new String[][]{{"FetchTSWTZAsTimestamp", "true"}}
            );
            return SqlServerConnectionConfig.openWithProperties(properties);
        });
    }

    private static void testDataSourceConnections() {
        System.out.println("\n=== DataSource Connection Option ===");
        run("false", () -> {
            SQLServerDataSource dataSource = SqlServerConnectionConfig.sqlServerDataSource();
            dataSource.setFetchTSWTZasTimestamp("false");
            return dataSource.getConnection();
        });
        run("true", () -> {
            SQLServerDataSource dataSource = SqlServerConnectionConfig.sqlServerDataSource();
            dataSource.setFetchTSWTZasTimestamp("true");
            return dataSource.getConnection();
        });
    }

    private static void run(String propertyValue, SqlConnectionSupplier supplier) {
        try (Connection connection = supplier.get();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(FETCH_SQL)) {
            resultSet.next();
            Object value = resultSet.getObject(1);
            System.out.println("FetchTSWTZAsTimestamp=" + propertyValue);
            System.out.println("Java type : " + value.getClass().getSimpleName());
            System.out.println("Value     : " + value);
        } catch (Exception exception) {
            System.out.println("FetchTSWTZAsTimestamp=" + propertyValue);
            System.out.println("Failed: " + exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface SqlConnectionSupplier {
        Connection get() throws SQLException;
    }
}