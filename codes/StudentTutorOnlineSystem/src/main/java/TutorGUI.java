import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	
	JButton submitButton, showRequests;
	public String userId;
	private static final String myApiKey = "";
	
	// user inputs for subject, lesson(description), session time, rate and number of sessions
	private static JTextField subjectText, descText, timeInput, rateIn, sessionNum;
	// chosen qualification level
	private static JComboBox qualList, timeList, daysBox, allRates, allRequests;

	
	
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
        JLabel relListTitle = new JLabel("Relevant Student requests");
        relListTitle.setBounds(10,50,450,25);
        panel.add(relListTitle);
        
        
        JLabel instruction = new JLabel("Select a request and then click on 'bid' to make a bid");
        instruction.setBounds(10,80,1000,25);
        instruction.setForeground(Color.red);
        panel.add(instruction);
        
        
        // take user inputs over here
        JLabel allRequestList = new JLabel("All requests made by students");
        allRequestList.setBounds(10,120,450,25);
        panel.add(allRequestList);
        
        panel.add(instruction);
        
        allRequests = new JComboBox();
        allRequests.setBounds(10, 150, 700, 25);
        panel.add(allRequests);
        
        
        frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/* Show the tutor all the requests made by students  */
	protected void showAllStudentRequests() {
		System.out.println("Function started");
		HttpResponse<String> userResponse = GraphicalUserInterface.initiateWebApiGET("user?fields=initiatedBids", myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
			String output="";
			allRequests.removeAllItems();
			for (ObjectNode node: jsonNodes) {
				
				for (JsonNode bidNode : node.get("initiatedBids")) {
					String status = bidNode.get("type").toString();
					String requester = node.get("userName").toString();
					String subject = bidNode.get("subject").get("name").toString();
					String topic = bidNode.get("subject").get("description").toString();
					output = "Status: "+status+"    "+"Requested by: "+requester+"    "+ "Subject: "+subject  +"    "+ "Topic: "+ topic;
					allRequests.addItem(output);
				}
			}
		}
		catch(Exception e) {
			System.out.println(e.getCause());
		}
	}
	
	
	@Override
	String webApiPOST(String endpoint, String subID) {
		// TODO Auto-generated method stub
		return null;
	}

}
