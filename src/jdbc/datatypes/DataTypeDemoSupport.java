package src.jdbc.datatypes;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class DataTypeDemoSupport {

    static final String TABLE = "jdbc_type_demo";

    private DataTypeDemoSupport() {
    }

    static void setup(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " c_bit BIT," +
                " c_tinyint TINYINT," +
                " c_smallint SMALLINT," +
                " c_int INT," +
                " c_bigint BIGINT," +
                " c_decimal DECIMAL(18,4)," +
                " c_numeric NUMERIC(10,2)," +
                " c_money MONEY," +
                " c_smallmoney SMALLMONEY," +
                " c_real REAL," +
                " c_float FLOAT," +
                " c_char CHAR(10)," +
                " c_varchar VARCHAR(100)," +
                " c_varchar_max VARCHAR(MAX)," +
                " c_nchar NCHAR(10)," +
                " c_nvarchar NVARCHAR(100)," +
                " c_nvarchar_max NVARCHAR(MAX)," +
                " c_binary BINARY(8)," +
                " c_varbinary VARBINARY(100)," +
                " c_varbinary_max VARBINARY(MAX)," +
                " c_date DATE," +
                " c_time TIME(7)," +
                " c_datetime DATETIME," +
                " c_datetime2 DATETIME2(7)," +
                " c_smalldatetime SMALLDATETIME," +
                " c_datetimeoffset DATETIMEOFFSET(7)," +
                " c_uniqueid UNIQUEIDENTIFIER," +
                " c_xml XML" +
                ")"
            );
        }
        connection.commit();
    }

    static void teardown(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE " + TABLE);
        }
        connection.commit();
    }
}