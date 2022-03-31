package com.FOS.Controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
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
import com.FOS.Extractor.CreateTicketExtractor;
import com.FOS.Extractor.CustomerSearchBillExtractor;
import com.FOS.Extractor.GetReceiptsExtractor;
import com.FOS.Extractor.TransactionMasterExtractor;
import com.FOS.Model.CreateTicketModel;
import com.FOS.Model.GetCustomerBillModel;
import com.FOS.Model.GetReceiptsModel;
import com.FOS.Model.ReportsModel;
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
public class CommonController {

	final static Logger logger = LogManager.getLogger(CommonController.class);
	ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

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

	@RequestMapping(value="/agentTransactions", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> agentTransactions(@RequestHeader(value="CREDS", required = true) String credentials) 
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
			TransactionMasterExtractor transactionExtractor = (TransactionMasterExtractor)
					context.getBean("transactionExtractor");
			String agentID = (String)validateLoginResponse.get("agentID");
			int agentTransResult = transactionExtractor.processAgentTransactionsList(agentID, ispDBConnector);
			if(agentTransResult == 200)
			{
				List<Map<String, String>> agentTransactionList = transactionExtractor.getAgentTransactionsList();
				StringWriter stringWriter = new StringWriter();
				gson.toJson(agentTransactionList,stringWriter);
				String agentTransactionListJSON = stringWriter.toString();
				responseEntity = new ResponseEntity<String>(agentTransactionListJSON,HttpStatus.OK);
			}
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/accountTransactions", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> accountTransactions(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String accountNo) 
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
			TransactionMasterExtractor transactionExtractor = (TransactionMasterExtractor)
					context.getBean("transactionExtractor");
			int accountTransResult = transactionExtractor.processUserTransactionsList(accountNo, ispDBConnector);
			if(accountTransResult == 200)
			{
				List<Map<String, String>> accountTransactionList = transactionExtractor.getUserTransactionsList();
				StringWriter stringWriter = new StringWriter();
				gson.toJson(accountTransactionList,stringWriter);
				String agentTransactionListJSON = stringWriter.toString();
				responseEntity = new ResponseEntity<String>(agentTransactionListJSON,HttpStatus.OK);
			}
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/terminalReport", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> terminalReport(@RequestHeader(value="CREDS", required = true) String credentials) 
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
			TransactionMasterExtractor transactionExtractor = (TransactionMasterExtractor)
					context.getBean("transactionExtractor");
			String agentID = (String)validateLoginResponse.get("agentID");
			int terminalReportResult = transactionExtractor.processTerminalReport(ispDBConnector, agentID);
			if(terminalReportResult == 200)
			{
				List<Map<String, String>> terminalReportList = transactionExtractor.getTerminalReportList();
				StringWriter stringWriter = new StringWriter();
				gson.toJson(terminalReportList,stringWriter);
				String terminalReportListJSON = stringWriter.toString();
				responseEntity = new ResponseEntity<String>(terminalReportListJSON,HttpStatus.OK);
			}
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 498)
			responseEntity = new ResponseEntity<String>("CONTACT SYSTEM ADMINISTRATOR",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/totalSubscriberCount", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> totalSubscriberCount(@RequestHeader(value="CREDS", required = true) String credentials) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		//PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			ReportsModel reportsModel = (ReportsModel)context.getBean("reportsModel");
			String agentAccountPOID = Long.toString((Long)validateLoginResponse.get("agentAccountPOID"));
			int reportsResult = reportsModel.totalSubscriberCountResult(ispDBConnector, agentAccountPOID);
			if(reportsResult == 200)
			{
				String totalSubscriberCountResponse = reportsModel.getTotalSubscriberCountResponse();
				responseEntity = new ResponseEntity<String>(totalSubscriberCountResponse,HttpStatus.OK);
				
				/* responseEntity = new ResponseEntity<String>("NO DATA",HttpStatus.INTERNAL_SERVER_ERROR); */
			}
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

	@RequestMapping(value="/subscriberWiseActivePkgCount", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> subscriberWiseActivePkgCount(@RequestHeader(value="CREDS", required = true) String credentials) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		//PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			ReportsModel reportsModel = (ReportsModel)context.getBean("reportsModel");
			String agentAccountPOID = Long.toString((Long)validateLoginResponse.get("agentAccountPOID"));
			int reportsResult = reportsModel.subscriberWiseActivePkgCountResult(ispDBConnector, agentAccountPOID);
			if(reportsResult == 200)
			{
				String subscriberWiseActivePkgCountResponse = reportsModel.getSubscriberWiseActivePkgCountResponse();
				responseEntity = new ResponseEntity<String>(subscriberWiseActivePkgCountResponse,HttpStatus.OK);
			}
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

	@RequestMapping(value="/dailyAndWeeklyCollectionReport", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> dailyAndWeeklyCollectionReport(@RequestHeader(value="CREDS", required = true) String credentials) 
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
		if(loginResult == 200)
		{
			ReportsModel reportsModel = (ReportsModel)context.getBean("reportsModel");
			String userID = ((String)validateLoginResponse.get("USER_ID"));
			int reportsResult = reportsModel.dailyAndWeeklyCollectionReportResult(pinDBConnector, userID);
			if(reportsResult == 200)
			{
				String dailyAndWeeklyCollectionReportResponse = reportsModel.getDailyAndWeeklyCollectionReportResponse();
				responseEntity = new ResponseEntity<String>(dailyAndWeeklyCollectionReportResponse,HttpStatus.OK);
			}
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

	@RequestMapping(value="/openAndClosedComplaintsCount", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> openAndClosedComplaintsCount(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
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
		if(loginResult == 200)
		{
			ReportsModel reportsModel = (ReportsModel)context.getBean("reportsModel");
			String agentAccountPOID = Long.toString((Long)validateLoginResponse.get("agentAccountPOID"));
			String period = parmeters;
			int reportsResult = reportsModel.openAndClosedComplaintsCountResult(pinDBConnector, agentAccountPOID, period);
			if(reportsResult == 200)
			{
				String openAndClosedComplaintsCountResponse = reportsModel.getOpenAndClosedComplaintsCountResponse();
				responseEntity = new ResponseEntity<String>(openAndClosedComplaintsCountResponse,HttpStatus.OK);
			}
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

	@RequestMapping(value="/getReceipts", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getReceipts(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
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
			String accountPOID = parmeters;
			GetReceiptsExtractor extractor = (GetReceiptsExtractor)context.getBean("getReceiptsExtractor");
			int extractResult = extractor.extractReceiptsResult(accountPOID, loginValidator);
			if(extractResult == 200)
			{
				GetReceiptsModel model = (GetReceiptsModel)context.getBean("getReceiptsModel");
				String paymentReceiptInfo = extractor.getReceiptsResponse();
				try 
				{
					if(!paymentReceiptInfo.contains("PIN_ERR_BAD_ARG") && !paymentReceiptInfo.contains("ERROR_DESCR"))
					{
						if(paymentReceiptInfo.contains("ACCOUNT_NO"))
						{
							com.BRM.CATV.GetReceipts.CATVReceiptsMaster bbReceiptsMaster = 
									(com.BRM.CATV.GetReceipts.CATVReceiptsMaster)gson.fromJson(paymentReceiptInfo, com.BRM.CATV.GetReceipts.CATVReceiptsMaster.class);
							int paymentReceiptInfoResult = model.getReceiptInfoResult(bbReceiptsMaster);
							if(paymentReceiptInfoResult == 200)
							{
								String getReceiptsInfoJson = model.getPaymentReceiptInfo();
								/*logger.info("Get Receipts JSON:");
							logger.info(getReceiptsInfoJson);*/
								responseEntity = new ResponseEntity<String>(getReceiptsInfoJson,HttpStatus.OK);
							}	
							else
								responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
						}
						else
							responseEntity = new ResponseEntity<String>("NO RECEIPTS FOUND",HttpStatus.INTERNAL_SERVER_ERROR);
					}
					else if(paymentReceiptInfo.contains("ERROR_CODE"))
					{
						JSONObject jsonObject = new JSONObject(paymentReceiptInfo);
						String ERROR_CODE = jsonObject.getJSONObject("MSO_OP_PYMT_GET_RECEIPTS_outputFlist").optString("ERROR_CODE", "408");
						String ERROR_DESCR = jsonObject.getJSONObject("MSO_OP_PYMT_GET_RECEIPTS_outputFlist").optString("ERROR_DESCR", "NO_ERROR");
						responseEntity = new ResponseEntity<String>(ERROR_CODE+":"+ERROR_DESCR,HttpStatus.INTERNAL_SERVER_ERROR);
					}
					else
						responseEntity = new ResponseEntity<String>("INVALID CUSTOMER ID",HttpStatus.INTERNAL_SERVER_ERROR);
				} 
				catch (Exception e) 
				{
					StringWriter stack = new StringWriter();
					e.printStackTrace(new PrintWriter(stack));
					logger.error("Get Receipts Log: "+stack);
					responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/getCustomerBills", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getCustomerBills(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="ACC", required = true) String accountNo) 
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
			CustomerSearchBillExtractor extractor = (CustomerSearchBillExtractor)context.getBean("customerSearchBillExtractor");
			int extractResult = extractor.extractCustomerSearchBillResult(accountNo, loginValidator);
			if(extractResult == 200)
			{
				GetCustomerBillModel model = (GetCustomerBillModel)context.getBean("getCustomerBillModel");
				String customerBillInfo = extractor.getCustomerSearchBillResponse();
				try 
				{
					if(!customerBillInfo.contains("PIN_ERR_BAD_ARG") && !customerBillInfo.contains("ERROR_CODE"))
					{
						if(customerBillInfo.contains("ACCOUNT_OBJ"))
						{
							com.BRM.CATV.CustomerSearchBill.CustomerSearchBillMaster customerSearchBillMaster = 
									(com.BRM.CATV.CustomerSearchBill.CustomerSearchBillMaster)gson.fromJson(customerBillInfo, (com.BRM.CATV.CustomerSearchBill.CustomerSearchBillMaster.class));
							int customerSearchBillResult = model.getCustomerSearchBillResult(customerSearchBillMaster);
							if(customerSearchBillResult == 200)
							{
								String customerBillInfoJson = model.getCustomerBillInfo();
								/*logger.info("Get Customer Bill Info JSON:");
							logger.info(customerBillInfoJson);*/
								responseEntity = new ResponseEntity<String>(customerBillInfoJson,HttpStatus.OK);
							}	
							else
								responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
						}
						else
							responseEntity = new ResponseEntity<String>("NO BILLS FOUND",HttpStatus.INTERNAL_SERVER_ERROR);
					}
					else if(customerBillInfo.contains("ERROR_CODE"))
					{
						JSONObject jsonObject = new JSONObject(customerBillInfo);
						String ERROR_CODE = jsonObject.getJSONObject("MSO_OP_CUST_SEARCH_BILL_outputFlist").optString("ERROR_CODE", "408");
						String ERROR_DESCR = jsonObject.getJSONObject("MSO_OP_CUST_SEARCH_BILL_outputFlist").optString("ERROR_DESCR", "NO_ERROR");
						responseEntity = new ResponseEntity<String>(ERROR_CODE+":"+ERROR_DESCR,HttpStatus.INTERNAL_SERVER_ERROR);
					}
					else
						responseEntity = new ResponseEntity<String>("INVALID CUSTOMER ID",HttpStatus.INTERNAL_SERVER_ERROR);
				} 
				catch (Exception e) 
				{
					StringWriter stack = new StringWriter();
					e.printStackTrace(new PrintWriter(stack));
					logger.error("Get Customer Bill Info Log: "+stack);
					responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}







	@RequestMapping(value="/createTicket", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> createTicket(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			CreateTicketExtractor extractor = (CreateTicketExtractor)gson.fromJson(parameters, CreateTicketExtractor.class);
			CreateTicketModel model = (CreateTicketModel)context.getBean("createTicketModel");
			String agentID = (String)validateLoginResponse.get("agentID");
			String userID = (String)validateLoginResponse.get("POID");
			int createTicketResult = model.createTicketResult(extractor, ispDBConnector, loginValidator, agentID, userID);
			String createTicketResponseJson = model.getCreateTicketResponse();
			if(createTicketResult == 200)
				responseEntity = new ResponseEntity<String>(createTicketResponseJson,HttpStatus.OK);
			else if(createTicketResult == 403)
				responseEntity = new ResponseEntity<String>(createTicketResponseJson,HttpStatus.FORBIDDEN);
			else if(createTicketResult == 404)
				responseEntity = new ResponseEntity<String>(createTicketResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(createTicketResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
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

	@RequestMapping(value="/viewTickets", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> viewTickets(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="ACC", required = true) String accountNo) 
	{
		ResponseEntity<String> responseEntity = null;

		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		//PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		Map<String, Object> validateLoginResponse = validator.validateLogin(ispDBConnector, loginValidator);
		loginResult = (Integer)validateLoginResponse.get("result");
		if(loginResult == 200)
		{
			if(accountNo != null && !accountNo.trim().isEmpty())
			{
				CreateTicketModel model = (CreateTicketModel)context.getBean("createTicketModel");
				int viewTicketsResult = model.viewTicketsResult(ispDBConnector, accountNo);
				if(viewTicketsResult == 200)
				{
					String viewTicketsResponse = model.getViewTicketsResponse();
					responseEntity = new ResponseEntity<String>(viewTicketsResponse,HttpStatus.OK);
				}
				else
					responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else
				responseEntity = new ResponseEntity<String>("PLEASE CHECK THE ACCOUNT NUMBER",HttpStatus.BAD_REQUEST);
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