package src.jdbc.batch;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PreparedStatementBatchFailureDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            BatchDemoSupport.setup(connection);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('P001', 10.00)");
            }

            connection.setAutoCommit(false);
            String sql = "INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                addRow(statement, "P002", 20.00);
                addRow(statement, "P001", 30.00);
                addRow(statement, "P003", 40.00);
                statement.executeBatch();
                connection.commit();
            } catch (BatchUpdateException exception) {
                connection.rollback();
                int[] counts = exception.getUpdateCounts();
                for (int index = 0; index < counts.length; index++) {
                    System.out.println("Prepared batch result " + index + ": " + counts[index]);
                }
            } finally {
                connection.setAutoCommit(true);
            }

            BatchDemoSupport.printRows(connection);
            BatchDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void addRow(PreparedStatement statement, String code, double value) throws SQLException {
        statement.setString(1, code);
        statement.setDouble(2, value);
        statement.addBatch();
    }
}