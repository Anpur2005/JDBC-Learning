package src.jdbc.crud.callable;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CallableBatchAndNamedParameterDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            CallableDemoSupport.setupSchema(connection);
            runCallableBatch(connection);
            demonstrateNamedParameters(connection);
            CallableDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void runCallableBatch(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_insert_emp(?, ?, ?)}")) {
            addBatchRow(statement, "Ella", "Finance", new BigDecimal("73000.00"));
            addBatchRow(statement, "Noah", "Engineering", new BigDecimal("101000.00"));
            addBatchRow(statement, "Owen", "Support", new BigDecimal("54000.00"));

            int[] counts = statement.executeBatch();
            connection.commit();
            System.out.println("CallableStatement batch count: " + counts.length);
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, name, department, salary FROM " + CallableDemoSupport.TABLE + " ORDER BY id")) {
            CallableDemoSupport.printEmployees(resultSet);
        }
    }

    private static void addBatchRow(CallableStatement statement, String name, String department, BigDecimal salary) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, department);
        statement.setBigDecimal(3, salary);
        statement.addBatch();
    }

    private static void demonstrateNamedParameters(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_insert_emp(?, ?, ?)}")) {
            statement.setString("@name", "Pia");
            statement.setString("@dept", "Operations");
            statement.setBigDecimal("@salary", new BigDecimal("68000.00"));
            statement.execute();
            connection.commit();
            System.out.println("Named parameter call executed.");
        } catch (SQLException exception) {
            connection.rollback();
            System.out.println("Named parameters not supported by this driver: " + exception.getMessage());
        }
    }
}