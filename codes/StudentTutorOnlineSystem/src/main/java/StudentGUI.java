import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class StudentGUI implements ActionListener {
	public JLabel name;
	public JPanel panel;
	
	JButton submitButton, showRequests;
	public String userId;
	private static final String myApiKey = "";
	
	// user inputs for subject, lesson(description), session time, rate and number of sessions
	private static JTextField subjectText, descText, timeInput, rateIn, sessionNum;
	// chosen qualification level
	private static JComboBox qualList, timeList, daysBox, allRates, allRequests;
	


	
	public StudentGUI() {
		// Creating instance of JFrame
        JFrame frame = new JFrame("Student Homepage");
        // Setting the width and height of frame
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      
        panel = new JPanel();    
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);
        
        
        // take user inputs over here
        JLabel actionLabel = new JLabel("Make a matching request");
        actionLabel.setBounds(10,50,300,25);
        panel.add(actionLabel);
        
        
        
        JLabel desc = new JLabel("Specify tutor details");
        desc.setBounds(10,70,300,25);
        desc.setForeground(Color.red);
        panel.add(desc);
        
        
        
        // Required qualification
        JLabel qualification = new JLabel("Specialisation: ");
        qualification.setBounds(10, 100, 100, 25);
        panel.add(qualification);
        
        subjectText = new JTextField(20);
        subjectText.setBounds(100,100,165,25);
        panel.add(subjectText);
        
        // Types of qualification
        String[] qualificationTypes = {"Bachelor's Degree", "Master's Degree", "Doctoral Degree","Secondary Education"};
        qualList = new JComboBox(qualificationTypes);
        qualList.setSelectedIndex(0);
        qualList.setBounds(300, 100, 200, 25);
        panel.add(qualList);
        
        
        // Lesson 
        JLabel lesson = new JLabel("Lesson: ");
        lesson.setBounds(10, 140, 80, 25);
        panel.add(lesson);
        
        descText = new JTextField(20);
        descText.setBounds(100,140,165,25);
        panel.add(descText);
        
        
        // Time and Day 
        JLabel timeAndDay = new JLabel("Time and Day: ");
        timeAndDay.setBounds(10, 170, 100, 25);
        panel.add(timeAndDay);
        
        // Time Selection
        timeInput = new JTextField(20);
        timeInput.setBounds(100,170,50,25);
        panel.add(timeInput);
        
        String[] allTimes = {"AM", "PM"};
        timeList = new JComboBox(allTimes);
        timeList.setBounds(150, 170, 70, 25);
        panel.add(timeList);
        
        // Day selection
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        daysBox = new JComboBox(days);
        daysBox.setSelectedIndex(0);
        daysBox.setBounds(250, 170, 200, 25);
        panel.add(daysBox);
        
        // Weekly Sessions
        JLabel session = new JLabel("Weekly Sessions: ");
        session.setBounds(10, 200, 200, 25);
        panel.add(session);
        
     
        sessionNum= new JTextField(20);
        sessionNum.setBounds(150,200,50,25);
        panel.add(sessionNum);
        
        
        // Payment Rate
     
        JLabel rate = new JLabel("Rate (RM): ");
        rate.setBounds(10, 240, 100, 25);
        panel.add(rate);
        
        rateIn = new JTextField(20);
        rateIn.setBounds(80, 240, 80, 25);
        panel.add(rateIn);
        
        String[] rateTypes = {"per hour", "per session"};
        allRates = new JComboBox(rateTypes);
        allRates.setBounds(180, 240, 100, 25);
        panel.add(allRates);
        
        // Creating request button
        
        submitButton = new JButton("Make Request");
        submitButton.setBounds(10, 270, 120, 25);
        submitButton.addActionListener(this);
        panel.add(submitButton);
        
        // show bid details
        
        /*
        showRequests = new JButton("Show Current Requests");
        showRequests.setBounds(10, 300, 120, 25);
        showRequests.addActionListener(this);
        panel.add(showRequests);
        */
        
        // show the current bids
        JLabel bidSectionHeader = new JLabel("Your current requests: ");
        bidSectionHeader.setBounds(10, 330, 300, 25);
        panel.add(bidSectionHeader);
        
        allRequests = new JComboBox();
        allRequests.setBounds(10, 360, 750, 25);
        panel.add(allRequests);
        
        /*
        showRequests = new JButton("Time");
        showRequests.setBounds(10, 400, 120, 25);
        showRequests.addActionListener(this);
        panel.add(showRequests);
        */
        
        // Setting the frame visibility to true
        frame.setVisible(true);
        
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == submitButton)
			requestTutor();
	}
	
	
	private void requestTutor() {
		
		System.out.println("Tutor Request process started");
		// user id of the logged in user
		System.out.println("User ID: " + userId);
		
		// subject id of the subject that student wants
		String subId = findSubject();
		System.out.println("Subject ID: " + subId);
		
		// bid id of the new request
		String newBidID = webApiPOST("bid", subId);
		System.out.println("Bid ID: " + newBidID);
		System.out.println("Successfully created a tutor bid/request");
		showAllBids();
	}
	
	
	/* Method to make a web request to GET some data */
	private  HttpResponse<String> initiateWebApiGET(String endpoint) {
		String Url = "https://fit3077.com/api/v1/"+endpoint;
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest
		.newBuilder(URI.create(Url))
		.setHeader("Authorization", myApiKey)
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
	
	
	/* Method to create a new class instances in db.
	 * For now: new subject can be created and new bid can be created */
	
	private String webApiPOST(String endpoint, String subID) {
		String refId = null;  // id value to get the subject or bid
		String jsonString = null;
		// set the endpoint types to be false
		boolean isSubject = false;
		boolean isBid = false;
		
		// endpoint.contains is used since we can have subject or subject/subjectID
		if(endpoint.contains("subject")) {
			System.out.println("Need to make subject JSON for: "+ endpoint);
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
			System.out.println("Bid Started at: " + bidStartTime);
			
			Calendar date = Calendar.getInstance();
			long timeInSecs = date.getTimeInMillis();
			String bidEndTime = new Date(timeInSecs + (30*60*1000)).toInstant().toString();
			System.out.println("Bid will close at: " + bidEndTime);
			
			JSONObject additionalInfo=new JSONObject(); 
			
			// create the additional info
			additionalInfo.put("qualificationLevel", qualList.getSelectedItem().toString());
			additionalInfo.put("weeklySessions", sessionNum.getText());
			String sessionTimeAndDay = timeInput.getText()+" "+timeList.getSelectedItem().toString()+" "+ daysBox.getSelectedItem().toString();
			additionalInfo.put("sessionTimeAndDay", sessionTimeAndDay);
			String rate = "RM"+rateIn.getText()+" "+ allRates.getSelectedItem().toString();
			additionalInfo.put("rate", rate);
			
			// the web api does not accept "dateClosedDown" value when making POST 
			additionalInfo.put("requestClosesAt", bidEndTime);
			System.out.println("Additional Info: "+additionalInfo.toString());
			
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
		HttpResponse<String> subResponse = initiateWebApiGET("subject");
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(subResponse.body(), ObjectNode[].class);
			
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
				System.out.println("Subject not found, so creating new subject ");
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
	protected void showAllBids() {
		HttpResponse<String> userResponse = initiateWebApiGET("user/"+userId+"?fields=initiatedBids");
		try {
			ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
			String output="";
			
			allRequests.removeAllItems();
			// loop since each student can have multiple requests
			for (JsonNode node : userNode.get("initiatedBids")) {
				// get the bid status
				String bidType = node.get("type").toString();
				String subjectName = node.get("subject").get("name").toString();
				String desc = node.get("subject").get("description").toString();
				String closingTime = node.get("additionalInfo").get("requestClosesAt").toString();
				output = "Bid Status: " + bidType +"   "+ "\nSubject: "+subjectName +"  Topic of Interest: "+ desc +" " +"Bid closes at: "+ closingTime +"\n\n"; 
				allRequests.addItem(output);	// update the UI to show each bid 
			}
			
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
	}
}

