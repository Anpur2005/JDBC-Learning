package src.jdbc.connections.basic;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DriverManagerPropertiesConnectionDemo {

    public static void main(String[] args) {
        System.out.println("=== DriverManager.getConnection(url, properties) ===");

        Properties properties = SqlServerConnectionConfig.propertiesWithOverrides(
            new String[][]{
                {"ApplicationName", "JDBC-Learning"},
                {"LoginTimeout", "10"}
            }
        );

        try (Connection connection = SqlServerConnectionConfig.openWithProperties(properties)) {
            System.out.println("Connection successful.");
            JdbcDemoSupport.printConnectionSummary(connection);
            System.out.println("ApplicationName configured through Properties.");
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}