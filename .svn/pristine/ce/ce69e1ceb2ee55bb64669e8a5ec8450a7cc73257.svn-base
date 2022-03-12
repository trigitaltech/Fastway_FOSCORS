package com.FOS.Validator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;

public class AgentCustomerValidator {

	final static Logger logger = LogManager.getLogger(AgentCustomerValidator.class);

	public int validateAgentCustomer(String userID, String deviceIMEI, String accountNo, ISPDBConnector ispDBConnector, String agentID)
	{
		int result = 0, success = 200, /*unauthorized = 406, badRequest = 400,*/ failure = 500;
		
		
		/*String customerTypeQuery = "select a.ACCOUNT_NO, a.POID_ID0, substr(to_char(a.BUSINESS_TYPE), 1,2) BUSINESS_TYPE from  "
				+ "pin.account_t@preprod a where a.ACCOUNT_NO = ? and substr(to_char(a.BUSINESS_TYPE), 1,2) in ('95','96','97','98')";*/
		

		String allCityQuery = "select distinct UPPER(a.CITY) CITY from "
				+ "FOS_CITY_MASTER a, FOS_AGENT_CITY_MAPPER b, FOS_AGENT_MASTER c "
				+ "where a.CITY_ID = b.CITY_ID "
				+ "and b.AGENT_ID = ? "
				+ "and UPPER(a.CITY) = 'ALL'";
		
		
		/*String cityQuery = "select distinct UPPER(a.CITY) CITY from "
					+ "FOS_CITY_MASTER a, FOS_AGENT_CITY_MAPPER b, FOS_AGENT_MASTER c "
					+ "where a.CITY_ID = b.CITY_ID "
					+ "and b.AGENT_ID = ? "
					+ "and UPPER(a.CITY) in (select distinct UPPER(a.CITY)from PIN.ACCOUNT_NAMEINFO_T@preprod a, pin.ACCOUNT_T@preprod b "
					+ "where a.OBJ_ID0 = b.POID_ID0 "
					+ "and b.ACCOUNT_NO = ?)";*/
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			/*preparedStatement = connection.prepareStatement(customerTypeQuery);
			preparedStatement.setString(1, accountNo);
			resultSet = preparedStatement.executeQuery();
			if(resultSet != null && resultSet.next())
			{
				HouseKeeper.closePreparedStatement(preparedStatement);*/
				
				preparedStatement = connection.prepareStatement(allCityQuery);
				preparedStatement.setString(1, agentID);
				ResultSet resultSet1 = preparedStatement.executeQuery();
				if(resultSet1.next())
				{
					logger.info("City: "+resultSet1.getString("CITY")); //Agent can access all cities  -- 200
					result = success;
					HouseKeeper.closeResultSet(resultSet1);
				}	
				/*else
				{
					HouseKeeper.closePreparedStatement(preparedStatement);
					preparedStatement.setString(1, agentID);
					preparedStatement.setString(2, accountNo);
					ResultSet resultSet2 = preparedStatement.executeQuery();
					if(resultSet2.next())
					{
						logger.info("City: "+resultSet2.getString("CITY"));//Agent city and customer city are matched 
						result = success;
					}
					else
						result = unauthorized; //Agent city and customer city are not matched -- 406
					HouseKeeper.closeResultSet(resultSet2);
				}
			}	
			else
				result = badRequest; // INVALID CUSTOMER ID --- CATV Customer / Invalid Customer -- 400 */
		} catch (SQLException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("Agent City Validation: ",e);
			result = failure;
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			//HouseKeeper.closeStatement(statement);
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}
}
