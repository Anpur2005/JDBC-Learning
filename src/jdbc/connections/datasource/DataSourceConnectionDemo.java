package src.jdbc.connections.datasource;

import com.ddtek.jdbcx.sqlserver.SQLServerDataSource;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataSourceConnectionDemo {

    public static void main(String[] args) {
        System.out.println("=== DataSource.getConnection() ===");

        SQLServerDataSource dataSource = SqlServerConnectionConfig.sqlServerDataSource();

        try (Connection connection = dataSource.getConnection()) {
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