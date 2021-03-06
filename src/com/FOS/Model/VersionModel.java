package com.FOS.Model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;

public class VersionModel {

	final static Logger logger = LogManager.getLogger(VersionModel.class);
	
	public static int updateVersion(ISPDBConnector ispDBConnector)
	{
		int result = 200;
		String updateSQL = "UPDATE FOS_APP_VERSION SET VERSION = VERSION +1 WHERE SID = 1";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			int res = statement.executeUpdate(updateSQL);
			logger.info("Update result: "+res);
		} catch (SQLException | ClassNotFoundException | IOException e) {
			logger.error(e);
			return -100;
		}
		finally{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}
	
	public static String getVersion(ISPDBConnector ispDBConnector)
	{
		String result = null;
		String selectSQL = "SELECT VERSION FROM FOS_APP_VERSION WHERE SID = 1";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSQL);
			if(resultSet.next())
				result = resultSet.getString("VERSION");
		} catch (SQLException | ClassNotFoundException | IOException e) {
			logger.error(e);
			result = "CANT GET THE VERSION AT THIS MOMENT";
		}
		finally{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}
}
