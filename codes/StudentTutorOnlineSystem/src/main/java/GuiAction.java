import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        catch (Exception e){ }
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
            System.out.println(response.statusCode());
        }
        catch (Exception e){System.out.println(e.getMessage());}
        return response;
    }

    //method that gets tutor comp for a given subject and tutorid
    static int getTuteComp(String subName,String tutorId){
        //get tutor competency in the subject
        String endpoint = "user/"+tutorId+"?fields=competencies.subject";
        int tutorcompetencyLevel = 0;
        HttpResponse<String> compResponse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode.class);

            for (JsonNode node : userNode.get("competencies")) {
                // get the subject name that the tutor teaches and compare it to the requested one.
                String nodeSubName = node.get("subject").get("name").toString();
                String tutorSubName = GuiAction.removeQuotations(nodeSubName);
                if(tutorSubName.equals(subName)) {
                    tutorcompetencyLevel = node.get("level").asInt();
                }
            }
        }
        catch (Exception e){}
        return tutorcompetencyLevel;
    }

    static String getTutorQualification(String userId) {
        String endpoint = "user?fields=qualifications";
        String tutorQ = "";
        HttpResponse<String> compResponse = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try{
            ObjectNode[] userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode[].class);
            for (JsonNode node : userNode) {
                if(node.get("id").toString().contains(userId)){//check that the tutorid=userid
                    for(JsonNode n:node.get("qualifications")){
                        tutorQ+=GuiAction.removeQuotations(n.get("title").toString())+" | ";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if tutor qualifictaion is not known then
        if (tutorQ.equals("")){
            tutorQ="unknown";
        }
        return tutorQ;

    }

}
