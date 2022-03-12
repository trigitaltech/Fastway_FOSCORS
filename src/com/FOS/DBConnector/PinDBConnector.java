package com.FOS.DBConnector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PinDBConnector
{
	final static Logger logger = LogManager.getLogger(PinDBConnector.class);
	private Connection connection;
	//private DataSource dataSource;
	public Connection getConnection() throws IOException, SQLException, ClassNotFoundException {
		Properties properties = new Properties();
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("resources/database.properties");
		properties.load(fileInputStream);
		Class.forName(properties.getProperty("ORACLE_DB_DRIVER_CLASS").trim());
		connection = DriverManager.getConnection(properties.getProperty("ORACLE_DB_URL").trim(),
				properties.getProperty("ORACLE_DB_USERNAME").trim(),
				properties.getProperty("ORACLE_DB_PASSWORD").trim());
		logger.info("Pin Database Connected");
		return connection;
	}	
		

	/*public Connection getConnection() throws IOException, SQLException, ClassNotFoundException {
		 PoolProperties poolProperties = new PoolProperties();

		poolProperties.setInitialSize(Integer.parseInt(properties.getProperty("initialSize").trim()));
		poolProperties.setMaxActive(Integer.parseInt(properties.getProperty("initialSize").trim()));
		poolProperties.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle").trim()));
		poolProperties.setMinIdle(Integer.parseInt(properties.getProperty("minIdle").trim()));
		poolProperties.setTimeBetweenEvictionRunsMillis(Integer.parseInt(properties.getProperty("timeBetweenEvictionRunsMillis").trim()));
		poolProperties.setMinEvictableIdleTimeMillis(Integer.parseInt(properties.getProperty("minEvictableIdleTimeMillis").trim()));
		poolProperties.setValidationInterval(Long.parseLong(properties.getProperty("validationInterval").trim()));
		poolProperties.setTestOnBorrow(Boolean.parseBoolean(properties.getProperty("testOnBorrow").trim()));
		poolProperties.setRemoveAbandoned(Boolean.parseBoolean(properties.getProperty("removeAbandoned").trim()));
		poolProperties.setRemoveAbandonedTimeout(Integer.parseInt(properties.getProperty("removeAbandonedTimeout").trim()));

		poolProperties.setDriverClassName(properties.getProperty("ORACLE_DB_DRIVER_CLASS").trim());
		poolProperties.setUrl(properties.getProperty("ORACLE_DB_URL").trim());
		poolProperties.setUsername(properties.getProperty("ORACLE_DB_USERNAME").trim());
		poolProperties.setPassword(properties.getProperty("ORACLE_DB_PASSWORD").trim());

		poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setInitialSize(1);
        poolProperties.setMaxWait(10000);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setMinEvictableIdleTimeMillis(30000);
        poolProperties.setMaxActive(2);
        poolProperties.setMinIdle(1);
        poolProperties.setMaxIdle(1);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer;");
        dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);
		
		connection = dataSource.getConnection();
		return connection; 
	}*/
	
	/*public Connection getConnection() throws NamingException, SQLException{
		Context initContext = new InitialContext();
		Context envContext  = (Context)initContext.lookup("java:/comp/env");
		dataSource = (DataSource)envContext.lookup("jdbc/pinDB");
		connection = dataSource.getConnection();
		return connection;
	}*/

	public void closeConnection(Connection connection)
	{
		if(connection != null)
		{
			logger.info("Connection Closed");
			try {
				connection.close();
				connection = null;
				//dataSource.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
