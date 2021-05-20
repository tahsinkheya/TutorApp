import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Calendar;
import java.util.Date;

/*
* interface for GUIcontext
* */
public interface GuiAction {
    String myApiKey=OnlineMatchingClient.myApiKey;
    void show();

    /* Method to make a web request to GET some data. Concrete implementation since
     * the subclasses will need this method */
    static HttpResponse<String> initiateWebApiGET(String endpoint, String apiKey) {
        String Url = "https://fit3077.com/api/v2/"+endpoint;

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

    /* Method to update a web request to POST some data. Concrete implementation since
     * the subclasses will need this method */
    static  HttpResponse<String> updateWebApi(String endpoint, String apiKey, String jsonString) {
        String Url = "https://fit3077.com/api/v2/"+endpoint;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Url))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e){
            System.out.println("Error!!!");
        }
        return response;
    }
    /* Method to remove "" from json get strings */
    static String removeQuotations(String str) {
        int strLen = str.length();
        String outputStr = str.substring(1, strLen-1);
        return outputStr;
    }
    
    /** Method to set the expiry date of a contract **/
    static String getContractExpiryDate(String contDurationInput) {
    	int contractLength;
        if(contDurationInput.equals("")) {
        	contractLength = 6;
        }
        else {
        	contractLength = Integer.parseInt(contDurationInput.toString());
        	if(contractLength < 3) {
        		return "Contract duration must be atleast 3 months";
        	}
        	System.out.println("Duration set by user is: "+contractLength);
        }
        
    	Calendar date = Calendar.getInstance();
        // add 6 months
        date.add(Calendar.MONTH, contractLength);
        long timeInSecs = date.getTimeInMillis();
        String contExpiryDate = new Date(timeInSecs).toInstant().toString();
    	return contExpiryDate;
    }
    //method to use a web request to patch data. subclasses use this method
    static HttpResponse<String> patchWebApi(String endpoint,String jsonString){
        String Url = "https://fit3077.com/api/v2/"+endpoint;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Url))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .method("PATCH",HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e){}
        return response;
    }

}
