package com.FOS.Model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.Validator.LoginValidator;

public class PasswordManagerModel {

	final static Logger logger = LogManager.getLogger(PasswordManagerModel.class);
	
	public int changePassword(String newPassword,
			ISPDBConnector ispDBConnector, LoginValidator loginValidator) {
		int result = 200;
		String ISP_MOB_AGENT_ID = loginValidator.getUserID();
		String ISP_MOB_DEVICE_IMEI = loginValidator.getDeviceIMEI();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			String insertQuery = "update FOS_AGENT_MASTER set PASSWORD = ? where USER_ID = ? and DEVICE_IMEI = ?";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setString(1, newPassword);
			preparedStatement.setString(2, ISP_MOB_AGENT_ID);
			preparedStatement.setString(3, ISP_MOB_DEVICE_IMEI);
			preparedStatement.executeUpdate();
			
		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error("Change Password Exception:", e);
			return result = -100;
		}
		finally{
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

}
