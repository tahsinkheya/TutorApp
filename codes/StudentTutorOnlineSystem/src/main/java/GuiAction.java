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
    static String myApiKey=OnlineMatchingClient.myApiKey;
    public void show();

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
            System.out.println(e.getCause());
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
        	System.out.println("Duration is empty");
        	contractLength = 6;
        	System.out.println("Default duration is: "+contractLength);
        }
        else {
        	System.out.println("Duration is not empty");
        	contractLength = Integer.parseInt(contDurationInput.toString());
        	System.out.println("Duration set by user is: "+contractLength);
        }
        
    	Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        String contExpiryDate = new Date(timeInSecs).toInstant().toString();
        
        // add 6 months
        date.add(Calendar.MONTH, contractLength);
        timeInSecs = date.getTimeInMillis();
        contExpiryDate = new Date(timeInSecs).toInstant().toString();
        
        System.out.println("Contract will expire on: "+contExpiryDate);
    	return contExpiryDate;
    }

}
