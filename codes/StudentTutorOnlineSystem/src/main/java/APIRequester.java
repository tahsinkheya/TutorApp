import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Rafaquat
 * This class represents the GUI for both students and tutors
 * 
 */
public abstract class APIRequester {
	
	/* This method is used to add data to the database. StudentGUI uses this
	 * to search for subjects they need help with and to show their current requests  */
	abstract String webApiPOST(String endpoint, String subID);
	
	
	/* Method to make a web request to GET some data. Concrete implementation since both 
	 * StudentGUI and TutorGUI will need this method */
	static  HttpResponse<String> initiateWebApiGET(String endpoint, String apiKey) {

		String Url = "https://fit3077.com/api/v1/"+endpoint;
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
		.newBuilder(URI.create(Url))
		.setHeader("Authorization", apiKey)
		.GET()
		.build();
		HttpResponse<String> response = null;
		try {
		 response = client.send(request, HttpResponse.BodyHandlers.ofString());
		}
		catch (Exception e){
            System.out.println(e.getCause());
        }
		return response;
	}
	
	static  HttpResponse<String> updateWebApi(String endpoint, String apiKey, String jsonString) {
		
		//System.out.println("The abstract class has this: "+jsonString);
		String Url = "https://fit3077.com/api/v1/"+endpoint;
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
		.newBuilder(URI.create(Url))
		.setHeader("Authorization", apiKey)
		.header("Content-Type","application/json")
		.POST(HttpRequest.BodyPublishers.ofString(jsonString))
		.build();
		HttpResponse<String> response = null;
		try {
		 response = client.send(request, HttpResponse.BodyHandlers.ofString());
		 //System.out.println("FROM GUI: " + response.toString());
		}
		catch (Exception e){
			System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
		return response;
	}
	
	static String removeQuotations(String str) {
		int strLen = str.length();
		String outputStr = str.substring(1, strLen-1);
		return outputStr;
	}
}
