import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Rafaquat
 * This class represents the GUI for both students and tutors
 * 
 */
public abstract class GraphicalUserInterface {
	
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
}
