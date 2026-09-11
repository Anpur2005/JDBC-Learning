package src.jdbc.connections.basic;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DriverManagerUrlConnectionDemo {

    public static void main(String[] args) {
        System.out.println("=== DriverManager.getConnection(url) ===");

        try (Connection connection = SqlServerConnectionConfig.openWithEmbeddedCredentials()) {
            System.out.println("Connection successful.");
            JdbcDemoSupport.printConnectionSummary(connection);
            try (ResultSet tables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
                System.out.println("Available tables:");
                JdbcDemoSupport.printTables(tables);
            }
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}