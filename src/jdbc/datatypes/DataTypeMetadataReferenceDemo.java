package src.jdbc.datatypes;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class DataTypeMetadataReferenceDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            DataTypeDemoSupport.setup(connection);
            showResultSetMetadata(connection);
            printTypeReference();
            DataTypeDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void showResultSetMetadata(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DataTypeDemoSupport.TABLE)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            JdbcDemoSupport.printResultSetShape(metaData);
        }
    }

    private static void printTypeReference() {
        System.out.println("JDBC Types reference:");
        System.out.println("  Types.BIT = " + Types.BIT);
        System.out.println("  Types.TINYINT = " + Types.TINYINT);
        System.out.println("  Types.DECIMAL = " + Types.DECIMAL);
        System.out.println("  Types.VARCHAR = " + Types.VARCHAR);
        System.out.println("  Types.TIMESTAMP = " + Types.TIMESTAMP);
        System.out.println("  Types.SQLXML = " + Types.SQLXML);
    }
}