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
import com.FOS.Utils.AESUtils;

public class LicenseModel {

	final static Logger logger = LogManager.getLogger(LicenseModel.class);
	public static int updateLicense(ISPDBConnector ispDBConnector, String date)
	{
		int result = 200;
		String secretKey = ISPDBConnector.secretKey;
		String encryptedString = AESUtils.encrypt(date, secretKey);
		if(encryptedString != null && !(encryptedString.isEmpty()))
		{
			String updateSQL = "UPDATE FOS_APP_LICENSE SET LICENSE_KEY = '"+encryptedString+"' WHERE ROWNUM = 1";
			Connection connection = null;
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				connection = ispDBConnector.getConnection();
				statement = connection.createStatement();
				int res = statement.executeUpdate(updateSQL);
				logger.info("Update Result: "+res);
			} catch (SQLException | ClassNotFoundException | IOException e) {
				logger.error(e);
				return 500;
			}
			finally{
				HouseKeeper.closeResultSet(resultSet);
				HouseKeeper.closeStatement(statement);
				ispDBConnector.closeConnection(connection);
			}
		}
		else
			result = 500; //INTERNAL_SERVER_ERROR
		return result;
	}
}
