package src.jdbc.datatypes;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.TimeZone;

public class DataTypeNullAndCalendarDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            DataTypeDemoSupport.setup(connection);
            insertNullableRow(connection);
            demonstrateWasNull(connection);
            demonstrateCalendarReads(connection);
            DataTypeDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void insertNullableRow(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + DataTypeDemoSupport.TABLE + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setNull(1, Types.BIT);
            statement.setNull(2, Types.TINYINT);
            statement.setNull(3, Types.SMALLINT);
            statement.setNull(4, Types.INTEGER);
            statement.setNull(5, Types.BIGINT);
            statement.setNull(6, Types.DECIMAL);
            statement.setNull(7, Types.NUMERIC);
            statement.setNull(8, Types.DECIMAL);
            statement.setNull(9, Types.DECIMAL);
            statement.setNull(10, Types.REAL);
            statement.setNull(11, Types.DOUBLE);
            statement.setString(12, "NULLROW");
            statement.setNull(13, Types.VARCHAR);
            statement.setNull(14, Types.VARCHAR);
            statement.setNull(15, Types.NCHAR);
            statement.setNull(16, Types.NVARCHAR);
            statement.setNull(17, Types.NVARCHAR);
            statement.setNull(18, Types.BINARY);
            statement.setNull(19, Types.VARBINARY);
            statement.setNull(20, Types.VARBINARY);
            statement.setTimestamp(21, null);
            statement.setTimestamp(22, null);
            statement.setTimestamp(23, Timestamp.valueOf("2024-06-15 14:30:45"));
            statement.setTimestamp(24, Timestamp.valueOf("2024-06-15 14:30:45"));
            statement.setTimestamp(25, Timestamp.valueOf("2024-06-15 14:30:00"));
            statement.setObject(26, null);
            statement.setObject(27, null);
            statement.setObject(28, null);
            statement.executeUpdate();
        }
        connection.commit();
    }

    private static void demonstrateWasNull(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT c_int, c_varchar FROM " + DataTypeDemoSupport.TABLE)) {
            if (resultSet.next()) {
                int value = resultSet.getInt("c_int");
                System.out.println("c_int wasNull: " + resultSet.wasNull() + " rawValue=" + value);
                String text = resultSet.getString("c_varchar");
                System.out.println("c_varchar wasNull: " + resultSet.wasNull() + " rawValue=" + text);
            }
        }
    }

    private static void demonstrateCalendarReads(Connection connection) throws SQLException {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT c_date, c_time, c_datetime2 FROM " + DataTypeDemoSupport.TABLE + " WHERE c_datetime2 IS NOT NULL")) {
            if (resultSet.next()) {
                System.out.println("Calendar date -> " + resultSet.getDate("c_date", utc));
                System.out.println("Calendar time -> " + resultSet.getTime("c_time", utc));
                System.out.println("Calendar timestamp -> " + resultSet.getTimestamp("c_datetime2", utc));
            }
        }
    }
}