package src.jdbc.metadata;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParameterMetadataDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            MetadataDemoSupport.setup(connection);
            inspectPreparedStatement(connection);
            inspectCallableStatement(connection);
            MetadataDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void inspectPreparedStatement(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, username FROM " + MetadataDemoSupport.TABLE + " WHERE score > ? AND is_active = ?"
             )) {
            ParameterMetaData metaData = statement.getParameterMetaData();
            for (int index = 1; index <= metaData.getParameterCount(); index++) {
                System.out.printf(
                    "Prepared parameter %d -> type=%s nullable=%d mode=%d%n",
                    index,
                    metaData.getParameterTypeName(index),
                    metaData.isNullable(index),
                    metaData.getParameterMode(index)
                );
            }
        }
    }

    private static void inspectCallableStatement(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call " + MetadataDemoSupport.PROCEDURE + "(?, ?)}")) {
            ParameterMetaData metaData = statement.getParameterMetaData();
            for (int index = 1; index <= metaData.getParameterCount(); index++) {
                System.out.printf(
                    "Callable parameter %d -> type=%s nullable=%d mode=%d%n",
                    index,
                    metaData.getParameterTypeName(index),
                    metaData.isNullable(index),
                    metaData.getParameterMode(index)
                );
            }
        }
    }
}