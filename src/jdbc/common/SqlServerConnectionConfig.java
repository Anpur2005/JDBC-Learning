package src.jdbc.common;

import com.ddtek.jdbcx.sqlserver.SQLServerDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class SqlServerConnectionConfig {

    private static final String DEFAULT_HOST = "10.30.236.88";
    private static final String DEFAULT_PORT = "1433";
    private static final String DEFAULT_DATABASE = "test";

    private static final String ENV_HOST = "JDBC_SQLSERVER_HOST";
    private static final String ENV_PORT = "JDBC_SQLSERVER_PORT";
    private static final String ENV_DATABASE = "JDBC_SQLSERVER_DATABASE";
    private static final String ENV_USERNAME = "JDBC_SQLSERVER_USERNAME";
    private static final String ENV_PASSWORD = "JDBC_SQLSERVER_PASSWORD";

    private SqlServerConnectionConfig() {
    }

    public static String host() {
        return readEnvOrDefault(ENV_HOST, DEFAULT_HOST);
    }

    public static int port() {
        return Integer.parseInt(readEnvOrDefault(ENV_PORT, DEFAULT_PORT));
    }

    public static String database() {
        return readEnvOrDefault(ENV_DATABASE, DEFAULT_DATABASE);
    }

    public static String username() {
        return requiredEnv(ENV_USERNAME);
    }

    public static String password() {
        return requiredEnv(ENV_PASSWORD);
    }

    public static String baseUrl() {
        return "jdbc:datadirect:sqlserver://" + host() + ":" + port() + ";DatabaseName=" + database() + ";";
    }

    public static String urlWithCredentials() {
        return baseUrl() + "User=" + username() + ";Password=" + password() + ";";
    }

    public static Properties credentialsProperties() {
        Properties properties = new Properties();
        properties.setProperty("User", username());
        properties.setProperty("Password", password());
        return properties;
    }

    public static Properties propertiesWithOverrides(String[][] overrides) {
        Properties properties = credentialsProperties();
        if (overrides == null) {
            return properties;
        }

        for (String[] override : overrides) {
            if (override != null && override.length == 2) {
                properties.setProperty(override[0], override[1]);
            }
        }
        return properties;
    }

    public static Connection openWithEmbeddedCredentials() throws SQLException {
        return DriverManager.getConnection(urlWithCredentials());
    }

    public static Connection openWithSeparateCredentials() throws SQLException {
        return DriverManager.getConnection(baseUrl(), username(), password());
    }

    public static Connection openWithProperties(Properties properties) throws SQLException {
        return DriverManager.getConnection(baseUrl(), properties);
    }

    public static SQLServerDataSource sqlServerDataSource() {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName(host());
        dataSource.setPortNumber(port());
        dataSource.setDatabaseName(database());
        dataSource.setUser(username());
        dataSource.setPassword(password());
        return dataSource;
    }

    private static String readEnvOrDefault(String envName, String defaultValue) {
        String value = System.getenv(envName);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String requiredEnv(String envName) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + envName);
        }
        return value;
    }
}