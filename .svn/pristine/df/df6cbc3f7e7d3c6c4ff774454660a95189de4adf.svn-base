package com.FOS.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PasswordUtils {

	final static Logger logger = LogManager.getLogger(PasswordUtils.class);
	public String getEncryptedMD5String(String original) {
		try {
			logger.info("Original Password: "+original);
			if(original != null){
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(original.getBytes());
				byte[] digest = md.digest();
				StringBuffer sb = new StringBuffer();
				for (byte b : digest) {
					sb.append(String.format("%02x", b & 0xff));
				}
				String md5Password = sb.toString();
				logger.info("MD5 Password: "+md5Password);
				return md5Password;
			}
			else
				return original;
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			return null;
		}

	}

}
