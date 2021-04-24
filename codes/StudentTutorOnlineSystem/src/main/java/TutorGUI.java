import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Rafaquat
 *
 */
public class TutorGUI extends GraphicalUserInterface implements ActionListener {
	
	public JLabel name;
	public JPanel panel;
	
	JButton msgBtn, buyOutBtn; 	//testBtn
	public String userId;
	private static final String myApiKey = "";
	
	// user inputs for messages
	private static JTextField msgContent;
	// container to hold all student requests
	private static JComboBox allRequests;
	private static JLabel competencyAlert;
	
	// list of all students' bid id since it is needed make message 
	private ArrayList<String> allStudentBidList = new ArrayList<String>();

	
	public TutorGUI() {

		// Creating instance of JFrame
        JFrame frame = new JFrame("Tutor Homepage");
        // Setting the width and height of frame
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      
        panel = new JPanel();    
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);
        
        
        // take user inputs over here
        JLabel relListTitle = new JLabel("All requests made by students");
        relListTitle.setBounds(10,50,450,25);
        panel.add(relListTitle);
        
        
        JLabel instruction = new JLabel("Select a request and then click on either 'Buy Out' or 'Place Bid' to make a bid");
        instruction.setBounds(10,80,1200,25);
        instruction.setForeground(Color.red);
        panel.add(instruction);
        
        
        allRequests = new JComboBox();
        allRequests.setBounds(10, 120, 700, 25);
        panel.add(allRequests);
        
        buyOutBtn = new JButton("Buy Out");
        buyOutBtn.setBounds(10, 180, 80, 25);
        buyOutBtn.addActionListener(this);
        panel.add(buyOutBtn);
        
        
        JLabel msgLabel = new JLabel("Your Message");
        msgLabel.setBounds(10, 220,200,25);
        panel.add(msgLabel);
        
        msgContent = new JTextField(20);
        msgContent.setBounds(100, 220,165,25);
        panel.add(msgContent);
        
        msgBtn = new JButton("Place Bid");
        msgBtn.setBounds(100, 250, 100, 25);
        msgBtn.addActionListener(this);
        panel.add(msgBtn);
        
        /*
        testBtn = new JButton("Test");
        testBtn.setBounds(10, 280, 80, 25);
        testBtn.addActionListener(this);
        panel.add(testBtn);
        */
        
        // take user inputs over here
        competencyAlert = new JLabel();
        competencyAlert.setBounds(10,300,450,25);
        panel.add(competencyAlert);
        
        frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buyOutBtn) {
			String bidId = getSelectedRequest();
			String subName = getSubjectById(bidId);
			int level = findTutorCompetency(subName);
			isCompetent(bidId, level);
			if(isCompetent(bidId, level)) {
				sendMsg("Buy Out");
			}
			else {
				competencyAlert.setText("You do not have the required competency to bid on this request");
			}
		}
		else if(e.getSource() == msgBtn){
			String bidId = getSelectedRequest();
			String subName = getSubjectById(bidId);
			int level = findTutorCompetency(subName);
			isCompetent(bidId, level);
			if(isCompetent(bidId, level)) {
				sendMsg("Place Bid");
			}
			else {
				competencyAlert.setText("You do not have the required competency to bid on this request");
			}
		}
		/*
		else if(e.getSource() == testBtn){
			String bidId = getSelectedRequest();
			String subName = getSubjectById(bidId);
			int level = findTutorCompetency(subName);
			isCompetent(bidId, level);
		}
		*/
	}
	
	/*Method to get the subject name from the bid Id*/
	private String getSubjectById(String bidId) {
		String endpoint = "bid/"+bidId;
		String subName = null;
		HttpResponse<String> response = GraphicalUserInterface.initiateWebApiGET(endpoint, myApiKey);
		try {
			ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
			
			String nodeId = userNode.get("subject").get("name").toString();
			subName = GraphicalUserInterface.removeQuotations(nodeId);
			System.out.println("Subject in the bid: "+subName);
			return subName;
			
		}
		catch (Exception e){
			System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
		return subName;
	}
	
	
	/* Method to find if the tutor's competency in the subject that they specialise in*/
	private int findTutorCompetency(String subName) {
		System.out.println("Inside the finding Competency function");
		String endpoint = "user/"+userId+"?fields=competencies.subject";
		int tutorcompetencyLevel = 0;
		HttpResponse<String> compResponse = GraphicalUserInterface.initiateWebApiGET(endpoint, myApiKey);
		try {
			ObjectNode userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode.class);
			
			for (JsonNode node : userNode.get("competencies")) {
				// get the subject name that the tutor teaches and compare it to the requested one.
				String nodeSubName = node.get("subject").get("name").toString();
				String tutorSubName = GraphicalUserInterface.removeQuotations(nodeSubName);
				if(tutorSubName.equals(subName)) {
					System.out.println("Found the subject for which competency is needed");
					tutorcompetencyLevel = node.get("level").asInt();
					System.out.println("Competency Level is: "+tutorcompetencyLevel);
					return tutorcompetencyLevel;
				}	
			}
		}
		catch (Exception e){
			System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
		
		// competency level is zero
		return tutorcompetencyLevel;
	}
	
	
	/* Method to find whether the tutor is competent enough to teach the subject in the bid
	 * The required competency given in the request can be obtained using the bidId */
	private boolean isCompetent(String bidId, int tutorCompetency) {
		System.out.println("Inside the Competency check function");
		String endpoint = "bid/"+bidId;
		HttpResponse<String> compResponse = GraphicalUserInterface.initiateWebApiGET(endpoint, myApiKey);
		try {
			ObjectNode userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode.class);
			// get the competency
			String bidComepetency = userNode.get("additionalInfo").get("requiredCompetency").toString();
			String requiredCompetency = GraphicalUserInterface.removeQuotations(bidComepetency);
			// convert to integer
			int reqCompetency = Integer.parseInt(requiredCompetency);
			if(tutorCompetency>= reqCompetency) {
				System.out.println("Tutor is elligible to teach the subject");
				return true;
			}
			
		}
		catch (Exception e){
			System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
		// tutor not elligible
		return false;
	}
	
	
	
	/* Method to get the bid id of the request selected from JComboBox */
	private String getSelectedRequest() {
		// get the index of the selected request
		int selectedRequestPos = allRequests.getSelectedIndex();
		// get the id of the selected request from list
		String bidIdFromList = allStudentBidList.get(selectedRequestPos);
		int bidLength = bidIdFromList.length();
		String selectedBidId = bidIdFromList.substring(1, bidLength-1); // remove additional quotations
		return selectedBidId;
	}
	
	/* Method to send the message for both buy out and placing bids */
	private void sendMsg(String intent) {
		String selectedBidId = getSelectedRequest();
		//System.out.println("Bid Id: " + selectedBidId);
		String msgPostDate = new Date().toInstant().toString(); // date of posting message
		String content = null;
		
		if(intent.equals("Buy Out")) {
			content = "I agree on all terms";
		}
		else if(intent.equals("Place Bid")) {
			content = msgContent.getText();
		}
		
		String jsonString = null;
		// create the message object
		JSONObject msgInfo=new JSONObject();
		msgInfo.put("bidId", selectedBidId);
		msgInfo.put("posterId", userId);
		msgInfo.put("datePosted", msgPostDate);
		msgInfo.put("content", content);
		JSONObject additionalInfo=new JSONObject();
		msgInfo.put("additionalInfo", additionalInfo);
		
		// convert message to JSON string
		jsonString = msgInfo.toString(); 
		webApiPOST("message", jsonString);
		
	}
	
	
	/* Method to create a new class instances in db
	 * This is to store the message in the database */
	@Override
	protected String webApiPOST(String endpoint, String jsonString) {
		String refId = null;  // id value to get the subject or bid

		// create a new message in the database 
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
			// get the id of the newly created object
			ObjectNode jsonNode = new ObjectMapper().readValue(postResponse.body(), ObjectNode.class);
			System.out.println("Bid/Message sent successfully");
			Student.showAllRequests();
		
		}
		catch(Exception e){
			System.out.println("Error !!!!");
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			
		}
		return refId;
	}
	
	
	/* Show the tutor all the requests made by students  */
	protected void showAllStudentRequests() {
		HttpResponse<String> userResponse = GraphicalUserInterface.initiateWebApiGET("user?fields=initiatedBids", myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
			//String output="";
			//allRequests.removeAllItems();
			for (ObjectNode node: jsonNodes) {
				
				for (JsonNode bidNode : node.get("initiatedBids")) {
					System.out.println(bidNode.toString());
					// show bids that are not closed down
					if(bidNode.get("dateClosedDown").toString().equals("null") ) {
						String closeTimeDb = "";
						String bidCloseTime = "";
						// this will throw an exception. The requested bid always has additional info, so this exception will not cause any problem
						if(bidNode.get("additionalInfo").equals(null)) {
							System.out.println("Additional info is null");
						}
						else {
							closeTimeDb = bidNode.get("additionalInfo").get("requestClosesAt").toString();
							bidCloseTime = GraphicalUserInterface.removeQuotations(closeTimeDb);
						}
						 
						String status = bidNode.get("type").toString();
						String requester = node.get("userName").toString();
						String subject = bidNode.get("subject").get("name").toString();
						String topic = bidNode.get("subject").get("description").toString();
						String output = "Status: "+status+"    "+"Requested by: "+requester+"    "+ "Subject: "+subject  +"    "+ "Topic: "+ topic+"    " + "Closes at: " + bidCloseTime;
						
						// put all available bids in the JCombo Box
						allRequests.addItem(output);
						
						// store all bidIds in allStudentBidList
						String bidId = bidNode.get("id").toString();
						allStudentBidList.add(bidId);
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("Error");
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			System.out.println(e.getCause());
		}
	}
}
