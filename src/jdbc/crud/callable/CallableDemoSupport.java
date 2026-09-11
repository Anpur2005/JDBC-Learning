package src.jdbc.crud.callable;

import src.jdbc.common.JdbcDemoSupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class CallableDemoSupport {

    static final String TABLE = "jdbc_emp_cs";

    private CallableDemoSupport() {
    }

    static void setupSchema(Connection connection) throws SQLException {
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE proc_insert_emp");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE proc_multi_rs");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP FUNCTION fn_dept_count");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE proc_inout_params");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE proc_out_params");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE proc_in_params");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP PROCEDURE proc_no_params");
        JdbcDemoSupport.executeIgnoringFailure(connection, "DROP TABLE " + TABLE);

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE " + TABLE + " (" +
                " id INT IDENTITY(1,1) PRIMARY KEY," +
                " name VARCHAR(100)," +
                " department VARCHAR(50)," +
                " salary DECIMAL(10,2)" +
                ")"
            );
            statement.execute(
                "INSERT INTO " + TABLE + " (name, department, salary) VALUES " +
                "('Alice','Engineering',95000)," +
                "('Bob','Marketing',72000)," +
                "('Carol','Engineering',88000)," +
                "('Dave','HR',65000)"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE proc_no_params AS BEGIN " +
                " DECLARE @cnt INT; SELECT @cnt = COUNT(*) FROM " + TABLE + ";" +
                " SELECT @cnt AS employee_count; END"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE proc_in_params @dept VARCHAR(50) AS BEGIN " +
                " SELECT id, name, department, salary FROM " + TABLE + " WHERE department = @dept; END"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE proc_out_params @dept VARCHAR(50), @avg_sal DECIMAL(10,2) OUTPUT AS BEGIN " +
                " SELECT @avg_sal = AVG(salary) FROM " + TABLE + " WHERE department = @dept; END"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE proc_inout_params @threshold DECIMAL(10,2) OUTPUT AS BEGIN " +
                " SELECT @threshold = COUNT(*) FROM " + TABLE + " WHERE salary > @threshold; END"
            );
            statement.execute(
                "CREATE OR ALTER FUNCTION fn_dept_count(@dept VARCHAR(50)) RETURNS INT AS BEGIN " +
                " DECLARE @n INT; SELECT @n = COUNT(*) FROM " + TABLE + " WHERE department = @dept; RETURN @n; END"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE proc_multi_rs AS BEGIN " +
                " SELECT id, name, department FROM " + TABLE + " WHERE department = 'Engineering';" +
                " SELECT id, name, department FROM " + TABLE + " WHERE department = 'Marketing'; END"
            );
            statement.execute(
                "CREATE OR ALTER PROCEDURE proc_insert_emp @name VARCHAR(100), @dept VARCHAR(50), @salary DECIMAL(10,2) AS BEGIN " +
                " INSERT INTO " + TABLE + " (name, department, salary) VALUES (@name, @dept, @salary); END"
            );
        }
        connection.commit();
    }

    static void printEmployees(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.printf("  [%d] %-10s %-12s %.2f%n",
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("department"),
                resultSet.getDouble("salary"));
        }
    }

    static void teardown(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP PROCEDURE proc_insert_emp");
            statement.execute("DROP PROCEDURE proc_multi_rs");
            statement.execute("DROP FUNCTION fn_dept_count");
            statement.execute("DROP PROCEDURE proc_inout_params");
            statement.execute("DROP PROCEDURE proc_out_params");
            statement.execute("DROP PROCEDURE proc_in_params");
            statement.execute("DROP PROCEDURE proc_no_params");
            statement.execute("DROP TABLE " + TABLE);
        }
        connection.commit();
    }
}