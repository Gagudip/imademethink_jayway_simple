package imademethink_jayway_simple.StepDefinitions;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import imademethink_jayway_simple.StepImplimentations.BddStepImplimentation_Simple_REST_API_Testing;

public class BddStepDefinitions_Simple_REST_API_Testing {

	BddStepImplimentation_Simple_REST_API_Testing handle = null;

	@Given("^Simple init$")
	public void simple_init(){
		handle = new BddStepImplimentation_Simple_REST_API_Testing();
	}
	
	@Given("^Set basic web application url is \"(.*?)\"$")
	public void set_basic_web_application_url_is(String basicApplicationURL){
	    handle.basicApplicationURL = basicApplicationURL;
	}

	@Given("^Set basic user details are$")
	public void set_basic_user_details_are(DataTable userDetailDataTable){
		handle.ParseBasicUserDetails(userDetailDataTable);
	}

	@Given("^Set GET api endpoint as \"(.*?)\"$")
	public void set_GET_api_endpoint_as(String currentAPIEndPoint){
		handle.currentAPIEndPoint = currentAPIEndPoint;
	}

	@Given("^Set POST api endpoint as \"(.*?)\"$")
	public void set_POST_api_endpoint_as(String currentAPIEndPoint){
		handle.currentAPIEndPoint = currentAPIEndPoint;
	}

	@Given("^Set PUT api endpoint as \"(.*?)\"$")
	public void set_PUT_api_endpoint_as(String currentAPIEndPoint){
		handle.currentAPIEndPoint = currentAPIEndPoint;
	}
	
	@Given("^Set DELETE api endpoint as \"(.*?)\"$")
	public void set_DELETE_api_endpoint_as(String currentAPIEndPoint){
		handle.currentAPIEndPoint = currentAPIEndPoint;
	}

	@Given("^Set HEADER param request content type as \"(.*?)\"$")
	public void set_HEADER_param_request_content_type_as(String headerParamRequestContentType){
		handle.headerParamRequestContentType = headerParamRequestContentType;
	}

	@Given("^Set HEADER param response accept type as \"(.*?)\"$")
	public void set_HEADER_param_response_accept_type_as(String headerParamResponseAcceptType){
		handle.headerParamResponseAcceptType = headerParamResponseAcceptType;
	}

	@Given("^Set Query param as \"(.*?)\"$")
	public void set_Query_param_as(String queryParam){
		handle.setQueryParam(queryParam);	    
	}

	@Given("^Raise \"(.*?)\" HTTP request$")
	public void raise_HTTP_request(String httpMethod){
		handle.rasieFollowingHTTPMethod(httpMethod);
	}

	@Then("^Valid HTTP response should be received$")
	public void valid_HTTP_response_should_be_received(){
		handle.validateHTTPresponseShouldBeValid();
	}

	@Then("^Response http code should be (\\d+)$")
	public void response_http_code_should_be(int expectedHttpResponseCode){
		handle.validateHTTPresponseCode(expectedHttpResponseCode);
	}

	@Then("^Response HEADER content type should be \"(.*?)\"$")
	public void response_HEADER_content_type_should_be(String headerParamResponseContentType){
		handle.validateHTTPresponseContentType(headerParamResponseContentType);
	}

	@Then("^Response BODY should not be null or empty$")
	public void response_BODY_should_not_be_null_or_empty(){
		handle.validateHTTPresponseBodyShouldNotBeNull();
	}

	@Then("^Response BODY parsing for \"(.*?)\" should be successful$")
	public void response_BODY_parsing_for_should_be_successful(String httpMethod_APIendPoint){
		handle.validateHTTPresponseBodyShouldBeSuccessfulFor(httpMethod_APIendPoint);
	}

	@Given("^Set BODY form param using basic user details$")
	public void set_BODY_form_param_using_basic_user_details(){
		handle.setBodyFormParamUsingBasicUserDetails();
	}
	
	@Given("^Modify BODY form param first name as \"(.*?)\" and last name as \"(.*?)\"$")
	public void modify_BODY_form_param_first_name_and_last_name(String first_Name, String last_Name){
		handle.modifyBodyFormParamFirstNameAndLastName(first_Name,last_Name);
	}

	@Given("^Perform setup for PUT request$")
	public void perform_setup_for_put_request(){
		handle.performSetupForPUTrequest();
	}

	@Given("^Perform setup for DELETE request$")
	public void perform_setup_for_DELETE_request(){
		handle.performSetupForDELETErequest();
	}

	
	
}
