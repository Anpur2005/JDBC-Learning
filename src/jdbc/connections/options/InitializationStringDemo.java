package src.jdbc.connections.options;

import com.ddtek.jdbcx.sqlserver.SQLServerDataSource;

import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class InitializationStringDemo {

    private static final String INITIALIZATION_STRING = "set quoted_identifier off";
    private static final String CHECK_SQL =
        "SELECT SESSIONPROPERTY('QUOTED_IDENTIFIER') AS quoted_identifier_value";

    public static void main(String[] args) {
        testUrlConnections();
        testPropertyConnections();
        testDataSourceConnections();
    }

    private static void testUrlConnections() {
        System.out.println("\n=== URL InitializationString ===");
        run("not set", () -> DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials()));
        run("set", () -> DriverManager.getConnection(
            SqlServerConnectionConfig.urlWithCredentials() + "InitializationString=" + INITIALIZATION_STRING + ";"
        ));
    }

    private static void testPropertyConnections() {
        System.out.println("\n=== URL + Properties InitializationString ===");
        run("not set", () -> SqlServerConnectionConfig.openWithProperties(SqlServerConnectionConfig.credentialsProperties()));
        run("set", () -> {
            Properties properties = SqlServerConnectionConfig.propertiesWithOverrides(
                new String[][]{{"InitializationString", INITIALIZATION_STRING}}
            );
            return SqlServerConnectionConfig.openWithProperties(properties);
        });
    }

    private static void testDataSourceConnections() {
        System.out.println("\n=== DataSource InitializationString ===");
        run("not set", () -> SqlServerConnectionConfig.sqlServerDataSource().getConnection());
        run("set", () -> {
            SQLServerDataSource dataSource = SqlServerConnectionConfig.sqlServerDataSource();
            dataSource.setInitializationString(INITIALIZATION_STRING);
            return dataSource.getConnection();
        });
    }

    private static void run(String propertyValue, SqlConnectionSupplier supplier) {
        try (Connection connection = supplier.get();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(CHECK_SQL)) {
            resultSet.next();
            System.out.println("InitializationString=" + propertyValue);
            System.out.println("QUOTED_IDENTIFIER : " + resultSet.getInt(1));
        } catch (Exception exception) {
            System.out.println("InitializationString=" + propertyValue);
            System.out.println("Failed: " + exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface SqlConnectionSupplier {
        Connection get() throws SQLException;
    }
}