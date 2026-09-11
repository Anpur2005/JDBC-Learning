package src.jdbc.crud.callable;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CallableProcedureDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            CallableDemoSupport.setupSchema(connection);
            callNoParameters(connection);
            callInParameters(connection);
            callOutParameters(connection);
            callInOutParameters(connection);
            CallableDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void callNoParameters(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_no_params}")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                System.out.println("Employee count: " + resultSet.getInt(1));
            }
        }
    }

    private static void callInParameters(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_in_params(?)}")) {
            statement.setString(1, "Engineering");
            try (ResultSet resultSet = statement.executeQuery()) {
                CallableDemoSupport.printEmployees(resultSet);
            }
        }
    }

    private static void callOutParameters(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_out_params(?, ?)}")) {
            statement.setString(1, "Engineering");
            statement.registerOutParameter(2, Types.DECIMAL);
            statement.execute();
            System.out.println("Average engineering salary: " + statement.getBigDecimal(2));
        }
    }

    private static void callInOutParameters(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_inout_params(?)}")) {
            statement.setBigDecimal(1, new java.math.BigDecimal("80000.00"));
            statement.registerOutParameter(1, Types.DECIMAL);
            statement.execute();
            System.out.println("Employees with salary above threshold: " + statement.getBigDecimal(1));
        }
    }
}