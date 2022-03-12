package com.FOS.DBConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class HouseKeeper {

	final static Logger logger = Logger.getLogger(HouseKeeper.class.getName());
	
	public static void closeStatement(Statement statement)
	{
		if(statement != null)
		{
			logger.info("Statement Closed");
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
	
	public static void closePreparedStatement(PreparedStatement preparedStatement)
	{
		if(preparedStatement != null)
		{
			logger.info("PreparedStatement Closed");
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
	
	public static void closeResultSet(ResultSet resultSet)
	{
		if(resultSet != null)
		{
			logger.info("ResultSet Closed");
			try {
				resultSet.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
	
}
