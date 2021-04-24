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
	
	JButton msgBtn, buyOutBtn;
	public String userId;
	private static final String myApiKey = "";
	
	// user inputs for messages
	private static JTextField msgContent;
	// container to hold all student requests
	private static JComboBox allRequests;
	
	// list of all students' bid id since it is needed make message 
	private ArrayList<String> allStudentBidList = new ArrayList<String>();

	
	public TutorGUI() {

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
        
        frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buyOutBtn) {
			//buyOut();
			sendMsg("Buy Out");
		}
		else if(e.getSource() == msgBtn){
			//placeBid();
			sendMsg("Place Bid");
		}
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
	
	
	/* Method to create a new class instances in db.
	 * For now: new subject can be created and new bid can be created */
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
			String output="";
			allRequests.removeAllItems();
			for (ObjectNode node: jsonNodes) {
				
				for (JsonNode bidNode : node.get("initiatedBids")) {
					// show bids that are not closed down
					if(bidNode.get("dateClosedDown").toString().equals("null") ) {
						String closeTimeDb = bidNode.get("additionalInfo").get("requestClosesAt").toString();
						String bidCloseTime = GraphicalUserInterface.removeQuotations(closeTimeDb); 
						String status = bidNode.get("type").toString();
						String requester = node.get("userName").toString();
						String subject = bidNode.get("subject").get("name").toString();
						String topic = bidNode.get("subject").get("description").toString();
						output = "Status: "+status+"    "+"Requested by: "+requester+"    "+ "Subject: "+subject  +"    "+ "Topic: "+ topic+"    " + "Closes at: " + bidCloseTime;
						allRequests.addItem(output);
						
						// store all bidIds in allStudentBidList
						String bidId = bidNode.get("id").toString();
						allStudentBidList.add(bidId);
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
	}
}
