package imademethink_jayway_simple.StepImplimentations;

import static com.jayway.restassured.RestAssured.with;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import cucumber.api.DataTable;
import junit.framework.Assert;

public class BddStepImplimentation_Simple_REST_API_Testing {

	public String basicApplicationURL = null;
	public String currentAPIEndPoint  = null;
	public String headerParamRequestContentType = null;
	public String headerParamResponseAcceptType = null;
	
	private boolean globalSuccessFlag 						           = false;
	public String serverLogFileBasePath                               = "D:\\Software\\apache-tomcat-8.0.30\\logs";
	public static String serverLogFileNameStartWith         = "localhost_access_log";
	private static String latestApacheServerLogFileName;
	private long latestApacheServerLogFileLineCount        = -1;
	public static String defaultJSONresponsePath;

	public GeneralUtilities genUtil 								  = null;
	private boolean isWebAppRunning                       = false;
	public String webAppEndpoint                               = null;
	private Response responseJayWay                        = null;
	private RequestSpecification requestspecs           = null;
	private String responseInString                              = null;
	private int responseCode                                        = -1;
	private String strContentTypeOfResponse            = null;
	private JSONObject jsonobject                              = null;	
	
	// user detail parameters
	public String emailid 				                   = null;
	public String password 			                       = null;
	public static String firstname 			           = null;
	public static String lastname 			           = null;
	public static String gender 				           = null;
	public static String secretquestion1           = null;
	public static String secretquestion2           = null;
	public static String secretanswer1             = null;
	public static String secretanswer2             = null;
	public static String activationkey 		      = null;
	public static String sessionid 			          = null;

    HashMap<String,Object> objHashMapGetDocumentationDetails                		= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapBasicUserDetails                						= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapErrorResp                          							= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapSignupGET                         						= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapSignupPOST                      							= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapActivateGET                       						= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapSigninPOST                        						= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapSignoutDELETE                  						= new HashMap<String,Object>();
    HashMap<String,Object> objHashMapGetAccountProfileDetailGET                   = new HashMap<String,Object>();
    HashMap<String,Object> objHashMapModifyAccountProfileDetailPUT             = new HashMap<String,Object>();
    HashMap<String,Object> objHashMapForgetPasswordGetSecretQuestionGET = new HashMap<String,Object>();
    HashMap<String,Object> objHashMapResetPasswordPUT                                   = new HashMap<String,Object>();
    HashMap<String,Object> params                                                                            = new HashMap<String,Object>();

	public BddStepImplimentation_Simple_REST_API_Testing(){
		genUtil  = new GeneralUtilities();
	}

	public void ParseBasicUserDetails(DataTable userDetailDataTable){	
		List<List<String>> userDetailList = userDetailDataTable.raw();
		emailid                = userDetailList.get(1).get(1).toString().trim();
		password            = userDetailList.get(2).get(1).toString().trim();
		firstname            = userDetailList.get(3).get(1).toString().trim();
		lastname             = userDetailList.get(4).get(1).toString().trim();
		gender                 = userDetailList.get(5).get(1).toString().trim();
		secretquestion1 = userDetailList.get(6).get(1).toString().trim();
		secretquestion2  = userDetailList.get(7).get(1).toString().trim();
		secretanswer1    = userDetailList.get(8).get(1).toString().trim();
		secretanswer2    = userDetailList.get(9).get(1).toString().trim();
		if(userDetailList.get(10).get(1).toString().trim().contains("empty"))  sessionid  = "";
		emailid = emailid.replace("01", Integer.toString(new Random().nextInt(80) + 11));
		
		// do this before each scenario
		objHashMapGetDocumentationDetails.clear();
        objHashMapBasicUserDetails.clear();        
        objHashMapErrorResp.clear();
        objHashMapSignupGET.clear();
        objHashMapSignupPOST.clear();
        objHashMapActivateGET.clear();
        objHashMapSigninPOST.clear();
        objHashMapSignoutDELETE.clear();
        objHashMapGetAccountProfileDetailGET.clear();
        objHashMapModifyAccountProfileDetailPUT.clear();
        objHashMapForgetPasswordGetSecretQuestionGET.clear();
        objHashMapResetPasswordPUT.clear();

		objHashMapGetDocumentationDetails = fill_GetDocumentationGET();
        objHashMapBasicUserDetails                 = fill_BasicUserDetails(this.emailid,this.password);        
        objHashMapErrorResp                            = fill_ErrorResponse();
        objHashMapSignupGET                          = fill_SignupGET();
        objHashMapSignupPOST                        = fill_SignupPOST();
        objHashMapActivateGET                        = fill_ActivateGET();
        objHashMapSigninPOST                         = fill_SigninPOST();
        objHashMapSignoutDELETE                   = fill_SignoutDELETE();
        objHashMapGetAccountProfileDetailGET                	= fill_GetAccountProfileDetailGET();
        objHashMapModifyAccountProfileDetailPUT            	= fill_ModifyAccountProfileDetailPUT();
        objHashMapForgetPasswordGetSecretQuestionGET = fill_ForgetPasswordGetSecretQuestionGET();
        objHashMapResetPasswordPUT                            	     = fill_ResetPasswordPUT();
	}

	public void setQueryParam(String queryParam){
		if (queryParam.contains("empty")) return;
		
		if (queryParam.contains("based on user details")) {
			if(null == objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_value_emailid")){
				Assert.fail("Basic user details are not present.");
			}
		}

	}

	public void rasieFollowingHTTPMethod(String httpMethod){
		switch(httpMethod) {
		   case "GET" :
			  requestspecs = setup_Connection();
		      try {responseJayWay =  requestspecs.get(basicApplicationURL + currentAPIEndPoint);}
		      catch (Exception e){
		    	  if(e.getMessage().contains("Connection refused")){Assert.fail("Web application is not running.");return;}
		          else{Assert.fail("Unknown exception while invoking this request.");e.printStackTrace();}return;
		      }
		      break;
		   case "POST" :
			  requestspecs = setup_Connection();
			  requestspecs.with().formParameters(params);
			  try {responseJayWay =  requestspecs.post(basicApplicationURL + currentAPIEndPoint);}
			  catch (Exception e){
					if(e.getMessage().contains("Connection refused")){Assert.fail("Web application is not running.");return;}
				    else{Assert.fail("Unknown exception while invoking this request.");e.printStackTrace();}return;}
		      break;
		   case "PUT" :
			  requestspecs = setup_Connection();
			  requestspecs.with().formParameters(params);
			  try {responseJayWay =  requestspecs.put(basicApplicationURL + currentAPIEndPoint);}
			  catch (Exception e){
					if(e.getMessage().contains("Connection refused")){Assert.fail("Web application is not running.");return;}
				    else{Assert.fail("Unknown exception while invoking this request.");e.printStackTrace();}return;}
		      break;
		   case "DELETE" :
			  requestspecs = setup_Connection();
		      requestspecs.with()
					.queryParam(	
							objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_key_emailid").toString(),
							objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_value_emailid").toString())
					.queryParam(	
							objHashMapBasicUserDetails.get("str_basic_account_session_key").toString(),
							objHashMapBasicUserDetails.get("str_basic_account_session_value").toString());
		        try {responseJayWay =  requestspecs.delete(basicApplicationURL + currentAPIEndPoint);}
		        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
		                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
		      break;
		   default :
			   	  Assert.fail("This type of method is not supported yet.");
		}
	}
	
	public void validateHTTPresponseShouldBeValid(){
		responseInString   = responseJayWay.asString();
		if(null == responseInString){
			Assert.fail("Valid HTTP response is not received");
		}
	}
	
	public void validateHTTPresponseCode(int expectedHttpResponseCode){
		responseCode  = responseJayWay.getStatusCode();
		if(responseCode != expectedHttpResponseCode){
			Assert.fail("Expected response code : "  + expectedHttpResponseCode + 
					          " and actual response code : " + responseCode + 
					          " are not matching");
		}
	}
	
	public void validateHTTPresponseContentType(String headerParamResponseContentType){
		strContentTypeOfResponse     = responseJayWay.getContentType();
		if( ! strContentTypeOfResponse.contains(headerParamResponseContentType)){
			Assert.fail("Valid HTTP response content type is not received: " + strContentTypeOfResponse);
		}
	}
	
	public void validateHTTPresponseBodyShouldNotBeNull(){
		try {
			jsonobject  = parse_StringResponse(responseInString);
			if( null == jsonobject){Assert.fail("Valid HTTP response body type is not received");}
		}catch (Exception e) {Assert.fail("Exception occured while parsing HTTP response");}
	}
	
	public void validateHTTPresponseBodyShouldBeSuccessfulFor(String httpMethod_APIendPoint){
		switch(httpMethod_APIendPoint) {
		   case "GET__signup" :
			  validateResponseBodyFor_GET__signup();
		      break;
		   case "POST__signup" :
			   validateResponseBodyFor_POST__signup();
		      break;
		   case "PUT__modify_account_profile_details" :
			  validateResponseBodyFor_PUT__modify_account_profile_details();
			  break;
		   case "DELETE__signout" :
			  validateResponseBodyFor_DELETE__signout();
			  break;
		   default :
			   Assert.fail("This is not supported something is wrogn===========================");
		}
	}
	
	private void validateResponseBodyFor_GET__signup(){
        System.out.println("\nFollowing success response received from web application.");
		JSONObject jsonobject_internal     = null;

        System.out.println(objHashMapSignupGET.get("str_signup_GET_response_key1").toString()+":"+jsonobject.get(objHashMapSignupGET.get("str_signup_GET_response_key1").toString()));
        System.out.println(objHashMapSignupGET.get("str_signup_GET_response_key2").toString()+":"+jsonobject.get(objHashMapSignupGET.get("str_signup_GET_response_key2").toString())); 
        System.out.println(objHashMapSignupGET.get("str_signup_GET_response_key3").toString()+":");
        jsonobject_internal             = (JSONObject) jsonobject.get(objHashMapSignupGET.get("str_signup_GET_response_key3").toString());

        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_emailid").toString()   +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_emailid")));
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_firstname").toString() +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_firstname"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_lastname").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_lastname"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_gender").toString()    +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_gender"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_password").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_password"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_1").toString()         +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_1"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_2").toString()         +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_2"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_1").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_1"))); 
        System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_2").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_2"))); 
 		System.out.println("Links: ");
 		JSONObject obj_json_Links = (JSONObject) jsonobject.get("Links");
 		System.out.println("     Link documentation:"    +  obj_json_Links.get("Link documentation"));
 		System.out.println("     Actual signup:"            +  obj_json_Links.get("Actual signup"));
 		
 		System.out.println("\n\nResponse BODY parsing for GET__signup is successful\n\n");
	}
	
	private void validateResponseBodyFor_POST__signup(){
        System.out.println("\nFollowing success response received from web application.");
        System.out.println(objHashMapSignupPOST.get("str_signup_POST_response_key1").toString()+":"+jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key1").toString()));
        System.out.println(objHashMapSignupPOST.get("str_signup_POST_response_key2").toString()+":"+jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key2").toString())); 
        System.out.println(objHashMapSignupPOST.get("str_signup_POST_response_key3").toString()+":"+jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key3").toString()) );
        // insert activation key in basic user hashmap variable
        objHashMapBasicUserDetails.put("str_basic_account_activation_value",jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key3").toString()));

 		System.out.println("\n\nResponse BODY parsing for POST__signup is successful\n\n");
	}
	
	private void validateResponseBodyFor_PUT__modify_account_profile_details(){
        System.out.println("\nFollowing success response received from web application.");
        System.out.println(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key1").toString()+":"+jsonobject.get(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key1").toString()));
        System.out.println(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key2").toString()+":"+jsonobject.get(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key2").toString())); 
        System.out.println(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key3").toString()+":"+jsonobject.get(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key3").toString()) );
        
 		System.out.println("\n\nResponse BODY parsing for PUT__modify_account_profile_details is successful\n\n");
	}
	
	public void validateResponseBodyFor_DELETE__signout(){
        System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key1").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key1").toString()));
        System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key2").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key2").toString())); 
        System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key3").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key3").toString()) );
        
        // during successful sign out set the session id / value to empty
        objHashMapBasicUserDetails.put("str_basic_account_session_value","");
        objHashMapBasicUserDetails.put("str_basic_account_latest_session_value","");

 		System.out.println("\n\nResponse BODY parsing for DELETE__signout is successful\n\n");
	}

	
	
	
	
	
	
	
	
	public void setBodyFormParamUsingBasicUserDetails(){
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_emailid").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_emailid").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_firstname").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_firstname").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_lastname").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_lastname").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_gender").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_gender").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_password").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_password").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_1").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_1").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_2").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_2").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_answer_1").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_answer_1").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_answer_2").toString(),
                   objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_answer_2").toString());
	}
	
	public void modifyBodyFormParamFirstNameAndLastName(String first_Name, String last_Name){
        objHashMapBasicUserDetails.put("str_modifyaccountprofiledetail_PUT_response_payload_value_firstname",first_Name);
        objHashMapBasicUserDetails.put("str_modifyaccountprofiledetail_PUT_response_payload_value_lastname", last_Name);
        params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_emailid").toString(),
                objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_emailid").toString());
	     params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_firstname").toString(),
	                objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_firstname").toString());
	     params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_lastname").toString(),
	                objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_lastname").toString());
	     params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_gender").toString(),
	                objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_gender").toString());
	     params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_password").toString(),
	                objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_password").toString());
	     params.put(objHashMapBasicUserDetails.get("str_basic_account_latest_session_key").toString(),
	                    objHashMapBasicUserDetails.get("str_basic_account_latest_session_value").toString());
	}
	
	public void performSetupForPUTrequest(){
		currentAPIEndPoint                         = "signup";
		headerParamRequestContentType = "application/x-www-form-urlencoded";
		headerParamResponseAcceptType = "application/json";
		String emailidTemp = "Happy01@aaa.com".replace("01", Integer.toString(new Random().nextInt(80) + 11));
		try {POST_signup_actual(emailidTemp);}catch (Exception e) {}
		
		currentAPIEndPoint                         = "activate";
		headerParamRequestContentType = "application/json";
		headerParamResponseAcceptType = "application/json";
		try {GET_activate_basic_account();}catch (Exception e) {}

		currentAPIEndPoint                         = "signin";
		headerParamRequestContentType = "application/x-www-form-urlencoded";
		headerParamResponseAcceptType = "application/json";
		try {POST_signin_actual();}catch (Exception e) {}

		currentAPIEndPoint                         = "get_account_profile_details";
		headerParamRequestContentType = "application/json";
		headerParamResponseAcceptType = "application/json";
		try {GET_basic_account_profile_detail();}catch (Exception e) {}
	}
	
	public void performSetupForDELETErequest(){
		currentAPIEndPoint                         = "signup";
		headerParamRequestContentType = "application/x-www-form-urlencoded";
		headerParamResponseAcceptType = "application/json";
		String emailidTemp = "Happy01@aaa.com".replace("01", Integer.toString(new Random().nextInt(80) + 11));
		try {POST_signup_actual(emailidTemp);}catch (Exception e) {}
		
		currentAPIEndPoint                         = "activate";
		headerParamRequestContentType = "application/json";
		headerParamResponseAcceptType = "application/json";
		try {GET_activate_basic_account();}catch (Exception e) {}
		
		currentAPIEndPoint                         = "signin";
		headerParamRequestContentType = "application/x-www-form-urlencoded";
		headerParamResponseAcceptType = "application/json";
		try {POST_signin_actual();}catch (Exception e) {}		
	}
	
	public void GET_Documentation() throws Exception {
        
        System.out.println("----------------------------------------------------------------------------");
        String url                 = objHashMapGetDocumentationDetails.get("str_getdocumentation_GET_url").toString();

		Response responseJayWay = null;
		RequestSpecification requestspecs = setup_Connection();
		
        // set up connection and call method
        try {responseJayWay =  requestspecs.get(url);}
        catch (Exception e){
        	if(e.getMessage().contains("Connection refused")){
        		//System.out.println("Web application is not running.");
    			Assert.fail("ERROR     :Web application is not running. So can not test functionality.");
        		return;
        	}
            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;
        }
        isWebAppRunning = true;
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		String responseInString                   = responseJayWay.asString();
		int responseCode                             = responseJayWay.getStatusCode();
		String strContentTypeOfResponse = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject                   = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        if (responseCode >= 400 && responseCode < 600 ){
            jsonobject                 = parse_StringResponse(responseInString);
            if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
            if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	
        }

        if (responseCode == 200){
            String tempDownloadPath = "G:\\zzz123\\downloaded_file.xlsx";
            if(!genUtil.DownloadAndSaveAttachment(tempDownloadPath,responseJayWay)){
            	System.out.println("No data found in downloadable content, empty or no content found.");
            	return;
            }
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }
	
	public void GET_signup_details() throws Exception {

        System.out.println("----------------------------------------------------------------------------");
        String url                 = objHashMapSignupGET.get("str_signup_GET_url").toString();

		Response responseJayWay = null;
		RequestSpecification requestspecs = setup_Connection();
		
        // set up connection and call method
        try {responseJayWay =  requestspecs.get(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject               = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}
        
        jsonobject                 = parse_StringResponse(responseInString);
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            
            JSONObject jsonobject_internal     = null;

            System.out.println(objHashMapSignupGET.get("str_signup_GET_response_key1").toString()+":"+jsonobject.get(objHashMapSignupGET.get("str_signup_GET_response_key1").toString()));
            System.out.println(objHashMapSignupGET.get("str_signup_GET_response_key2").toString()+":"+jsonobject.get(objHashMapSignupGET.get("str_signup_GET_response_key2").toString())); 
            System.out.println(objHashMapSignupGET.get("str_signup_GET_response_key3").toString()+":");
            jsonobject_internal             = (JSONObject) jsonobject.get(objHashMapSignupGET.get("str_signup_GET_response_key3").toString());

            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_emailid").toString()   +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_emailid")));
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_firstname").toString() +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_firstname"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_lastname").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_lastname"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_gender").toString()    +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_gender"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_password").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_password"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_1").toString()         +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_1"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_2").toString()         +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_2"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_1").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_1"))); 
            System.out.println("     "+ objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_2").toString()  +":"+jsonobject_internal.get(objHashMapSignupGET.get("str_signup_GET_response_payload_key_secret_question_answer_2"))); 

     		System.out.println("Links: ");
     		JSONObject obj_json_Links = (JSONObject) jsonobject.get("Links");
     		System.out.println("     Link documentation:"    +  obj_json_Links.get("Link documentation"));
     		System.out.println("     Actual signup:"            +  obj_json_Links.get("Actual signup"));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }

    public void POST_signup_actual(String emailId) throws Exception {
    	
    	this.emailid = emailId;
    	objHashMapBasicUserDetails  = fill_BasicUserDetails(this.emailid,this.password);

        System.out.println("----------------------------------------------------------------------------");
        String url                 = objHashMapSignupPOST.get("str_signup_POST_url").toString();

        Map<String,Object> params = new LinkedHashMap<String,Object>();
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_emailid").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_emailid").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_firstname").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_firstname").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_lastname").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_lastname").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_gender").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_gender").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_password").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_password").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_1").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_1").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_2").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_2").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_answer_1").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_answer_1").toString());
        params.put(objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_secret_question_answer_2").toString(),
        		objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_secret_question_answer_2").toString());

		Response responseJayWay = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().formParameters(params);
		
		// set up connection and call method
		try {responseJayWay =  requestspecs.post(url);}
		catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
		        else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
		System.out.println("Response Code               : HTTP " + responseCode);
		System.out.println("Content type of response is : " + strContentTypeOfResponse);
		JSONObject jsonobject     = null;
		
        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 201){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapSignupPOST.get("str_signup_POST_response_key1").toString()+":"+jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key1").toString()));
            System.out.println(objHashMapSignupPOST.get("str_signup_POST_response_key2").toString()+":"+jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key2").toString())); 
            System.out.println(objHashMapSignupPOST.get("str_signup_POST_response_key3").toString()+":"+jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key3").toString()) );
            // insert activation key in basic user hashmap variable
            objHashMapBasicUserDetails.put("str_basic_account_activation_value",jsonobject.get(objHashMapSignupPOST.get("str_signup_POST_response_key3").toString()));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }

    public void GET_activate_basic_account() throws Exception {
        
        System.out.println("----------------------------------------------------------------------------");
        if(objHashMapBasicUserDetails.get("str_basic_account_activation_value").toString().isEmpty()){
            System.out.println("This test can not be performed as activation code is empty.");
            return;
        }

        // build the query param string using string builder first
        StringBuilder requestUrl   = new StringBuilder(objHashMapActivateGET.get("str_activate_GET_url").toString());
        String url                 = requestUrl.toString();

		Response responseJayWay  = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().queryParam(	objHashMapBasicUserDetails.get("str_signup_POST_response_payload_key_emailid").toString(),
				objHashMapBasicUserDetails.get("str_signup_POST_response_payload_value_emailid").toString())
						   .queryParam(	objHashMapBasicUserDetails.get("str_basic_account_activation_key").toString(),
								   objHashMapBasicUserDetails.get("str_basic_account_activation_value").toString());

        // set up connection and call method
        try {responseJayWay =  requestspecs.get(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject     = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapActivateGET.get("str_activate_GET_response_key1").toString()+":"+jsonobject.get(objHashMapActivateGET.get("str_activate_GET_response_key1").toString()));
            System.out.println(objHashMapActivateGET.get("str_activate_GET_response_key2").toString()+":"+jsonobject.get(objHashMapActivateGET.get("str_activate_GET_response_key2").toString())); 
            System.out.println(objHashMapActivateGET.get("str_activate_GET_response_key3").toString()+":"+jsonobject.get(objHashMapActivateGET.get("str_activate_GET_response_key3").toString()) );
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }
    
    public void POST_signin_actual() throws Exception {

        System.out.println("----------------------------------------------------------------------------");
        String url                 = objHashMapSigninPOST.get("str_signin_POST_url").toString();

        Map<String,Object> params = new LinkedHashMap<String,Object>();
        params.put(objHashMapBasicUserDetails.get("str_signin_POST_response_payload_key_emailid").toString(),                      
        		objHashMapBasicUserDetails.get("str_signin_POST_response_payload_value_emailid").toString());
        params.put(objHashMapBasicUserDetails.get("str_signin_POST_response_payload_key_password").toString(),                      
        		objHashMapBasicUserDetails.get("str_signin_POST_response_payload_value_password").toString());

		Response responseJayWay = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().formParameters(params);
		
		// set up connection and call method
		try {responseJayWay =  requestspecs.post(url);}
		catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
		        else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject               = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapSigninPOST.get("str_signin_POST_response_key1").toString()+":"+jsonobject.get(objHashMapSigninPOST.get("str_signin_POST_response_key1").toString()));
            System.out.println(objHashMapSigninPOST.get("str_signin_POST_response_key2").toString()+":"+jsonobject.get(objHashMapSigninPOST.get("str_signin_POST_response_key2").toString())); 
            System.out.println(objHashMapSigninPOST.get("str_signin_POST_response_key3").toString()+":"+jsonobject.get(objHashMapSigninPOST.get("str_signin_POST_response_key3").toString()) );
            // insert activation key in basic user hashmap variable
            objHashMapBasicUserDetails.put("str_basic_account_session_value",jsonobject.get(objHashMapSigninPOST.get("str_signin_POST_response_key3").toString()));
            objHashMapBasicUserDetails.put("str_basic_account_latest_session_value",jsonobject.get(objHashMapSigninPOST.get("str_signin_POST_response_key3").toString()));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }

    public void DELETE_signout_basic_account(String whichScenario) throws Exception {

    	if(whichScenario.contains("invalid scenario 1")){
            objHashMapBasicUserDetails.put("str_basic_account_session_value",             "12345678901234567890123456789012");
            objHashMapBasicUserDetails.put("str_basic_account_latest_session_value",  "12345678901234567890123456789012");
    	}
    	
        System.out.println("----------------------------------------------------------------------------");
        if(objHashMapBasicUserDetails.get("str_basic_account_session_value").toString().isEmpty()){
            System.out.println("This test can not be performed as session value is empty.");
            return;
        }
        
        // build the query param string using string builder first
        StringBuilder requestUrl = new StringBuilder(objHashMapSignoutDELETE.get("str_signout_DELETE_url").toString());
        String url               = requestUrl.toString();

		Response responseJayWay  = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with()
			.queryParam(	
					objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_key_emailid").toString(),
					objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_value_emailid").toString())
			.queryParam(	objHashMapBasicUserDetails.get("str_basic_account_session_key").toString(),
					objHashMapBasicUserDetails.get("str_basic_account_session_value").toString());

        // set up connection and call method
        try {responseJayWay =  requestspecs.delete(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject     = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key1").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key1").toString()));
            System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key2").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key2").toString())); 
            System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key3").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key3").toString()) );
            
            // during successful sign out set the session id / value to empty
            objHashMapBasicUserDetails.put("str_basic_account_session_value","");
            objHashMapBasicUserDetails.put("str_basic_account_latest_session_value","");
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }

    public void DELETE_signout_basic_account_special() throws Exception {

    	System.out.println("----------------------------------------------------------------------------");
        if(objHashMapBasicUserDetails.get("str_basic_account_session_value").toString().isEmpty()){
            System.out.println("This test can not be performed as session value is empty.");
            return;
        }
        
        // build the query param string using string builder first
        StringBuilder requestUrl = new StringBuilder(objHashMapSignoutDELETE.get("str_signout_DELETE_url").toString());
        String url               = requestUrl.toString();

		Response responseJayWay  = null;
		// special case
		headerParamRequestContentType = MediaType.APPLICATION_JSON;
    	headerParamResponseAcceptType = MediaType.APPLICATION_XML;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().queryParam(	objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_key_emailid").toString(),
				                       	objHashMapBasicUserDetails.get("str_signout_DELETE_response_payload_value_emailid").toString())
						   .queryParam(	objHashMapBasicUserDetails.get("str_basic_account_session_key").toString(),
										objHashMapBasicUserDetails.get("str_basic_account_session_value").toString());

        // set up connection and call method
        try {responseJayWay =  requestspecs.delete(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject     = null;

        if (null == strContentTypeOfResponse){
            System.out.println("Raw response is : " + strContentTypeOfResponse);
            return;
        }

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key1").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key1").toString()));
            System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key2").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key2").toString())); 
            System.out.println(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key3").toString()+":"+jsonobject.get(objHashMapSignoutDELETE.get("str_signout_DELETE_response_key3").toString()) );
            
            // during successful sign out set the session id / value to empty
            objHashMapBasicUserDetails.put("str_basic_account_session_value","");
            objHashMapBasicUserDetails.put("str_basic_account_latest_session_value","");
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }
    
    public void GET_basic_account_profile_detail() throws Exception {

        System.out.println("----------------------------------------------------------------------------");
        if(objHashMapBasicUserDetails.get("str_basic_account_latest_session_value").toString().isEmpty()){
            System.out.println("This test can not be performed as latest session value is empty.");
            return;
        }

        // build the query param string using string builder first
        StringBuilder requestUrl   = new StringBuilder(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_url").toString());
        String url                 = requestUrl.toString();

		Response responseJayWay  = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().queryParam(	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_key_emailid").toString(),
				                       	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_value_emailid").toString())
						   .queryParam(	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_key_password").toString(),
										objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_value_password").toString())
						   .queryParam(	objHashMapBasicUserDetails.get("str_basic_account_latest_session_key").toString(),
								   		objHashMapBasicUserDetails.get("str_basic_account_latest_session_value").toString());

        // set up connection and call method
        try {responseJayWay =  requestspecs.get(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject     = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            JSONObject jsonobject_internal     = null;

            System.out.println(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key1").toString()+":"+jsonobject.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key1").toString()));
            System.out.println(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key2").toString()+":"+jsonobject.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key2").toString())); 
            System.out.println(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key3").toString()+":");
            jsonobject_internal     = (JSONObject) jsonobject.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key3").toString());

            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_emailid").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_emailid")));
            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_firstname").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_firstname")));
            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_lastname").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_lastname")));
            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_gender").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_gender")));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }

    public void GET_basic_account_profile_detail_special() throws Exception {

        System.out.println("----------------------------------------------------------------------------");
        if(objHashMapBasicUserDetails.get("str_basic_account_latest_session_value").toString().isEmpty()){
            System.out.println("This test can not be performed as latest session value is empty.");
            return;
        }

        // build the query param string using string builder first
        StringBuilder requestUrl   = new StringBuilder(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_url").toString());
        String url                 = requestUrl.toString();

		Response responseJayWay  = null;
		// special case
		headerParamRequestContentType = MediaType.APPLICATION_XML;
    	headerParamResponseAcceptType = MediaType.APPLICATION_JSON;    			
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().queryParam(	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_key_emailid").toString(),
				                       	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_value_emailid").toString())
						   .queryParam(	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_key_password").toString(),
										objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_value_password").toString())
						   .queryParam(	objHashMapBasicUserDetails.get("str_basic_account_latest_session_key").toString(),
								   		objHashMapBasicUserDetails.get("str_basic_account_latest_session_value").toString());

        // set up connection and call method
        try {responseJayWay =  requestspecs.get(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject     = null;

        if (null == strContentTypeOfResponse){
            System.out.println("Raw response is : " + responseInString);
            return;
        }
        
        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            JSONObject jsonobject_internal     = null;

            System.out.println(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key1").toString()+":"+jsonobject.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key1").toString()));
            System.out.println(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key2").toString()+":"+jsonobject.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key2").toString())); 
            System.out.println(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key3").toString()+":");
            jsonobject_internal     = (JSONObject) jsonobject.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_response_key3").toString());

            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_emailid").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_emailid")));
            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_firstname").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_firstname")));
            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_lastname").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_lastname")));
            System.out.println("     "+ objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_gender").toString()       +":"+jsonobject_internal.get(objHashMapGetAccountProfileDetailGET.get("str_getaccountprofiledetail_GET_key_gender")));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }
    
    public void PUT_modify_basic_account_profile_detail(String firstName, String lastName, String whichScenario) throws Exception {

        if(whichScenario.contains("valid scenario 2")){
	        objHashMapBasicUserDetails.put("str_modifyaccountprofiledetail_PUT_response_payload_value_firstname",    firstName);
	        objHashMapBasicUserDetails.put("str_modifyaccountprofiledetail_PUT_response_payload_value_lastname",     lastName); 
        }

        System.out.println("----------------------------------------------------------------------------");
        String url                 = objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_url").toString();

        Map<String,Object> params = new LinkedHashMap<String,Object>();
        params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_emailid").toString(),
                   objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_emailid").toString());
        params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_firstname").toString(),
                   objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_firstname").toString());
        params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_lastname").toString(),
                   objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_lastname").toString());
        params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_gender").toString(),
                   objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_gender").toString());
        params.put(objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_key_password").toString(),
                   objHashMapBasicUserDetails.get("str_modifyaccountprofiledetail_PUT_response_payload_value_password").toString());
        params.put(objHashMapBasicUserDetails.get("str_basic_account_latest_session_key").toString(),
                       objHashMapBasicUserDetails.get("str_basic_account_latest_session_value").toString());

		Response responseJayWay = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().formParameters(params);
		
		// set up connection and call method
		try {responseJayWay =  requestspecs.put(url);}
		catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
		        else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject               = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key1").toString()+":"+jsonobject.get(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key1").toString()));
            System.out.println(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key2").toString()+":"+jsonobject.get(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key2").toString())); 
            System.out.println(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key3").toString()+":"+jsonobject.get(objHashMapModifyAccountProfileDetailPUT.get("str_modifyaccountprofiledetail_PUT_response_key3").toString()) );
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }

    public void GET_forget_password_get_secret_question() throws Exception {

        System.out.println("----------------------------------------------------------------------------");

        // build the query param string using string builder first
        StringBuilder requestUrl   = new StringBuilder(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_url").toString());
        String url                 = requestUrl.toString();

		Response responseJayWay  = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().queryParam(	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_key_emailid").toString(),
				                       	objHashMapBasicUserDetails.get("str_getaccountprofiledetail_GET_response_payload_value_emailid").toString());

        // set up connection and call method
        try {responseJayWay =  requestspecs.get(url);}
        catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
                            else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString             = responseJayWay.asString();
		int responseCode                    = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject     = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            JSONObject jsonobject_internal     = null;

            System.out.println(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_response_key1").toString()+":"+jsonobject.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_response_key1").toString()));
            System.out.println(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_response_key2").toString()+":"+jsonobject.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_response_key2").toString())); 
            System.out.println(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_response_key3").toString()+":");
            jsonobject_internal     = (JSONObject) jsonobject.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_response_key3").toString());

            System.out.println("     "+ objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_key_question1").toString()       +":"+jsonobject_internal.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_key_question1")));
            System.out.println("     "+ objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_key_question2").toString()       +":"+jsonobject_internal.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_key_question2")));
            
            // insert secret question in basic user hashmap variable
            objHashMapBasicUserDetails.put("str_signup_POST_response_payload_value_secret_question_1",jsonobject.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_key_question1").toString()));
            objHashMapBasicUserDetails.put("str_signup_POST_response_payload_value_secret_question_2",jsonobject.get(objHashMapForgetPasswordGetSecretQuestionGET.get("str_forgetpasswordgetsecretquestion_GET_key_question2").toString()));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}

        System.out.println("----------------------------------------------------------------------------");
    }
    
    public void PUT_reset_password() throws Exception {

        System.out.println("----------------------------------------------------------------------------");
        String url                 = objHashMapResetPasswordPUT.get("str_resetpassword_PUT_url").toString();

        Map<String,Object> params = new LinkedHashMap<String,Object>();
        params.put(objHashMapBasicUserDetails.get("str_resetpassword_PUT_response_payload_key_emailid").toString(),
                   objHashMapBasicUserDetails.get("str_resetpassword_PUT_response_payload_value_emailid").toString());
        params.put(objHashMapBasicUserDetails.get("str_forgetpassword_PUT_response_payload_key_secret_question_answer_1").toString(),
                   objHashMapBasicUserDetails.get("str_forgetpassword_PUT_response_payload_value_secret_question_answer_1").toString());
        params.put(objHashMapBasicUserDetails.get("str_forgetpassword_PUT_response_payload_key_secret_question_answer_2").toString(),
                   objHashMapBasicUserDetails.get("str_forgetpassword_PUT_response_payload_value_secret_question_answer_2").toString());

		Response responseJayWay = null;
		RequestSpecification requestspecs = setup_Connection();
		requestspecs.with().formParameters(params);
		
		// set up connection and call method
		try {responseJayWay =  requestspecs.put(url);}
		catch (Exception e){if(e.getMessage().contains("Connection refused")){System.out.println("Web application is not running.");return;}
		        else{System.out.println("Unknown exception while invoking this request.");e.printStackTrace();}return;}
        
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        
        // response handling
		String responseInString                      = responseJayWay.asString();
		int responseCode                                = responseJayWay.getStatusCode();
		String strContentTypeOfResponse     = responseJayWay.getContentType();
        System.out.println("Response Code               : HTTP " + responseCode);
        System.out.println("Content type of response is : " + strContentTypeOfResponse);
        JSONObject jsonobject                      = null;

        if(! validateResponseContentType(strContentTypeOfResponse)){return;}

        jsonobject                 = parse_StringResponse(responseInString);        
        if (! genUtil.handleClientSideError4xx(responseCode,responseInString, jsonobject,objHashMapErrorResp)){return;}        
        if (! genUtil.handleServerSideError5xx(responseCode,responseInString, jsonobject, objHashMapErrorResp)){return;}        	

        if (responseCode == 200){
            System.out.println("\nFollowing success response received from web application.");
            System.out.println(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key1").toString()+":"+jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key1").toString()));
            System.out.println(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key2").toString()+":"+jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key2").toString())); 
            System.out.println(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key3").toString()+":"+jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key3").toString()) );

            // insert new password in basic user hashmap variable
            objHashMapBasicUserDetails.put("str_signup_POST_response_payload_value_password",jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key3").toString()));
            objHashMapBasicUserDetails.put("str_signin_POST_response_payload_value_password",jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key3").toString()));
            objHashMapBasicUserDetails.put("str_getaccountprofiledetail_GET_response_payload_value_password",jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key3").toString()));
            objHashMapBasicUserDetails.put("str_modifyaccountprofiledetail_PUT_response_payload_value_password",jsonobject.get(objHashMapResetPasswordPUT.get("str_resetpassword_PUT_response_key3").toString()));
            globalSuccessFlag = true;
        }
        else{System.out.println("Following invalid HTTP code received : " + responseCode);}
        
        System.out.println("----------------------------------------------------------------------------");
    }
    
	public void ValidateEachUserAction(){
		
        System.out.println("---------------------- *************** ----------------------");

		for(int nn=0; nn<10; nn++){try {Thread.sleep(1000);} catch (Exception e) {}}
		List<String> logFIleContent = null;
		try {logFIleContent = Files.readAllLines(Paths.get(latestApacheServerLogFileName), Charset.defaultCharset());}
		catch (Exception e) {}

		System.out.println("Apache server log for current scenario:");
		for(long cc= latestApacheServerLogFileLineCount; cc < logFIleContent.size(); cc++){
			System.out.println( "\t\t"+ logFIleContent.get((int) cc));
		}
		latestApacheServerLogFileLineCount = logFIleContent.size();
		
	    if(! globalSuccessFlag){
	    	Assert.fail("ERROR     : Any of the individual step is failed.");
	    };
        System.out.println("---------------------- *************** ----------------------");
	}
	
    // helper methods

    private static HashMap<String,Object> fill_GetDocumentationGET(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_getdocumentation_GET_url",                 "http://localhost:8080/imademethink/restfulapi/account_basic/get_documentation");
        hMapLocal.put("str_getdocumentation_GET_method",                 "GET");
        hMapLocal.put("str_getdocumentation_GET_response_key1",         "Activity status");
        hMapLocal.put("str_getdocumentation_GET_response_key2",         "Additional message");
        hMapLocal.put("str_getdocumentation_GET_response_key3",         "Payload");
        return hMapLocal;
    }
    
    private static HashMap<String,Object> fill_SignupGET(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_signup_GET_url",                 "http://localhost:8080/imademethink/restfulapi/account_basic/signup");
        hMapLocal.put("str_signup_GET_method",                 "GET");
        hMapLocal.put("str_signup_GET_response_key1",         "Activity status");
        hMapLocal.put("str_signup_GET_response_key2",         "Additional message");
        hMapLocal.put("str_signup_GET_response_key3",         "Payload");

        hMapLocal.put("str_signup_GET_response_payload_key_emailid",         "signup_emailid");
        hMapLocal.put("str_signup_GET_response_payload_key_firstname",         "signup_firstname");
        hMapLocal.put("str_signup_GET_response_payload_key_lastname",         "signup_lastname");
        hMapLocal.put("str_signup_GET_response_payload_key_gender",         "signup_gender");
        hMapLocal.put("str_signup_GET_response_payload_key_password",         "signup_password");
        hMapLocal.put("str_signup_GET_response_payload_key_secret_question_1",             "signup_secret_question_1");
        hMapLocal.put("str_signup_GET_response_payload_key_secret_question_2",             "signup_secret_question_2");
        hMapLocal.put("str_signup_GET_response_payload_key_secret_question_answer_1",     "signup_secret_question_1_answer");
        hMapLocal.put("str_signup_GET_response_payload_key_secret_question_answer_2",     "signup_secret_question_2_answer");

        return hMapLocal;
    }

    private static HashMap<String,Object> fill_SignupPOST(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_signup_POST_url",                 "http://localhost:8080/imademethink/restfulapi/account_basic/signup");
        hMapLocal.put("str_signup_POST_method",             "POST");
        hMapLocal.put("str_signup_POST_response_key1",         "Activity status");
        hMapLocal.put("str_signup_POST_response_key2",         "Additional message");
        hMapLocal.put("str_signup_POST_response_key3",         "Payload");

        return hMapLocal;
    }
    
    private static HashMap<String,Object> fill_ActivateGET(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_activate_GET_url",                 "http://localhost:8080/imademethink/restfulapi/account_basic/activate");
        hMapLocal.put("str_activate_GET_method",             "GET");
        hMapLocal.put("str_activate_GET_response_key1",     "Activity status");
        hMapLocal.put("str_activate_GET_response_key2",     "Additional message");
        hMapLocal.put("str_activate_GET_response_key3",     "Payload");

        return hMapLocal;
    }    
    
    private static HashMap<String,Object> fill_SigninPOST(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_signin_POST_url",                 "http://localhost:8080/imademethink/restfulapi/account_basic/signin");
        hMapLocal.put("str_signin_POST_method",             "POST");
        hMapLocal.put("str_signin_POST_response_key1",         "Activity status");
        hMapLocal.put("str_signin_POST_response_key2",         "Additional message");
        hMapLocal.put("str_signin_POST_response_key3",         "Payload");

        return hMapLocal;
    }
    
    private static HashMap<String,Object> fill_SignoutDELETE(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_signout_DELETE_url",             "http://localhost:8080/imademethink/restfulapi/account_basic/signout");
        hMapLocal.put("str_signout_DELETE_method",             "DELETE");
        hMapLocal.put("str_signout_DELETE_response_key1",     "Activity status");
        hMapLocal.put("str_signout_DELETE_response_key2",     "Additional message");
        hMapLocal.put("str_signout_DELETE_response_key3",     "Payload");

        return hMapLocal;
    }
    
    private static HashMap<String,Object> fill_GetAccountProfileDetailGET(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_getaccountprofiledetail_GET_url",             "http://localhost:8080/imademethink/restfulapi/account_basic/get_account_profile_details");
        hMapLocal.put("str_getaccountprofiledetail_GET_method",         "GET");
        hMapLocal.put("str_getaccountprofiledetail_GET_response_key1",     "Activity status");
        hMapLocal.put("str_getaccountprofiledetail_GET_response_key2",     "Additional message");
        hMapLocal.put("str_getaccountprofiledetail_GET_response_key3",     "Payload");

        hMapLocal.put("str_getaccountprofiledetail_GET_key_emailid",         "Basic account: email id");
        hMapLocal.put("str_getaccountprofiledetail_GET_key_firstname",         "Basic account: first name");
        hMapLocal.put("str_getaccountprofiledetail_GET_key_lastname",         "Basic account: last name");
        hMapLocal.put("str_getaccountprofiledetail_GET_key_gender",         "Basic account: gender");

        return hMapLocal;
    }    
    
    private static HashMap<String,Object> fill_ModifyAccountProfileDetailPUT(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_url",             "http://localhost:8080/imademethink/restfulapi/account_basic/modify_account_profile_details");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_method",             "PUT");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_key1",     "Activity status");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_key2",     "Additional message");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_key3",     "Payload");

        return hMapLocal;
    }

    private static HashMap<String,Object> fill_ForgetPasswordGetSecretQuestionGET(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_url",             "http://localhost:8080/imademethink/restfulapi/account_basic/forget_password_get_secret_question");
        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_method",         "GET");
        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_response_key1",     "Activity status");
        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_response_key2",     "Additional message");
        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_response_key3",     "Payload");

        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_key_question1",     "Basic account: secret question 1");
        hMapLocal.put("str_forgetpasswordgetsecretquestion_GET_key_question2",     "Basic account: secret question 2");

        return hMapLocal;
    }

    private static HashMap<String,Object> fill_ResetPasswordPUT(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_resetpassword_PUT_url",                 "http://localhost:8080/imademethink/restfulapi/account_basic/reset_password");
        hMapLocal.put("str_resetpassword_PUT_method",             "PUT");
        hMapLocal.put("str_resetpassword_PUT_response_key1",     "Activity status");
        hMapLocal.put("str_resetpassword_PUT_response_key2",     "Additional message");
        hMapLocal.put("str_resetpassword_PUT_response_key3",     "Payload");

        return hMapLocal;
    }
    
    private static HashMap<String,Object> fill_BasicUserDetails(String strTempEmailId, String strTempPassword){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_signup_POST_response_payload_key_emailid",                                "signup_emailid");
        hMapLocal.put("str_signup_POST_response_payload_value_emailid",                             strTempEmailId);
        hMapLocal.put("str_signin_POST_response_payload_key_emailid",                                 "signin_emailid");
        hMapLocal.put("str_signin_POST_response_payload_value_emailid",                              strTempEmailId);
        hMapLocal.put("str_signout_DELETE_response_payload_key_emailid",                           "signout_emailid");
        hMapLocal.put("str_signout_DELETE_response_payload_value_emailid",                        strTempEmailId);
        hMapLocal.put("str_getaccountprofiledetail_GET_response_payload_key_emailid",      "signin_emailid");
        hMapLocal.put("str_getaccountprofiledetail_GET_response_payload_value_emailid",   strTempEmailId);
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_key_emailid",              "signin_emailid");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_value_emailid",           strTempEmailId);
        hMapLocal.put("str_resetpassword_PUT_response_payload_key_emailid",                                    "signup_emailid");
        hMapLocal.put("str_resetpassword_PUT_response_payload_value_emailid",                                 strTempEmailId);

        hMapLocal.put("str_signup_POST_response_payload_key_password",                                "signup_password");
        hMapLocal.put("str_signup_POST_response_payload_value_password",                             strTempPassword);
        hMapLocal.put("str_signin_POST_response_payload_key_password",                                 "signin_password");
        hMapLocal.put("str_signin_POST_response_payload_value_password",                              strTempPassword);
        hMapLocal.put("str_getaccountprofiledetail_GET_response_payload_key_password",                 "signin_password");
        hMapLocal.put("str_getaccountprofiledetail_GET_response_payload_value_password",              strTempPassword);
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_key_password",          "signin_password");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_value_password",       strTempPassword);
//    	private String activationkey 		= null;
//    	private String sessionid 			= null;
    	
        hMapLocal.put("str_signup_POST_response_payload_key_firstname",                                    "signup_firstname");
        hMapLocal.put("str_signup_POST_response_payload_value_firstname",                                 firstname);
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_key_firstname",   "signin_firstname");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_value_firstname",firstname);

        hMapLocal.put("str_signup_POST_response_payload_key_lastname",                                     "signup_lastname");
        hMapLocal.put("str_signup_POST_response_payload_value_lastname",                                  lastname);
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_key_lastname",    "signin_lastname");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_value_lastname", lastname);

        hMapLocal.put("str_signup_POST_response_payload_key_gender",                                      "signup_gender");
        hMapLocal.put("str_signup_POST_response_payload_value_gender",                                   gender);
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_key_gender",     "signin_gender");
        hMapLocal.put("str_modifyaccountprofiledetail_PUT_response_payload_value_gender",  gender);

        hMapLocal.put("str_signup_POST_response_payload_key_secret_question_1",         "signup_secret_question_1");
        hMapLocal.put("str_signup_POST_response_payload_value_secret_question_1",      secretquestion1);
        hMapLocal.put("str_signup_POST_response_payload_key_secret_question_2",         "signup_secret_question_2");
        hMapLocal.put("str_signup_POST_response_payload_value_secret_question_2",      secretquestion2); 

        hMapLocal.put("str_signup_POST_response_payload_key_secret_question_answer_1",                "signup_secret_question_1_answer");
        hMapLocal.put("str_signup_POST_response_payload_value_secret_question_answer_1",             secretanswer1);
        hMapLocal.put("str_forgetpassword_PUT_response_payload_key_secret_question_answer_1",   "signup_secret_question_1_answer");
        hMapLocal.put("str_forgetpassword_PUT_response_payload_value_secret_question_answer_1",secretanswer1);
        hMapLocal.put("str_signup_POST_response_payload_key_secret_question_answer_2",                "signup_secret_question_2_answer");
        hMapLocal.put("str_signup_POST_response_payload_value_secret_question_answer_2",             secretanswer2);
        hMapLocal.put("str_forgetpassword_PUT_response_payload_key_secret_question_answer_2",   "signup_secret_question_2_answer");
        hMapLocal.put("str_forgetpassword_PUT_response_payload_value_secret_question_answer_2",secretanswer2);

        // other user details
        hMapLocal.put("str_basic_account_activation_key",           "account_basic_activatation_key");
        hMapLocal.put("str_basic_account_activation_value",        "");
        
        hMapLocal.put("str_basic_account_session_key",                "session_id");
        hMapLocal.put("str_basic_account_session_value",             "");
        
        hMapLocal.put("str_basic_account_latest_session_key",     "latest_session_key");
        hMapLocal.put("str_basic_account_latest_session_value",  "");

        return hMapLocal;
    }

    private static HashMap<String,Object> fill_ErrorResponse(){
        HashMap<String,Object> hMapLocal = new HashMap<String,Object>();
        hMapLocal.put("str_key_ErrorMessage",         "Error message");
        hMapLocal.put("str_key_ExceptionType",         "Exception type");
        hMapLocal.put("str_key_InternalErrorCode",     "Internal error code");

        return hMapLocal;
    }
	
    private boolean validateResponseContentType(String strContentTypeOfResponse){
        if(! strContentTypeOfResponse.contains(MediaType.APPLICATION_JSON)){
            System.out.println("Content type of response expected is : " + MediaType.APPLICATION_JSON);
            System.out.println("Please check what is the issue from server side.");
            return false;
        }
        return true;
    }
    
    // helper methods general

    
    private RequestSpecification setup_Connection(){
    	RequestSpecification requestspecs = with();
    	requestspecs.contentType(headerParamRequestContentType);
    	requestspecs.accept(headerParamResponseAcceptType);
		return requestspecs;
    }
        

    
    private JSONObject parse_StringResponse(String responseInString) throws Exception{
        // parsing the response and json conversion
        JSONParser parser             = new JSONParser();
        Object object                     = parser.parse(responseInString);
        JSONObject jsonobject     = (JSONObject) object;

        return jsonobject;
    }


}
