package src.jdbc.batch;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BatchChunkingDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            BatchDemoSupport.setup(connection);
            connection.setAutoCommit(false);

            String sql = "INSERT INTO " + BatchDemoSupport.TABLE + " (code, value) VALUES (?, ?)";
            int chunkSize = 10;
            int chunkCounter = 0;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int index = 1; index <= 25; index++) {
                    statement.setString(1, String.format("C%03d", index));
                    statement.setDouble(2, index * 1.5);
                    statement.addBatch();
                    chunkCounter++;

                    if (chunkCounter == chunkSize) {
                        statement.executeBatch();
                        connection.commit();
                        System.out.println("Committed chunk ending at row " + index);
                        chunkCounter = 0;
                    }
                }

                if (chunkCounter > 0) {
                    statement.executeBatch();
                    connection.commit();
                    System.out.println("Committed final partial chunk.");
                }
            }

            BatchDemoSupport.printRows(connection);
            BatchDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}