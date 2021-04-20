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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class StudentGUI implements ActionListener {
	public JLabel name;
	public JPanel panel;
	
	JButton submitButton;
	public String userId;
	private static final String myApiKey = "";
	
	private static JTextField subjectText, descText;
	
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
        JComboBox qualList = new JComboBox(qualificationTypes);
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
        
        JTextField time = new JTextField(20);
        time.setBounds(100,170,50,25);
        panel.add(time);
        
        String[] allTimes = {"AM", "PM"};
        JComboBox timeList = new JComboBox(allTimes);
        timeList.setBounds(150, 170, 70, 25);
        panel.add(timeList);
        
        // Day selection
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        JComboBox daysBox = new JComboBox(days);
        daysBox.setSelectedIndex(0);
        daysBox.setBounds(250, 170, 200, 25);
        panel.add(daysBox);
        
        // Weekly Sessions
        JLabel session = new JLabel("Weekly Sessions: ");
        session.setBounds(10, 200, 200, 25);
        panel.add(session);
        
        NumberFormat amountFormat = null;
        int num = 2;
        JFormattedTextField sessionIn= new JFormattedTextField(amountFormat);
        sessionIn.setValue(num);
        sessionIn.setBounds(150,200,50,25);
        panel.add(sessionIn);
        
        
        // Payment Rate
     
        JLabel rate = new JLabel("Rate (RM): ");
        rate.setBounds(10, 240, 100, 25);
        panel.add(rate);
        
        JTextField rateIn = new JTextField(20);
        rateIn.setBounds(80, 240, 80, 25);
        panel.add(rateIn);
        
        String[] rateTypes = {"per hour", "per session"};
        JComboBox allRates = new JComboBox(rateTypes);
        allRates.setBounds(180, 240, 100, 25);
        panel.add(allRates);
        
        // Creating request button
        
        submitButton = new JButton("Make Request");
        submitButton.setBounds(10, 270, 120, 25);
        submitButton.addActionListener(this);
        panel.add(submitButton);
        
        
        // Setting the frame visibility to true
        frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == submitButton)
	          saveRequest();	
	}
	
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
	
	
	private void webApiPOST(String endpoint) {
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
			// create a new JSON object for bid
			jsonString = "{" +
					"\"type\":\"" + subjectText.getText() + "\"," +
					"\"initiatorId\":\"" + subjectText.getText() + "\"," +
					"\"dateCreated\":\"" + subjectText.getText() + "\"," +
					"\"subjectId\":\"" + subjectText.getText() + "\"," +
					"\"additionalInfo\":\"" + descText.getText()+ "\"" +
				"}";
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
			System.out.println("Created new subject in database");
		}
		catch(Exception e){
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			
		}
		/*
		if(isSubject) {
			findSubject();
		}
		*/
		
	}
	
	
	private void saveRequest() {
		
		System.out.println("Submit button clicked");
		System.out.println(userId);
		findSubject();
	}
	
	
	/* Method to get the subject id for the subject given as input by the user */
	private String findSubject() {
		String subjectID = null;
		// get the user inputs
		String userSub = subjectText.getText();
		String userDesc = descText.getText();
		HttpResponse<String> subResponse = initiateWebApiGET("subject");
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(subResponse.body(), ObjectNode[].class);
			
			// look for the subject and description in the database
			for (ObjectNode node: jsonNodes) {
				System.out.println(node.toString());
		      	String subFromDB = node.get("name").asText();
		      	String descFromDB = node.get("description").asText();
				if (subFromDB.equals(userSub) & descFromDB.equals(userDesc) ) {
					System.out.println("Match found");
					subjectID = node.get("id").asText();
					System.out.println(node.toString());
					return subjectID;
				}	
			}
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
		
		// if the subject is not found in database, then create new subject
		System.out.println("Match not found, so creating new subject ");
		webApiPOST("subject");
		
		return subjectID;	
	}
}


