package imademethink_jayway_simple.StepImplimentations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.jayway.restassured.response.Response;

public class GeneralUtilities {

	public GeneralUtilities(){}
	
	public String GetLatestApacheServeLogFileName(String serverLogFilePath, String serverLogFileNameStartWith){
		try{
			File dir                                                               = new File(serverLogFilePath);
			FilenameFilter filterOnApacheAccessLogFile = new MyApacheLogFileFilter();
			File[] arrayOfFiles                                             = dir.listFiles(filterOnApacheAccessLogFile);
			Arrays.sort(arrayOfFiles);
			System.out.println("Following LATEST apache server log file will be referred for log analysis: ");
			System.out.println("\t\t" + arrayOfFiles[-1 + arrayOfFiles.length].getName());
			return arrayOfFiles[-1 + arrayOfFiles.length].getName().toString();
		}catch(Exception e){}
		return null;
	}
	
	public boolean DownloadAndSaveAttachment(String downloadPath,Response responseJayWay) throws Exception{
        FileOutputStream fileOutputStream  = new FileOutputStream(downloadPath);
        InputStream inputStream                    = responseJayWay.asInputStream();
        boolean dataFoundInInputStream     = false;
        int chunkLength                                    = 0;
        byte[] buffer4k                                      = new byte[4096];
        while((chunkLength = inputStream.read(buffer4k)) != -1) {
        	dataFoundInInputStream = true;
        	fileOutputStream.write(buffer4k, 0, chunkLength);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
        if (! dataFoundInInputStream){
            System.out.println("Encoountered an issue while receiving attachment from web application.");
        	return false;
        }
        System.out.println("Attachment related to to documentation is received from web application.");
        System.out.println("Attachment saved at following location: " + downloadPath);
        return true;
	}
	
    public boolean handleClientSideError4xx(
													int responseCode, 
													String responseInString, 
													JSONObject jsonobject, 
													HashMap<String, Object> objHashMapErrorResp) throws Exception{
		if (responseCode >= 400 && responseCode < 500 ){
			// Client side error handler
			// parsing the response and json conversion
			handle_ClientSideErrorCode(responseCode);
			handle_ErrorResponseMessage(jsonobject, objHashMapErrorResp);
			return false;
		}
		return true;
	}
	
    public boolean handleServerSideError5xx(
			int responseCode, 
			String responseInString, 
			JSONObject jsonobject,
			HashMap<String, Object> objHashMapErrorResp) throws Exception{
		if (responseCode >= 500 && responseCode < 600 ){
			// Server side error handler
			// parsing the response and json conversion
			handle_ServerSideErrorCode(responseCode);
			handle_ErrorResponseMessage(jsonobject, objHashMapErrorResp);
			return false;
		}
		return true;
	}

    private void handle_ClientSideErrorCode(int nResponseCodeTemp){
        if (400 ==  nResponseCodeTemp)        System.out.println("ERROR     : This is a bad request.");
        if (404 ==  nResponseCodeTemp)        System.out.println("ERROR     : This page / resource is not found.");
        if (401 ==  nResponseCodeTemp)        System.out.println("ERROR     : This request is not authorized.");
        if (403 ==  nResponseCodeTemp)        System.out.println("ERROR     : This request is not forbidded.");
    }
    
    private void handle_ServerSideErrorCode(int nResponseCodeTemp){
        if (500 ==  nResponseCodeTemp)        System.out.println("ERROR     : Internal server error.");
        if (503 ==  nResponseCodeTemp)        System.out.println("ERROR     : Service unavailable or some maintenance is going on.");
    }
    
    private void handle_ErrorResponseMessage(JSONObject jsonobjectTemp,HashMap<String,Object> objHashMapErrorRespTemp){
        System.out.println("\nFollowing error response received: ");
        System.out.println(objHashMapErrorRespTemp.get("str_key_ErrorMessage").toString()      +":"+jsonobjectTemp.get(objHashMapErrorRespTemp.get("str_key_ErrorMessage").toString()));
        System.out.println(objHashMapErrorRespTemp.get("str_key_ExceptionType").toString()     +":"+jsonobjectTemp.get(objHashMapErrorRespTemp.get("str_key_ExceptionType").toString()));
        System.out.println(objHashMapErrorRespTemp.get("str_key_InternalErrorCode").toString() +":"+jsonobjectTemp.get(objHashMapErrorRespTemp.get("str_key_InternalErrorCode").toString()));
    }
    
	
}

class MyApacheLogFileFilter implements FilenameFilter {
//	private Set<String> exts = new HashSet<String>();
//
//    public MyApacheLogFileFilter(String... filterNameStartWith) {
//        for (String ext : filterNameStartWith) {
//            exts.add("." + ext.toLowerCase().trim());
//        }
//    }

	@Override
	public boolean accept(File directory, String fileName) {
//		final Iterator<String> extList = exts.iterator();
		if (fileName.startsWith("localhost_access_log")) {
            return true;
        }
        return false;
	}
}

