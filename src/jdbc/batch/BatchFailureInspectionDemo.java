package src.jdbc.batch;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BatchFailureInspectionDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            BatchDemoSupport.setup(connection);
            seed(connection);
            demonstrateFailureInspection(connection);
            BatchDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void seed(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('A001', 10.00)");
        }
    }

    private static void demonstrateFailureInspection(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        String[] statements = {
            "INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('B001', 1.00)",
            "INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('A001', 9.99)",
            "INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('B002', 2.00)"
        };

        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
            connection.commit();
        } catch (BatchUpdateException exception) {
            List<Integer> failedIndices = new ArrayList<>();
            int[] counts = exception.getUpdateCounts();
            for (int index = 0; index < counts.length; index++) {
                if (counts[index] == Statement.EXECUTE_FAILED) {
                    failedIndices.add(index);
                }
            }
            for (int index = counts.length; index < statements.length; index++) {
                failedIndices.add(index);
            }

            connection.rollback();
            System.out.println("Failed statement indices: " + failedIndices);
            for (Throwable throwable : exception) {
                if (throwable instanceof SQLException sqlException) {
                    System.out.println("  Failure detail: " + sqlException.getMessage());
                }
            }
            System.out.println("Rows after rollback:");
            BatchDemoSupport.printRows(connection);
        } finally {
            connection.setAutoCommit(true);
        }
    }
}