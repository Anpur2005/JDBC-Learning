package src.jdbc.metadata;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetMetadataDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            MetadataDemoSupport.setup(connection);

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, username, email, score, is_active, created FROM " + MetadataDemoSupport.TABLE
                 );
                 ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                System.out.println("Column count: " + metaData.getColumnCount());
                for (int index = 1; index <= metaData.getColumnCount(); index++) {
                    System.out.printf(
                        "  [%d] name=%s label=%s type=%s class=%s precision=%d scale=%d nullable=%d%n",
                        index,
                        metaData.getColumnName(index),
                        metaData.getColumnLabel(index),
                        metaData.getColumnTypeName(index),
                        metaData.getColumnClassName(index),
                        metaData.getPrecision(index),
                        metaData.getScale(index),
                        metaData.isNullable(index)
                    );
                }
            }

            MetadataDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}