package src.jdbc.transactions;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.sql.Statement;

public class RollbackAndSavepointDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            TransactionDemoSupport.setup(connection);
            demonstrateRollback(connection);
            demonstrateSavepoints(connection);
            TransactionDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void demonstrateRollback(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance - 300 WHERE owner = 'Bob'");
                statement.executeUpdate("UPDATE missing_table SET balance = 0 WHERE id = 1");
                connection.commit();
            }
        } catch (SQLException exception) {
            connection.rollback();
            System.out.println("Rollback executed after failure.");
        } finally {
            connection.setAutoCommit(true);
        }

        TransactionDemoSupport.printBalances(connection, "Balances after rollback:");
    }

    private static void demonstrateSavepoints(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance + 500 WHERE owner = 'Alice'");
            Savepoint afterAlice = connection.setSavepoint("after_alice_credit");
            statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance - 100 WHERE owner = 'Bob'");
            Savepoint unnamed = connection.setSavepoint();
            statement.executeUpdate("UPDATE " + TransactionDemoSupport.TABLE + " SET balance = balance - 50 WHERE owner = 'Bob'");
            System.out.println("Unnamed savepoint id: " + unnamed.getSavepointId());
            connection.rollback(afterAlice);
            connection.commit();
        } finally {
            connection.setAutoCommit(true);
        }

        TransactionDemoSupport.printBalances(connection, "Balances after savepoint rollback:");
    }
}