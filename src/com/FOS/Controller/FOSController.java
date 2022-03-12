package com.FOS.Controller;

import java.lang.reflect.Type;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.DBConnector.PinDBConnector;
import com.FOS.Extractor.CustomerInfoCATVExtractor;
import com.FOS.Extractor.MakePaymentExtractor;
import com.FOS.Extractor.SuspendReactivateExtractor;
import com.FOS.Model.CustomerInfoCATVModel;
import com.FOS.Model.LicenseModel;
import com.FOS.Model.MakePaymentModel;
import com.FOS.Model.PasswordManagerModel;
import com.FOS.Model.SuspendReactivateModel;
import com.FOS.Model.VersionModel;
import com.FOS.Validator.DateValidator;
import com.FOS.Validator.LoginValidationImplementor;
import com.FOS.Validator.LoginValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class FOSController {

	public static String version = "1.3";
	final static Logger logger = LogManager.getLogger(FOSController.class);
	ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
	/*static{
		log4jConfig();
	}

	public static void log4jConfig()
	{
		PropertyConfigurator.configure("/resources/log4j.properties");
	}*/
	public static class BadDoubleDeserializer implements JsonDeserializer<Double> {

		@Override
		public Double deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			try {
				return Double.parseDouble(element.getAsString().replace(',', '.'));
			} catch (NumberFormatException e) {
				throw new JsonParseException(e);
			}
		}

	}
	
	@RequestMapping(value="/updateLicense", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> updateLicense(@RequestHeader(value="UPD_DATE", required = true) String date) 
	{
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		ResponseEntity<String> responseEntity = null;
		int result = 0;
		if(DateValidator.validate(date))
			result = LicenseModel.updateLicense(ispDBConnector, date);
		else
			result = 400;
		logger.info("Result: "+result);
		if(result == 200)
			responseEntity = new ResponseEntity<String>("LICENSE UPDATED SUCCESSFULLY",HttpStatus.OK);
		else if(result == 400)
			responseEntity = new ResponseEntity<String>("INVALID LICENSE DATE",HttpStatus.BAD_REQUEST);
		else if(result == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);	
		return responseEntity;
	}
	
	@RequestMapping(value="/getVersion", method = RequestMethod.GET, produces="text/plain")
	public ResponseEntity<String> getVersion() 
	{
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		ResponseEntity<String> responseEntity = null;
		String version = VersionModel.getVersion(ispDBConnector);
		if(version != null && version.contains("."))
			responseEntity = new ResponseEntity<String>(version,HttpStatus.OK);
		else
			responseEntity = new ResponseEntity<String>(version,HttpStatus.INTERNAL_SERVER_ERROR);	
		return responseEntity;
	}
	
	@RequestMapping(value="/login", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> initLogin(@RequestHeader(value="CREDS", required = true) String credentials) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		/*  return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			and for correct request
			return new ResponseEntity<String>(json,HttpStatus.OK);
			UPDATE 1
			after spring 4.1 there are helper methods in ResponseEntity could be used as
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			and
			return ResponseEntity.ok(json);  */

		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		LoginValidator loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
			/*responseEntity = new ResponseEntity<String>(
					("SUCCESS:"+(Double)validateLoginResponse.get("paymentLimit")).trim()+":"+(String)validateLoginResponse.get("APP_ACCESS")+":"+(String)validateLoginResponse.get("SERVICE_STATUS"),HttpStatus.OK);*/
			responseEntity = new ResponseEntity<String>(
					("SUCCESS:"+(Double)validateLoginResponse.get("paymentLimit")).trim()+":"+
					(String)validateLoginResponse.get("APP_ACCESS")+":0:"+(String)validateLoginResponse.get("USER_TYPE"),HttpStatus.OK);
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		logger.info("Status Code: "+responseEntity.getStatusCode());
		return responseEntity;
	}

	@RequestMapping(value="/getCustomerInfo", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getCustomerInfo(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="TYPE", required = true) String type, @RequestHeader(value="PARAMS", required = true) String parmeters) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		//AgentCustomerValidator agentCustomerCityValidator = (AgentCustomerValidator)context.getBean("agentCustomerValidator");
		if(loginResult == 200)
		{
			/*String userID = loginValidator.getUserID();
			String deviceIMEI = loginValidator.getDeviceIMEI();
			String accountNumber = parmeters;
			String agentID = (String)validateLoginResponse.get("agentID");
			int agentCityValidateResult = agentCustomerCityValidator
					.validateAgentCustomer(userID, deviceIMEI, accountNumber, ispDBConnector, agentID);
			if(agentCityValidateResult == 200)
			{*/
				String userID = (String)validateLoginResponse.get("POID");
				CustomerInfoCATVExtractor extractor = (CustomerInfoCATVExtractor)context.getBean("custInfoCATVExtractor");
				int extractResult = extractor.extractResponseResult(type, parmeters, userID);
				logger.info("Extract Result: "+extractResult);
				if(extractResult == 200)
				{
					CustomerInfoCATVModel model = (CustomerInfoCATVModel)context.getBean("customerInfoCATVModel");
					String customerInfoResponse = extractor.getCustomerInfoResponse();
					try 
					{
						if(!customerInfoResponse.contains("PIN_ERR_BAD_ARG"))
						{
							com.BRM.CATV.GetCustomer.CATVCustomerMaster customerMasterInfo = 
									(com.BRM.CATV.GetCustomer.CATVCustomerMaster)gson.fromJson(customerInfoResponse, com.BRM.CATV.GetCustomer.CATVCustomerMaster.class);
							int customerInfoResult = model.getCustomerInfoResult(customerMasterInfo, pinDBConnector);
							if(customerInfoResult == 200)
							{
								String customerInfoJson = model.getCustomerInfo();
								logger.info("Customer Info JSON:");
								logger.info(customerInfoJson);
								responseEntity = new ResponseEntity<String>(customerInfoJson,HttpStatus.OK);
							}	
							else
								responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
						}
						else
							responseEntity = new ResponseEntity<String>("CAN'T SEARCH THE CUSTOMER ID RIGHT NOW. PLEASE TRY AGAIN AFTER SOMETIME",HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					catch (Exception e) 
					{
						logger.error("Customer Info Error: ",e);
						responseEntity = new ResponseEntity<String>("PLEASE TRY AGAIN",HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				else if(extractResult == 204)
					responseEntity = new ResponseEntity<String>("NO RECORDS FOUND",HttpStatus.NO_CONTENT);
				else if(extractResult == 400)
					responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
				else if(extractResult == 403)
				{
					String customerInfoResponse = extractor.getCustomerInfoResponse();
					responseEntity = new ResponseEntity<String>(customerInfoResponse,HttpStatus.FORBIDDEN);
				}	
				else
					responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
			/*}
			else if(agentCityValidateResult == 400)
				responseEntity = new ResponseEntity<String>("INVALID ACCOUNT NUMBER",HttpStatus.NOT_ACCEPTABLE);
			else if(agentCityValidateResult == 406)
				responseEntity = new ResponseEntity<String>("AGENT HAS NO ACCESS TO THIS CITY",HttpStatus.NOT_ACCEPTABLE);
			else if(loginResult == 500)
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);*/
		}
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/makePayment", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> makePayment(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		LoginValidator loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			MakePaymentExtractor extractor = (MakePaymentExtractor)gson.fromJson(parameters, MakePaymentExtractor.class);
			MakePaymentModel paymentModel = (MakePaymentModel)context.getBean("makePaymentModel");
			String agentID = (String)validateLoginResponse.get("agentID");
			String userID = (String)validateLoginResponse.get("POID");
			int paymentResult = paymentModel.makePaymentResult(extractor, ispDBConnector, loginValidator, agentID, userID);
			String paymentResponseJson = paymentModel.getMakePaymentResponse();
			if(paymentResult == 200)
				responseEntity = new ResponseEntity<String>(paymentResponseJson,HttpStatus.OK);
			else if(paymentResult == 403)
				responseEntity = new ResponseEntity<String>(paymentResponseJson,HttpStatus.FORBIDDEN);
			else if(paymentResult == 404)
				responseEntity = new ResponseEntity<String>(paymentResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(paymentResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/retryPayment", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> retryPayment(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;

		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			MakePaymentModel paymentModel = (MakePaymentModel)context.getBean("makePaymentModel");
			String agentID = (String)validateLoginResponse.get("agentID"), referenceID = parameters;
			int retryPaymentResult = paymentModel.retryPaymentResult(referenceID, ispDBConnector, agentID);
			String retryPaymentResponseJson = paymentModel.getRetryPaymentResponse();
			if(retryPaymentResult == 200)
				responseEntity = new ResponseEntity<String>(retryPaymentResponseJson,HttpStatus.OK);
			else if(retryPaymentResult == 403)
				responseEntity = new ResponseEntity<String>(retryPaymentResponseJson,HttpStatus.FORBIDDEN);
			else if(retryPaymentResult == 404)
				responseEntity = new ResponseEntity<String>(retryPaymentResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(retryPaymentResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/suspendService", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> suspendService(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
	{
		logger.info("Suspend Service Request JSON: ");
		logger.info(parmeters);
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			if((Integer.parseInt((String)validateLoginResponse.get("SERVICE_STATUS")) == 0) || (loginValidator.getUserID().trim().toUpperCase().equals("SOURAB_UP".toUpperCase())) || (loginValidator.getUserID().trim().toUpperCase().equals("SHIVAM_UP".toUpperCase())))
			{
					String agentID = (String)validateLoginResponse.get("agentID");
					String userID = (String)validateLoginResponse.get("POID");
					SuspendReactivateExtractor extractor = (SuspendReactivateExtractor)gson.fromJson(parmeters, SuspendReactivateExtractor.class);
					SuspendReactivateModel model = (SuspendReactivateModel)context.getBean("suspendReactivateModel");
					int suspendResult = model.suspendResult(extractor, ispDBConnector, loginValidator, agentID, userID);
					String suspendResponseJson = model.getSuspendResponse();
					if(suspendResult == 200)
						responseEntity = new ResponseEntity<String>(suspendResponseJson,HttpStatus.OK);
					else if(suspendResult == 403)
						responseEntity = new ResponseEntity<String>(suspendResponseJson,HttpStatus.FORBIDDEN);
					else if(suspendResult == 404)
						responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
					else
						responseEntity = new ResponseEntity<String>(suspendResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else
				responseEntity = new ResponseEntity<String>("SUSPEND NOT ALLOWED",HttpStatus.BAD_REQUEST);
		}
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}
	
	@RequestMapping(value="/reactivateService", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> reactivateService(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
	{
		logger.info("Reactivate Service Request JSON: ");
		logger.info(parmeters);
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			String agentID = (String)validateLoginResponse.get("agentID");
			String userID = (String)validateLoginResponse.get("POID");
			SuspendReactivateExtractor extractor = (SuspendReactivateExtractor)gson.fromJson(parmeters, SuspendReactivateExtractor.class);
			SuspendReactivateModel model = (SuspendReactivateModel)context.getBean("suspendReactivateModel");
			int reactivateResult = model.reactivateResult(extractor, ispDBConnector, loginValidator, agentID, userID);
			String reactivateResponseJson = model.getReactivateResponse();
			if(reactivateResult == 200)
				responseEntity = new ResponseEntity<String>(reactivateResponseJson,HttpStatus.OK);
			else if(reactivateResult == 403)
				responseEntity = new ResponseEntity<String>(reactivateResponseJson,HttpStatus.FORBIDDEN);
			else if(reactivateResult == 404)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(reactivateResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/changePassword", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> changePassword(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String newPassword) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;


		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			PasswordManagerModel passwordManagerModel = (PasswordManagerModel)context.getBean("passwordManagerModel");
			int passwdChangeResult = passwordManagerModel.changePassword(newPassword, ispDBConnector, loginValidator);
			if(passwdChangeResult == 200)
				responseEntity = new ResponseEntity<String>("PASSWORD CHANGED SUCCESSFULLY",HttpStatus.OK);
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 400)
			responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}
}