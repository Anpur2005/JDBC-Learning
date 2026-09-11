# JDBC Learning

This repository contains Java JDBC practice code for SQL Server using the DataDirect JDBC driver.

It covers JDBC connection creation, statement APIs, metadata APIs, transactions, batch processing, stored procedures and functions, result set behavior, generated keys, SQL Server data type mapping, and selected driver connection options.

## Folder Structure

```text
src/
	jdbc/
		batch/
		common/
		connections/
			basic/
			datasource/
			options/
		crud/
			callable/
			prepared/
			statement/
		datatypes/
		metadata/
		transactions/
```

## Environment Variables

The connection configuration used by the demos is in `src/jdbc/common/SqlServerConnectionConfig.java`.

Set these environment variables before running any demo:

```powershell
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_USERNAME", "your-username", "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_PASSWORD", "your-password", "User")
```

Optional overrides:

```powershell
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_HOST", "10.30.236.88", "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_PORT", "1433", "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_DATABASE", "test", "User")
```

To remove the saved variables:

```powershell
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_USERNAME", $null, "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_PASSWORD", $null, "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_HOST", $null, "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_PORT", $null, "User")
[Environment]::SetEnvironmentVariable("JDBC_SQLSERVER_DATABASE", $null, "User")
```

## What Each Package Covers

- `src.jdbc.connections.basic`: DriverManager URL connections, separate credentials, properties-based connections, and connection post-configuration.
- `src.jdbc.connections.datasource`: `SQLServerDataSource` connection examples.
- `src.jdbc.connections.options`: `FetchTSWTZAsTimestamp` and `InitializationString` SQL Server driver options.
- `src.jdbc.crud.statement`: CRUD operations, scrollable result sets, updatable result sets, generated keys, and `Statement` execution patterns.
- `src.jdbc.crud.prepared`: Parameter binding, CRUD operations, reusable prepared statements, batching, generated keys, and parameter metadata.
- `src.jdbc.crud.callable`: Stored procedures, stored functions, IN and OUT parameters, INOUT parameters, multiple result sets, callable batching, and named parameters.
- `src.jdbc.metadata`: `DatabaseMetaData`, `ResultSetMetaData`, and `ParameterMetaData` examples.
- `src.jdbc.transactions`: Auto-commit, manual transactions, rollback, savepoints, and transaction isolation levels.
- `src.jdbc.batch`: Statement batch execution, batch failure handling, update-count inspection, chunked batch processing, and prepared-statement batch failures.
- `src.jdbc.datatypes`: SQL Server to Java type mapping, typed getters, `getObject`, null handling, calendar-aware reads, and type metadata.

## Compile

```powershell
javac -cp ".;c:\Program Files\Progress\DataDirect\JDBC\lib\60\sqlserver.jar" src\jdbc\common\*.java src\jdbc\connections\basic\*.java src\jdbc\connections\datasource\*.java src\jdbc\connections\options\*.java src\jdbc\crud\statement\*.java src\jdbc\crud\prepared\*.java src\jdbc\crud\callable\*.java src\jdbc\metadata\*.java src\jdbc\transactions\*.java src\jdbc\batch\*.java src\jdbc\datatypes\*.java
```

## Run Example

```powershell
java -cp ".;c:\Program Files\Progress\DataDirect\JDBC\lib\60\sqlserver.jar" src.jdbc.connections.basic.DriverManagerUrlConnectionDemo
```