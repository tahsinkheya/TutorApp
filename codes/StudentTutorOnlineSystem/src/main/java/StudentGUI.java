import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.simple.JSONObject;

public class StudentGUI extends APIRequester implements ActionListener {
	public JLabel name;

	JButton submitButton, selectBtn;
	public static String userId;
	private static String myApiKey = "";
	
	// user inputs for subject, lesson(description), session time, rate and number of sessions
	private static JTextField subjectText, descText, timeInput, rateIn, sessionNum;
	// chosen qualification level
	private static JComboBox qualList, compList, timeList, daysBox, allRates, allRequests;
	
	private static JLabel requestMade, requestStatus;
	
	// create the bid only once.
	private static boolean bidCreated = false;

//	private void setKey(String key){
//		myApiKey=key;
//	}


//	private void makeRequest() {


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == submitButton) {
			requestTutor();
		}

			
	}
	
	
	private void requestTutor() {
		

		
		// subject id of the subject that student wants
		String subId = findSubject();
		System.out.println("Subject ID: " + subId);
		
		// bid id of the new request
		String newBidID = webApiPOST("bid", subId);
		System.out.println("Bid ID: " + newBidID);
		System.out.println("Successfully created a tutor bid/request");
		showAllRequests();
	}
	
	
	
	
	/* Method to create a new class instances in db.
	 * For now: new subject can be created and new bid can be created */
	
	protected String webApiPOST(String endpoint, String subID) {

		String refId = null;  // id value to get the subject or bid
		String jsonString = null;
		String closeTime = null;
		// set the endpoint types to be false
		boolean isSubject = false;
		boolean isBid = false;
		
		// endpoint.contains is used since we can have subject or subject/subjectID
		if(endpoint.contains("subject")) {
			// create a new JSON object for subject
			jsonString = "{" + 
					"\"name\":\"" + subjectText.getText() + "\"," +
					"\"description\":\"" + descText.getText()+ "\"" +
				"}";
			isSubject = true;
		}
		
		// endpoint.contains is used since we can have subject or subject/subjectID
		else if(endpoint.contains("bid")) {
			
			// find today's date and time
			String bidStartTime = new Date().toInstant().toString();			
			Calendar date = Calendar.getInstance();
			long timeInSecs = date.getTimeInMillis();
			String bidEndTime = new Date(timeInSecs + (30*60*1000)).toInstant().toString();
			
			
			JSONObject additionalInfo=new JSONObject(); 
			// create the additional info
			additionalInfo.put("qualificationLevel", qualList.getSelectedItem().toString());
			additionalInfo.put("requiredCompetency", compList.getSelectedItem().toString());
			additionalInfo.put("weeklySessions", sessionNum.getText());
			String sessionTimeAndDay = timeInput.getText()+" "+timeList.getSelectedItem().toString()+" "+ daysBox.getSelectedItem().toString();
			additionalInfo.put("sessionTimeAndDay", sessionTimeAndDay);
			String rate = "RM"+rateIn.getText()+" "+ allRates.getSelectedItem().toString();
			additionalInfo.put("rate", rate);
			
			// the web api does not accept "dateClosedDown" value when making POST 
			additionalInfo.put("requestClosesAt", bidEndTime);
			closeTime = bidEndTime;
			
			
			// create the bid
			JSONObject bidInfo=new JSONObject();
			bidInfo.put("type", "open");
			bidInfo.put("initiatorId", userId);
			bidInfo.put("dateCreated", bidStartTime);
			bidInfo.put("subjectId", subID);
			bidInfo.put("additionalInfo", additionalInfo);
			jsonString = bidInfo.toString(); // convert to string
			System.out.println("Bid/Request Info: "+jsonString);
			isBid = true;
		}
		
		
		// create a new subject or bid in the database 
		String Url = "https://fit3077.com/api/v1/"+endpoint;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
		.newBuilder(URI.create(Url))
		.setHeader("Authorization", myApiKey)
		.header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
	    .POST(HttpRequest.BodyPublishers.ofString(jsonString))
		.build();
		
		try {
			HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(isSubject) {
				System.out.println("Created new subject in database");
			}
			else if(isBid) {
				System.out.println("Created new bid in database");
				bidCreated = true;
			}
			
			// get the id of the newly created object
			ObjectNode jsonNode = new ObjectMapper().readValue(postResponse.body(), ObjectNode.class);
			refId = jsonNode.get("id").asText();
			return refId;
		
		}
		catch(Exception e){
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			
		}
		return refId;
	}
	

	
	
	/* Method to get the subject id for the subject given as input by the user */
	private String findSubject() {
		String subjectID = null;
		boolean subjectFound = false;
		// get the user inputs
		String userSub = subjectText.getText();
		String userDesc = descText.getText();
		HttpResponse<String> subResponse = APIRequester.initiateWebApiGET("subject", myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(subResponse.body(), ObjectNode[].class);
			userSub="maths";
			// look for the subject and description in the database
			for (ObjectNode node: jsonNodes) {
		      	String subFromDB = node.get("name").asText();
		      	String descFromDB = node.get("description").asText();
				if (subFromDB.equals(userSub) & descFromDB.equals(userDesc) ) {
					System.out.println("Subject found in database");
					subjectFound = true;
					subjectID = node.get("id").asText();
					return subjectID;
				}
			}

			if (subjectFound==false) {
				// if the subject is not found in database, then create new subject
				System.out.println("Subject not found, so creating new subject: " + userSub);
				subjectID = webApiPOST("subject", null);
				return subjectID;
			}
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
		return subjectID;
	}

	/* Method to show the current bids opened by the student  */
	protected static void showAllRequests() {
		// get all the bids with messages
		HttpResponse<String> userResponse = APIRequester.initiateWebApiGET("bid?fields=messages", myApiKey);
		try {
			ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
			String output="";
			String studentRequest  = "";
			allRequests.removeAllItems();
			requestMade.setText("");
			for (ObjectNode node : userNodes) {
				// process the initiator id to remove extra quotations
				String initId = node.get("initiator").get("id").toString();
				String initiatorId = APIRequester.removeQuotations(initId);
			
				// find requests made by student by comparing userId and initiatorId
				if(initiatorId.equals(userId) & node.get("dateClosedDown").toString().equals("null") ) {
					System.out.println("Found bid made by student");
					String bidId = APIRequester.removeQuotations(node.get("id").toString());
					System.out.println("The bid id is: " + bidId);
					
					String closeTime = node.get("additionalInfo").get("requestClosesAt").toString();
					String subjectName = node.get("subject").get("name").toString();
					String desc = node.get("subject").get("description").toString();
					
					// each bid can have multiple messages so loop 
					String msg = "";
					String msgSender="";
					for (JsonNode msgNode : node.get("messages")) {
						msg = msgNode.get("content").toString();
						msgSender = msgNode.get("poster").get("userName").toString();
						// update the jcombo box as more tutors reply
						output = "Subject: "+subjectName  +"    "+ "Topic: "+ desc+"    "+"Bid: "+ msg+"    "+ "From: "+ msgSender;
						allRequests.addItem(output);
					}
					
					studentRequest = "Subject: "+subjectName  +"    "+ "Topic: "+ desc;
					if(bidCreated) {
						closeBid(bidId, closeTime);
						bidCreated = false;
					}

					requestMade.setText("Your Request: "+ studentRequest);
					requestStatus.setText("Tutor request will remain open for the next 30 minutes");
					
				}
			}
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
	}
	
	/*Method to close the bid after 30 minutes it was created*/
	private static void closeBid(String bidId, String closeTime) {
		// bid lasts for 10 seconds for now
		int seconds = 1800; 	// 30 minutes
		new RequestCloser(seconds, bidId, myApiKey, closeTime);
        System.out.println("Bid opened for 30 minutes.");
	}

	
		
}
