package src.jdbc.metadata;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class MetadataDemoSupport {

    static final String TABLE = "jdbc_meta_demo";
    static final String PROCEDURE = "meta_demo_proc";

    private MetadataDemoSupport() {
    }

    static void setup(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE " + PROCEDURE);
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " id INT IDENTITY(1,1) PRIMARY KEY," +
                " username VARCHAR(50) NOT NULL," +
                " email VARCHAR(100)," +
                " score DECIMAL(5,2)," +
                " is_active BIT NOT NULL DEFAULT 1," +
                " created DATETIME2" +
                ")"
            );
            statement.execute(
                "INSERT INTO " + TABLE + " (username, email, score, is_active, created) VALUES " +
                "('alice', 'alice@example.com', 98.50, 1, GETDATE())"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE " + PROCEDURE + " @in_str VARCHAR(100), @out_len INT OUTPUT AS BEGIN " +
                " SET @out_len = LEN(@in_str); END"
            );
        }
        connection.commit();
    }

    static void teardown(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP PROCEDURE " + PROCEDURE);
            statement.execute("DROP TABLE " + TABLE);
        }
        connection.commit();
    }
}