package src.jdbc.datatypes;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public class DataTypeInsertAndReadDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            DataTypeDemoSupport.setup(connection);
            insertAllTypes(connection);
            readTypedValues(connection);
            readWithGetObject(connection);
            DataTypeDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void insertAllTypes(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + DataTypeDemoSupport.TABLE + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, true);
            statement.setByte(2, (byte) 120);
            statement.setShort(3, (short) -1024);
            statement.setInt(4, 42);
            statement.setLong(5, 9_223_372_036_854L);
            statement.setBigDecimal(6, new BigDecimal("12345.6789"));
            statement.setBigDecimal(7, new BigDecimal("54321.09"));
            statement.setBigDecimal(8, new BigDecimal("1000.1200"));
            statement.setBigDecimal(9, new BigDecimal("99.9900"));
            statement.setFloat(10, 3.14f);
            statement.setDouble(11, 2.718281828);
            statement.setString(12, "HELLO");
            statement.setString(13, "Hello JDBC");
            statement.setCharacterStream(14, new StringReader("Large varchar text"));
            statement.setNString(15, "NCHARé");
            statement.setNString(16, "Unicode 日本語");
            statement.setNCharacterStream(17, new StringReader("National char stream éàü"));
            statement.setBytes(18, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
            statement.setBytes(19, new byte[]{(byte) 0xDE, (byte) 0xAD});
            statement.setBinaryStream(20, new ByteArrayInputStream(new byte[]{9, 8, 7, 6}));
            statement.setDate(21, Date.valueOf(LocalDate.of(2024, 6, 15)));
            statement.setTime(22, Time.valueOf(LocalTime.of(14, 30, 45)));
            statement.setTimestamp(23, Timestamp.valueOf("2024-06-15 14:30:45.123"));
            statement.setTimestamp(24, Timestamp.valueOf("2024-06-15 14:30:45.1234567"));
            statement.setTimestamp(25, Timestamp.valueOf("2024-06-15 14:30:00"));
            statement.setObject(26, "2024-06-15 14:30:45.1234567 +05:30");
            statement.setString(27, "6F9619FF-8B86-D011-B42D-00C04FC964FF");
            statement.setString(28, "<root><item>Hello XML</item></root>");
            statement.executeUpdate();
        }
        connection.commit();
    }

    private static void readTypedValues(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DataTypeDemoSupport.TABLE)) {
            if (!resultSet.next()) {
                return;
            }

            System.out.println("BIT -> " + resultSet.getBoolean("c_bit"));
            System.out.println("TINYINT -> " + resultSet.getByte("c_tinyint"));
            System.out.println("SMALLINT -> " + resultSet.getShort("c_smallint"));
            System.out.println("INT -> " + resultSet.getInt("c_int"));
            System.out.println("BIGINT -> " + resultSet.getLong("c_bigint"));
            System.out.println("DECIMAL -> " + resultSet.getBigDecimal("c_decimal"));
            System.out.println("FLOAT -> " + resultSet.getDouble("c_float"));
            System.out.println("VARCHAR -> " + resultSet.getString("c_varchar"));
            System.out.println("NVARCHAR -> " + resultSet.getNString("c_nvarchar"));
            System.out.println("DATE -> " + resultSet.getDate("c_date"));
            System.out.println("TIME -> " + resultSet.getTime("c_time"));
            System.out.println("DATETIME2 -> " + resultSet.getTimestamp("c_datetime2"));
            System.out.println("UNIQUEIDENTIFIER -> " + resultSet.getString("c_uniqueid"));
            System.out.println("XML -> " + resultSet.getString("c_xml"));
        }
    }

    private static void readWithGetObject(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT c_decimal, c_datetimeoffset, c_xml FROM " + DataTypeDemoSupport.TABLE)) {
            if (resultSet.next()) {
                System.out.println("getObject(c_decimal) -> " + resultSet.getObject("c_decimal"));
                System.out.println("getObject(c_datetimeoffset) -> " + resultSet.getObject("c_datetimeoffset"));
                System.out.println("getObject(c_xml) -> " + resultSet.getObject("c_xml"));
            }
        }
    }
}