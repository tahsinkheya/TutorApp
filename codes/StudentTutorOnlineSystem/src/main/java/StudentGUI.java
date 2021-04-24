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
import java.util.Timer;
import java.util.TimerTask;

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

public class StudentGUI extends GraphicalUserInterface implements ActionListener {
	public JLabel name;
	public JPanel panel;
	
	JButton submitButton, selectBtn;
	public static String userId;
	private static final String myApiKey = "";
	
	// user inputs for subject, lesson(description), session time, rate and number of sessions
	private static JTextField subjectText, descText, timeInput, rateIn, sessionNum;
	// chosen qualification level
	private static JComboBox qualList, compList, timeList, daysBox, allRates, allRequests;
	
	private static JLabel requestMade, requestStatus;
	
	// create the bid only once.
	private static boolean bidCreated = false;

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
        
        
        String[] competencies = {"1","2","3","4","5","6","7","8","9","10"};
        compList = new JComboBox(competencies);
        compList.setSelectedIndex(0);
        compList.setBounds(550, 100, 60, 25);
        panel.add(compList);
        
        
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
        
        
        // show the current bids
        JLabel bidSectionHeader = new JLabel("Your current requests: ");
        bidSectionHeader.setBounds(10, 330, 300, 25);
        panel.add(bidSectionHeader);
        
        JLabel instruction = new JLabel("Select a request/bid and click on 'Select Bidder' to close bid");
        instruction.setBounds(10, 350,1200,25);
        instruction.setForeground(Color.red);
        panel.add(instruction);
        
        
        requestMade = new JLabel("Your Request: ");
        requestMade.setBounds(10, 380, 800, 25);
        panel.add(requestMade);
        
        // Weekly Sessions
        JLabel responseLabel = new JLabel("All Responses: ");
        responseLabel.setBounds(10, 420, 200, 25);
        panel.add(responseLabel);
        
        
        // all responses
        allRequests = new JComboBox();
        allRequests.setBounds(130, 420, 750, 25);
        panel.add(allRequests);
        
        requestStatus = new JLabel("Status: ");
        requestStatus.setBounds(10, 450, 600, 25);
        panel.add(requestStatus);
        
        
        selectBtn = new JButton("Select Bidder");
        selectBtn.setBounds(10, 490, 120, 25);
        selectBtn.addActionListener(this);
        panel.add(selectBtn); 		
        		
        // Setting the frame visibility to true
        frame.setVisible(true);
        
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == submitButton) {
			requestTutor();
		}
			
	}
	
	
	private void requestTutor() {
		
		System.out.println("Tutor Request process started");
		
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
		HttpResponse<String> subResponse = GraphicalUserInterface.initiateWebApiGET("subject", myApiKey);
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
		HttpResponse<String> userResponse = GraphicalUserInterface.initiateWebApiGET("bid?fields=messages", myApiKey);
		try {
			ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
			String output="";
			String studentRequest  = "";
			allRequests.removeAllItems();
			requestMade.setText("");
			for (ObjectNode node : userNodes) {
				// process the initiator id to remove extra quotations
				String initId = node.get("initiator").get("id").toString();
				String initiatorId = GraphicalUserInterface.removeQuotations(initId);
			
				// find requests made by student by comparing userId and initiatorId
				if(initiatorId.equals(userId) & node.get("dateClosedDown").toString().equals("null") ) {
					System.out.println("Found bid made by student");
					String bidId = GraphicalUserInterface.removeQuotations(node.get("id").toString());
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
