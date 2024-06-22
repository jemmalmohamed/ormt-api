USE [master];
GO

IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = 'pva')
BEGIN
    CREATE LOGIN [pva] WITH PASSWORD = 'pva@database2024', CHECK_POLICY = OFF;
    ALTER SERVER ROLE [sysadmin] ADD MEMBER [pva];
END
GO