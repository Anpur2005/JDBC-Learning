package src.jdbc.crud.prepared;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementMetadataDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);

            PreparedStatementDemoSupport.recreateTable(connection);
            inspectParameterMetadata(connection);
            PreparedStatementDemoSupport.dropTable(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void inspectParameterMetadata(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + PreparedStatementDemoSupport.TABLE + " (name, category, price, in_stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ParameterMetaData metaData = statement.getParameterMetaData();
            for (int index = 1; index <= metaData.getParameterCount(); index++) {
                System.out.printf(
                    "Parameter %d -> type=%s nullable=%d mode=%d%n",
                    index,
                    metaData.getParameterTypeName(index),
                    metaData.isNullable(index),
                    metaData.getParameterMode(index)
                );
            }
        }
    }
}