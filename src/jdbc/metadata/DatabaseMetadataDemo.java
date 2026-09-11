package src.jdbc.metadata;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMetadataDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            MetadataDemoSupport.setup(connection);

            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            System.out.println("Database: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("User: " + metaData.getUserName());
            System.out.println("supportsTransactions: " + metaData.supportsTransactions());
            System.out.println("supportsBatchUpdates: " + metaData.supportsBatchUpdates());
            System.out.println("supportsStoredProcedures: " + metaData.supportsStoredProcedures());
            System.out.println("supportsNamedParameters: " + metaData.supportsNamedParameters());
            System.out.println("supportsGetGeneratedKeys: " + metaData.supportsGetGeneratedKeys());

            try (ResultSet tables = metaData.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
                System.out.println("Tables:");
                JdbcDemoSupport.printTables(tables);
            }

            try (ResultSet columns = metaData.getColumns(connection.getCatalog(), null, MetadataDemoSupport.TABLE, "%")) {
                System.out.println("Columns for " + MetadataDemoSupport.TABLE + ":");
                while (columns.next()) {
                    System.out.printf("  %s -> %s(%d)%n",
                        columns.getString("COLUMN_NAME"),
                        columns.getString("TYPE_NAME"),
                        columns.getInt("COLUMN_SIZE"));
                }
            }

            try (ResultSet procedures = metaData.getProcedures(connection.getCatalog(), null, MetadataDemoSupport.PROCEDURE)) {
                while (procedures.next()) {
                    System.out.println("Procedure found: " + procedures.getString("PROCEDURE_NAME"));
                }
            }

            try (ResultSet types = metaData.getTypeInfo()) {
                int shown = 0;
                System.out.println("Type info sample:");
                while (types.next() && shown < 10) {
                    System.out.println("  " + types.getString("TYPE_NAME") + " -> JDBC type " + types.getInt("DATA_TYPE"));
                    shown++;
                }
            }

            MetadataDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }
}