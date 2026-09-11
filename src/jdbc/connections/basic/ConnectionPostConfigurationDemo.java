package src.jdbc.connections.basic;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

public class ConnectionPostConfigurationDemo {

    public static void main(String[] args) {
        System.out.println("=== Connection Post-Configuration ===");

        try (Connection connection = SqlServerConnectionConfig.openWithEmbeddedCredentials()) {
            System.out.println("Default auto-commit: " + connection.getAutoCommit());
            connection.setAutoCommit(false);
            System.out.println("Auto-commit after set(false): " + connection.getAutoCommit());

            System.out.println("Default isolation: " + connection.getTransactionIsolation());
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            System.out.println("Isolation after set: " + connection.getTransactionIsolation());

            connection.setReadOnly(false);
            System.out.println("Read-only: " + connection.isReadOnly());

            System.out.println("Current catalog: " + connection.getCatalog());
            connection.setCatalog(SqlServerConnectionConfig.database());
            System.out.println("Catalog after setCatalog(): " + connection.getCatalog());

            // ExecutorService executor = Executors.newSingleThreadExecutor();
            // try {
            //     connection.setNetworkTimeout(executor, 5_000);
            //     System.out.println("Network timeout: " + connection.getNetworkTimeout());
            // } finally {
            //     executor.shutdown();
            // }

            Properties clientInfo = new Properties();
            clientInfo.setProperty("ApplicationName", "JDBC-Learning-Connection-Config");
            connection.setClientInfo(clientInfo);
            System.out.println("Connection valid: " + connection.isValid(5));
            System.out.println("Connection closed: " + connection.isClosed());
            JdbcDemoSupport.printConnectionSummary(connection);
        } catch (Exception exception) {
            if (exception instanceof SQLException sqlException) {
                JdbcDemoSupport.printSqlException(sqlException);
                return;
            }
            throw new RuntimeException(exception);
        }
    }
}