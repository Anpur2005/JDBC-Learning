package src.jdbc.batch;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementBatchHappyPathDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            BatchDemoSupport.setup(connection);
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                statement.addBatch("INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('A001', 10.00)");
                statement.addBatch("INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('A002', 20.00)");
                statement.addBatch("INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES ('A003', 30.00)");
                int[] counts = statement.executeBatch();
                connection.commit();
                System.out.println("Batch counts length: " + counts.length);
            }

            BatchDemoSupport.printRows(connection);
            BatchDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}