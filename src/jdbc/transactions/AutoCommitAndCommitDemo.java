package src.jdbc.transactions;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AutoCommitAndCommitDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            TransactionDemoSupport.setup(connection);

            System.out.println("Default auto-commit: " + connection.getAutoCommit());
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance + 100 WHERE owner = 'Alice'");
            }

            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance - 200 WHERE owner = 'Alice'");
                statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance + 200 WHERE owner = 'Bob'");
                connection.commit();
            }

            TransactionDemoSupport.printBalances(connection, "Balances after auto-commit and manual commit:");
            connection.setAutoCommit(true);
            TransactionDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}