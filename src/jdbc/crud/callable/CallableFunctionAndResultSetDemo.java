package src.jdbc.crud.callable;

import src.jdbc.common.JdbcDemoSupport;
import src.jdbc.common.SqlServerConnectionConfig;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CallableFunctionAndResultSetDemo {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(SqlServerConnectionConfig.urlWithCredentials())) {
            connection.setAutoCommit(false);
            CallableDemoSupport.setupSchema(connection);
            callStoredFunction(connection);
            callProcedureReturningResultSet(connection);
            callProcedureReturningMultipleResultSets(connection);
            CallableDemoSupport.teardown(connection);
        } catch (SQLException exception) {
            JdbcDemoSupport.printSqlException(exception);
        }
    }

    private static void callStoredFunction(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{? = call fn_dept_count(?)}")) {
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, "Engineering");
            statement.execute();
            System.out.println("Engineering employee count: " + statement.getInt(1));
        }
    }

    private static void callProcedureReturningResultSet(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_in_params(?)}")) {
            statement.setString(1, "Marketing");
            try (ResultSet resultSet = statement.executeQuery()) {
                CallableDemoSupport.printEmployees(resultSet);
            }
        }
    }

    private static void callProcedureReturningMultipleResultSets(Connection connection) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("{call proc_multi_rs}")) {
            boolean hasResults = statement.execute();
            int index = 1;
            while (hasResults) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    System.out.println("Result set " + index++ + ":");
                    while (resultSet.next()) {
                        System.out.printf("  %s (%s)%n", resultSet.getString("name"), resultSet.getString("department"));
                    }
                }
                hasResults = statement.getMoreResults();
            }
        }
    }
}