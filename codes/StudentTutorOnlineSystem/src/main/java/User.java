import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface User {
    //public boolean logIn(String username, String password);
    public boolean signContract();
    public void create(String uName, String gName,String fName);
    

    
    /* Method to make a web request to GET some data */
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
