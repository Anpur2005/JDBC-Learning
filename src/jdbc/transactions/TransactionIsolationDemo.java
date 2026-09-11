package src.jdbc.transactions;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TransactionIsolationDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            TransactionDemoSupport.setup(connection);
            DatabaseMetaData metaData = connection.getMetaData();
            int[] levels = {
                Connection.TRANSACTION_READ_UNCOMMITTED,
                Connection.TRANSACTION_READ_COMMITTED,
                Connection.TRANSACTION_REPEATABLE_READ,
                Connection.TRANSACTION_SERIALIZABLE
            };

            for (int level : levels) {
                System.out.println("Supports " + level + ": " + metaData.supportsTransactionIsolationLevel(level));
            }

            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            System.out.println("Current isolation: " + connection.getTransactionIsolation());
            System.out.println("Dirty reads are possible at READ_UNCOMMITTED.");
            System.out.println("Non-repeatable reads are blocked at REPEATABLE_READ.");
            System.out.println("Phantom reads are blocked at SERIALIZABLE.");

            TransactionDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}