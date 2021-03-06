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
import com.FOS.Extractor.AddPlanExtractor;
import com.FOS.Extractor.CancelPlanExtractor;
import com.FOS.Extractor.ChangePlanExtractor;
import com.FOS.Extractor.PlanCodesExtractor;
import com.FOS.Model.PlanModel;
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
public class PlanController {

	final static Logger logger = LogManager.getLogger(PlanController.class);
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
	/*@RequestMapping(value="/getPlanDetails", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getPlanInfo(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			PlanDetailExtractor extractor = (PlanDetailExtractor)gson.fromJson(parameters, PlanDetailExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			int planInfoResult = model.getPlanInfoResult(extractor, ispDBConnector);
			if(planInfoResult == 200)
			{
				String planInfo = model.getPlanInfo();
				responseEntity = new ResponseEntity<String>(planInfo,HttpStatus.OK);
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

	@RequestMapping(value="/getCustomerPlanDetail", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getCustomerPlanDetail(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			CustomerPlanDetailExtractor extractor = (CustomerPlanDetailExtractor)gson.fromJson(parameters, CustomerPlanDetailExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			int custPlanDetailResult = model.getCustomerPlanDetailResult(extractor, ispDBConnector);
			String custPlanPriceDetail = model.getCustPlanPriceDetail();
			if(custPlanDetailResult == 200)
				responseEntity = new ResponseEntity<String>(custPlanPriceDetail,HttpStatus.OK);
			else if(custPlanDetailResult == 403)
				responseEntity = new ResponseEntity<String>(custPlanPriceDetail,HttpStatus.FORBIDDEN);
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}*/

	@RequestMapping(value="/getPlanList", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getPlanList(@RequestHeader(value="CREDS", required = true) String credentials) 
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
			PlanModel model = (PlanModel)context.getBean("planModel");
			//String accountNo = (String)validateLoginResponse.get("agentAccountNo");
			//int planListResult = model.getPlanListResult(accountNo, ispDBConnector);
			String userID = (String) validateLoginResponse.get("USER_ID");
			String userType = (String) validateLoginResponse.get("USER_TYPE");
			int planListResult = model.getPlanListResult(userID, userType, ispDBConnector);
			if(planListResult == 200)
			{
				String planList = model.getPlanList();
				responseEntity = new ResponseEntity<String>(planList,HttpStatus.OK);
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

	@RequestMapping(value="/getPlanListByID", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getPlanListByID(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PLAN_LIST_ID", required = true) String planListID) 
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
			PlanModel model = (PlanModel)context.getBean("planModel");
			//String accountNo = (String)validateLoginResponse.get("agentAccountNo");
			//int planListResult = model.getPlanListResult(accountNo, ispDBConnector);
			if(planListID != null && !planListID.trim().isEmpty())
			{
				int planListByIDResult = model.getPlanListByIDResult(planListID, ispDBConnector);
				if(planListByIDResult == 200)
				{
					String planByIDList = model.getPlanByIDList();
					responseEntity = new ResponseEntity<String>(planByIDList,HttpStatus.OK);
				}	
				else
					responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
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

	@RequestMapping(value="/getPlanPrice", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getPlanPrice(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
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
			logger.info("Get Plan Price Parameters: "+parmeters);
			PlanCodesExtractor planCodesBean = (PlanCodesExtractor)gson.fromJson(parmeters, PlanCodesExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			int planPriceResult = model.getPlanPriceResult(planCodesBean, ispDBConnector);
			if(planPriceResult == 200)
			{
				String planPriceDetail = model.getPlanPriceDetail();
				responseEntity = new ResponseEntity<String>(planPriceDetail,HttpStatus.OK);
			}	
			else if(loginResult == 201)
				responseEntity = new ResponseEntity<String>("PLAN PRICE NOT FOUND",HttpStatus.INTERNAL_SERVER_ERROR);
			else if(loginResult == 400)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
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

	/*@RequestMapping(value="/getTopUpPlanList", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getTopUpPlanList(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			String city = parameters;
			PlanModel model = (PlanModel)context.getBean("planModel");
			int topUpPlanListResult = model.getTopUpPlanListResult(city, ispDBConnector);
			if(topUpPlanListResult == 200)
			{
				String topUpPlans = model.getTopUpPlans();
				responseEntity = new ResponseEntity<String>(topUpPlans,HttpStatus.OK);
			}	
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}*/


	/*@RequestMapping(value="/getRenewPlanList", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> getRenewPlanList(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
	{
		ResponseEntity<String> responseEntity = null;
		int loginResult = 0;
		LoginValidator loginValidator = null;
		Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new BadDoubleDeserializer()).create();
		loginValidator = (LoginValidator)gson.fromJson(credentials, LoginValidator.class);
		ISPDBConnector ispDBConnector = (ISPDBConnector)context.getBean("ispDBConnector");
		LoginValidationImplementor validator = (LoginValidationImplementor)context.getBean("loginValidator");
		loginResult = validator.validateLogin(ispDBConnector, loginValidator);
		if(loginResult == 200)
		{
			String city = parameters;
			PlanModel model = (PlanModel)context.getBean("planModel");
			int renewPlanListResult = model.getRenewPlanListResult(city, ispDBConnector);
			if(renewPlanListResult == 200)
			{
				String renewPlans = model.getRenewPlans();
				responseEntity = new ResponseEntity<String>(renewPlans,HttpStatus.OK);
			}	
			else
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}*/

	/*@RequestMapping(value="/changePlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> changePlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
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
				ChangePlanExtractor extractor = (ChangePlanExtractor)gson.fromJson(parmeters, ChangePlanExtractor.class);
				PlanModel model = (PlanModel)context.getBean("planModel");
				String agentID = (String)validateLoginResponse.get("agentID");
				int planListResult = model.changePlanResult(extractor, ispDBConnector, loginValidator, agentID);
				if(planListResult == 200)
					responseEntity = new ResponseEntity<String>("PLAN CHANGED SUCCESSFULLY",HttpStatus.OK);
				else
					responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else if(loginResult == 401)
				responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
			else if(loginResult == 500)
				responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
			return responseEntity;
	}

	@RequestMapping(value="/retryChangePlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> retryChangePlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			PlanModel model = (PlanModel)context.getBean("planModel");
			String agentID = (String)validateLoginResponse.get("agentID"), referenceID = parameters;
			int retryChangePlanResult = model.retryChangePlanResult(referenceID, ispDBConnector, agentID);
			String retryChangePlanResponseJson = model.getRetryRenewResponse();
			if(retryChangePlanResult == 200)
				responseEntity = new ResponseEntity<String>(retryChangePlanResponseJson,HttpStatus.OK);
			else if(retryChangePlanResult == 403)
				responseEntity = new ResponseEntity<String>(retryChangePlanResponseJson,HttpStatus.FORBIDDEN);
			else if(retryChangePlanResult == 404)
				responseEntity = new ResponseEntity<String>(retryChangePlanResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(retryChangePlanResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}*/

	@RequestMapping(value="/addPlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> addPlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
	{
		logger.info("Add Plan Request JSON: ");
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
			AddPlanExtractor extractor = (AddPlanExtractor)gson.fromJson(parmeters, AddPlanExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
			int addPlanResult = model.addPlanResult(extractor, ispDBConnector, pinDBConnector, loginValidator, agentID, userID);
			String addPlanResponseJson = model.getAddPlanResponse();
			if(addPlanResult == 200)
				responseEntity = new ResponseEntity<String>(addPlanResponseJson,HttpStatus.OK);
			else if(addPlanResult == 403)
				responseEntity = new ResponseEntity<String>(addPlanResponseJson,HttpStatus.FORBIDDEN);
			else if(addPlanResult == 404)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(addPlanResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
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

	@RequestMapping(value="/changePlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> changePlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
	{
		logger.info("Change Plan Request JSON: ");
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
			ChangePlanExtractor extractor = (ChangePlanExtractor)gson.fromJson(parmeters, ChangePlanExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			int changePlanResult = model.changePlanResult(extractor, ispDBConnector, loginValidator, agentID, userID);
			String changePlanResponseJson = model.getPlanChangeResponse();
			if(changePlanResult == 200)
				responseEntity = new ResponseEntity<String>(changePlanResponseJson,HttpStatus.OK);
			else if(changePlanResult == 403)
				responseEntity = new ResponseEntity<String>(changePlanResponseJson,HttpStatus.FORBIDDEN);
			else if(changePlanResult == 404)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(changePlanResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
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

	@RequestMapping(value="/cancelPlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> cancelPlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
	{
		logger.info("Cancel Plan Request JSON: ");
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
			/*if(Integer.parseInt((String)validateLoginResponse.get("SERVICE_STATUS")) == 0)
			{*/
			String agentID = (String)validateLoginResponse.get("agentID");
			String userID = (String)validateLoginResponse.get("POID");
			CancelPlanExtractor extractor = (CancelPlanExtractor)gson.fromJson(parmeters, CancelPlanExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			int cancelPlanResult = model.cancelPlanResult(extractor, ispDBConnector, loginValidator, agentID, userID);
			String cancelPlanResponseJson = model.getCancelPlanResponse();
			if(cancelPlanResult == 200)
				responseEntity = new ResponseEntity<String>(cancelPlanResponseJson,HttpStatus.OK);
			else if(cancelPlanResult == 403)
				responseEntity = new ResponseEntity<String>(cancelPlanResponseJson,HttpStatus.FORBIDDEN);
			else if(cancelPlanResult == 404)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(cancelPlanResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
			/*}
			else
				responseEntity = new ResponseEntity<String>("CANCEL PLAN NOT ALLOWED",HttpStatus.BAD_REQUEST);*/
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

	/*@RequestMapping(value="/retryFupTopUp", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> retryFupTopUp(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			PlanModel model = (PlanModel)context.getBean("planModel");
			String agentID = (String)validateLoginResponse.get("agentID"), referenceID = parameters;
			int retryFupTopUpResult = model.retryFupTopUpResult(referenceID, ispDBConnector, agentID);
			String retryFupTopUpResponseJson = model.getRetryTopUpResponse();
			if(retryFupTopUpResult == 200)
				responseEntity = new ResponseEntity<String>(retryFupTopUpResponseJson,HttpStatus.OK);
			else if(retryFupTopUpResult == 403)
				responseEntity = new ResponseEntity<String>(retryFupTopUpResponseJson,HttpStatus.FORBIDDEN);
			else if(retryFupTopUpResult == 404)
				responseEntity = new ResponseEntity<String>(retryFupTopUpResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(retryFupTopUpResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/topUpPlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> topUpPlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
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
			String agentID = (String)validateLoginResponse.get("agentID");
			AddPlanExtractor extractor = (AddPlanExtractor)gson.fromJson(parmeters, AddPlanExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			//PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
			int topUpResult = model.topUpResult(extractor, ispDBConnector, loginValidator, agentID);
			String topUpResponseJson = model.getGbTopUpResponse();
			if(topUpResult == 200)
				responseEntity = new ResponseEntity<String>(topUpResponseJson,HttpStatus.OK);
			else if(topUpResult == 403)
				responseEntity = new ResponseEntity<String>(topUpResponseJson,HttpStatus.FORBIDDEN);
			else if(topUpResult == 404)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(topUpResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/retryTopUp", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> retryTopUp(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			PlanModel model = (PlanModel)context.getBean("planModel");
			String agentID = (String)validateLoginResponse.get("agentID"), referenceID = parameters;
			int retryTopUpResult = model.retryTopUpResult(referenceID, ispDBConnector, agentID);
			String retryTopUpResponseJson = model.getRetryTopUpResponse();
			if(retryTopUpResult == 200)
				responseEntity = new ResponseEntity<String>(retryTopUpResponseJson,HttpStatus.OK);
			else if(retryTopUpResult == 403)
				responseEntity = new ResponseEntity<String>(retryTopUpResponseJson,HttpStatus.FORBIDDEN);
			else if(retryTopUpResult == 404)
				responseEntity = new ResponseEntity<String>(retryTopUpResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(retryTopUpResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/renewPlan", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> renewPlan(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parmeters) 
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
			String agentID = (String)validateLoginResponse.get("agentID");
			AddPlanExtractor extractor = (AddPlanExtractor)gson.fromJson(parmeters, AddPlanExtractor.class);
			PlanModel model = (PlanModel)context.getBean("planModel");
			//PinDBConnector pinDBConnector = (PinDBConnector)context.getBean("pinDBConnector");
			int planRenewalResult = model.planRenewalResult(extractor, ispDBConnector, loginValidator, agentID);
			String renewResponseJson = model.getPlanRenewResponse();
			if(planRenewalResult == 200)
				responseEntity = new ResponseEntity<String>(renewResponseJson,HttpStatus.OK);
			else if(planRenewalResult == 403)
				responseEntity = new ResponseEntity<String>(renewResponseJson,HttpStatus.FORBIDDEN);
			else if(topUpResult == 404)
				responseEntity = new ResponseEntity<String>("BAD REQUEST",HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(renewResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}

	@RequestMapping(value="/retryPlanRenew", method = RequestMethod.POST, produces="text/plain")
	public ResponseEntity<String> retryPlanRenew(@RequestHeader(value="CREDS", required = true) String credentials, @RequestHeader(value="PARAMS", required = true) String parameters) 
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
			PlanModel model = (PlanModel)context.getBean("planModel");
			String agentID = (String)validateLoginResponse.get("agentID"), referenceID = parameters;
			int retryPlanRenewResult = model.retryPlanRenewResult(referenceID, ispDBConnector, agentID);
			String retryPlanRenewResponseJson = model.getRetryRenewResponse();
			if(retryPlanRenewResult == 200)
				responseEntity = new ResponseEntity<String>(retryPlanRenewResponseJson,HttpStatus.OK);
			else if(retryPlanRenewResult == 403)
				responseEntity = new ResponseEntity<String>(retryPlanRenewResponseJson,HttpStatus.FORBIDDEN);
			else if(retryPlanRenewResult == 404)
				responseEntity = new ResponseEntity<String>(retryPlanRenewResponseJson,HttpStatus.BAD_REQUEST);
			else
				responseEntity = new ResponseEntity<String>(retryPlanRenewResponseJson,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else if(loginResult == 401)
			responseEntity = new ResponseEntity<String>("UNAUTHORIZED",HttpStatus.UNAUTHORIZED);
		else if(loginResult == 500)
			responseEntity = new ResponseEntity<String>("INTERNAL SERVER ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
		return responseEntity;
	}*/
}
