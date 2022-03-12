package com.FOS.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;

public class AESUtils {

	final static Logger logger = LogManager.getLogger(AESUtils.class);

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String myKey)
	{
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String strToEncrypt, String secret)
	{
		try
		{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return DatatypeConverter.printBase64Binary(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
			//return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		}
		catch (Exception e)
		{
			logger.error("Error while encrypting: ",e);
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret)
	{
		try
		{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(strToDecrypt)));
			//return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		}
		catch (Exception e)
		{
			logger.error("Error while decrypting: ",e);
		}
		return null;
	}

	public boolean isLicenceValid(ISPDBConnector ispDBConnector)
	{
		boolean result = false;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			if(connection != null)
			{
				String secretKey = ISPDBConnector.secretKey;
				String sql = "select LICENSE_KEY, to_char(SYSDATE,'DD-MM-YYYY') CURR_DATE from FOS_APP_LICENSE where ROWNUM = 1";
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if(resultSet.next())
				{
					String encrLicense = resultSet.getString("LICENSE_KEY");
					String currentDateString = resultSet.getString("CURR_DATE");

					if(encrLicense != null && !(encrLicense.isEmpty()))
					{
						String decrLicense = decrypt(encrLicense, secretKey);
						if(decrLicense != null && !(decrLicense.isEmpty()))
						{
							Date licenseDate = simpleDateFormat.parse(decrLicense);
							Date currentDate = simpleDateFormat.parse(currentDateString);

							long diff = licenseDate.getTime() - currentDate.getTime();
							long diffDays = diff / (24 * 60 * 60 * 1000);
							if(diffDays >= 0)
								result = true;
						}
					}
				}
			}
		} catch (ClassNotFoundException | IOException | SQLException | ParseException e) {
			logger.error(e);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}

		return result;
	}
}
