import java.awt.Color;
import java.awt.Font;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * 
 * @author Rafaquat
 * This class is used to show the finalized/signed contracts of both student and tutors
 */
public class viewContractAction implements GuiAction{
	private JFrame frame;
	private JPanel panel;
	private JLabel warning;
	private JTextArea contractDetails;
	private String id;
	private String type;
	
    public viewContractAction(String userid, String userType) {
    	id = userid;
    	type = userType;
    }
    
    /*
     * Method to load the UI for showing contracts
     */
    @Override
    public void show() {
    	frame = new JFrame("All Finalized Contracts");
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);
        panel.setBackground(new Color(172, 209, 233));


        contractDetails = new JTextArea();
        contractDetails.setBounds(30, 50, 700, 700);
        contractDetails.setEditable(false);
        panel.add(contractDetails);

        // for future warning messages
        warning=new JLabel();
        warning.setBounds(10,220,200,25);
        panel.add(warning);

        // making the page scrollable
        JScrollPane scrollBar = new JScrollPane (contractDetails);
        scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        

        frame.add(scrollBar);      
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // call the method to show contracts
        showFinalizedContracts();

    }
    
    
    
    /*
     * Method to show all contracts that have been finalized
     */
    protected void showFinalizedContracts() {
		String finalOutput = "";
		HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract", myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(contResponse.body(), ObjectNode[].class);
			String output = "Your Finalized Contracts: \n\n\n";
			
			for (ObjectNode node: jsonNodes) {
				// get the signed date for contract to check if it is null or not
				String dateSign = node.get("dateSigned").toString();	
				
				// not null means contract has been finalized
				if (!dateSign.equals("null")) {
					ArrayList<String> userFullNames = getStudentAndTutorNames(node);
					String studentFullName = userFullNames.get(0);
					String tutorFullName = userFullNames.get(1);
					ArrayList<String> details = getcontractDetails(node, id);
					
					// skip if contract does not involve current user
					if(!details.isEmpty()) {
						String subject = details.get(0);
						String lesson = details.get(1);
						String tutorQualification = details.get(2);
						String tutorCompetency = details.get(3);
						String weeklySessions = details.get(4);
						String studyHrs = details.get(5);
						String rate = details.get(6);						
						String dateFinalized = details.get(7);
						String contractExpiryDate = details.get(8);
						
						// format the sign and expiry data to a more readable form
						Date signedDate = ViewLatestFiveContracts.formatDate(dateFinalized);
						Date expiryDate = ViewLatestFiveContracts.formatDate(contractExpiryDate);
						
						String twoParties = "Student: "+ studentFullName + "\n"+ "Tutor: "+ tutorFullName +"\n";
						String subjectInfo = "Subject: "+ subject + "\n" + "Subject Description: "+ lesson + "\n" +"Tutor Qualification: "+tutorQualification +"\n"+ "Tutor Competency: "+ tutorCompetency + "\n";  
						String sessionInfo  = "Number of Sessions per week: "+weeklySessions + "\n" + "Hours per lession: "+studyHrs +"\n" + "Rate: "+ rate + "\n";
						String signDate = "Contract signed on: "+ signedDate + "\n" +"Contract expires on: "+ expiryDate + "\n";
						String dottedLine = "--------------------------------------------------------------------------"+"\n";
						String expirNot = expiryNotification(contractExpiryDate).toUpperCase() + "\n";
						
						if(expirNot.equals("")) {
							output += twoParties +subjectInfo +sessionInfo+signDate+dottedLine;
						}	
						else {
							output += twoParties +subjectInfo +sessionInfo+signDate+expirNot+dottedLine;
						}	
					}
				}
			}
			contractDetails.setText(output);
		}
		catch(Exception e) {
			System.out.println("Error!!!");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
		
	}
    
    /** Method to print contract expiry notifications for specific contracts **/
    private String expiryNotification(String contractExpiryDate) {
    		String output = "";
    		// split the numerical date parts, YYYY-MMMM-DDDD
			String[] contractExpiryTime = contractExpiryDate.split("-");
			String[] currentTime = new Date().toInstant().toString().split("-");
			
			String currentYear = currentTime[0];
			String expiryYear = contractExpiryTime[0];
			int currentMonth = Integer.parseInt(currentTime[1]);
			int expiryMonth = Integer.parseInt(contractExpiryTime[1]);
			
			// contract needs to expire the same year as current year
			if(currentYear.equals(expiryYear)) {
				// the month of expiry may have come or will come in a month's time
				if((expiryMonth-currentMonth) <= 1) {
					output = "This contract will expire soon";
					return output;
				}
			}
			return output;
    }
    
    /**
     * Method to get the student and tutor full names
     * @param node
     * @return ArrayList containing student and tutor full names
     */
    static ArrayList<String> getStudentAndTutorNames(ObjectNode node) {
    	ArrayList<String> studentAndTutor = new ArrayList<String>();
    	boolean firstPartyType = node.get("firstParty").get("isStudent").asBoolean();
    	String studentFullName;
		String tutorFullName;
		// first party is student and second party is tutor
		if(firstPartyType == true) {
			String studentGivenName = GuiAction.removeQuotations(node.get("firstParty").get("givenName").toString());
			String studentFamilyName = GuiAction.removeQuotations(node.get("firstParty").get("familyName").toString());
			studentFullName = studentGivenName +" "+ studentFamilyName;
			
			String tutorGivenName = GuiAction.removeQuotations(node.get("secondParty").get("givenName").toString());
			String tutorFamilyName = GuiAction.removeQuotations(node.get("secondParty").get("familyName").toString());
			tutorFullName = tutorGivenName +" "+ tutorFamilyName;
		}
		
		// second party is student and first party is tutor
		else {
			String studentGivenName = GuiAction.removeQuotations(node.get("secondParty").get("givenName").toString());
			String studentFamilyName = GuiAction.removeQuotations(node.get("secondParty").get("familyName").toString());
			studentFullName = studentGivenName +" "+ studentFamilyName;
			
			String tutorGivenName = GuiAction.removeQuotations(node.get("firstParty").get("givenName").toString());
			String tutorFamilyName = GuiAction.removeQuotations(node.get("firstParty").get("familyName").toString());
			tutorFullName = tutorGivenName +" "+ tutorFamilyName;
		}
		
		studentAndTutor.add(studentFullName);
		studentAndTutor.add(tutorFullName);
		return studentAndTutor;
    	
    }
    
    /*
     * Method to receive details about each contract
     */
    static ArrayList<String> getcontractDetails(ObjectNode node, String id) {
    	ArrayList<String> contractDetails = new ArrayList<String>();
    	String contractId = GuiAction.removeQuotations(node.get("id").toString());
    	String firstPartyId = GuiAction.removeQuotations(node.get("firstParty").get("id").toString());
		String secondPartyId = GuiAction.removeQuotations(node.get("secondParty").get("id").toString());
		
		// has to be a user related to this contract
		if (id.equals(firstPartyId) || id.equals(secondPartyId)) {
			// subject information
			String subject = GuiAction.removeQuotations(node.get("subject").get("name").toString());
			String lesson = GuiAction.removeQuotations(node.get("subject").get("description").toString());
			String dateFinalized = GuiAction.removeQuotations(node.get("dateSigned").toString());
			String contractExpiryDate = GuiAction.removeQuotations(node.get("expiryDate").toString());
			
			String studyHrs= "";
			String weeklySessions = "";
			String rate = "";
			String lessonStatus = node.get("lessonInfo").toString();
			String tutorQualification = "";
			String tutorCompetency = "";
			
			// to handle some empty pre-existing contracts
			if (!lessonStatus.equals("{}")) {
				studyHrs =  GuiAction.removeQuotations(node.get("lessonInfo").get("hoursPerLesson").toString());
				weeklySessions =  GuiAction.removeQuotations(node.get("lessonInfo").get("weeklySession").toString());
				rate =  GuiAction.removeQuotations(node.get("lessonInfo").get("rate").toString());
				tutorQualification = GuiAction.removeQuotations(node.get("lessonInfo").get("tutorQualification").toString());
				tutorCompetency = GuiAction.removeQuotations(node.get("lessonInfo").get("competency").toString());
			}
			
			contractDetails.add(subject);
			contractDetails.add(lesson);
			contractDetails.add(tutorQualification);
			contractDetails.add(tutorCompetency);
			contractDetails.add(weeklySessions);
			contractDetails.add(studyHrs);
			contractDetails.add(rate);
			contractDetails.add(dateFinalized);
			contractDetails.add(contractExpiryDate);
			contractDetails.add(contractId);
		}
		else {
			System.out.println("");
		}
		
		return contractDetails;
    }
    
    static boolean contractNotification(String userId) {
    	String notification = "";
		HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract", myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(contResponse.body(), ObjectNode[].class);
			for (ObjectNode node: jsonNodes) {
				// get the signed date for contract to check if it is null or not
				String dateSign = node.get("dateSigned").toString();	
				
				// not null means contract has been finalized
				if (!dateSign.equals("null")) {
					String firstPartyId = GuiAction.removeQuotations(node.get("firstParty").get("id").toString());
					String secondPartyId = GuiAction.removeQuotations(node.get("secondParty").get("id").toString());
					
					// has to be a user related to this contract
					if (userId.equals(firstPartyId) || userId.equals(secondPartyId)) {
						String[] contractExpiryTime = GuiAction.removeQuotations(node.get("expiryDate").toString()).split("T");
						String[] currentTime = new Date().toInstant().toString().split("T");
						//System.out.println("User Id: "+userId);
						String currentDate = currentTime[0];
						String expiryDate = contractExpiryTime[0];
						
						 SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
						 Date d1 = sdf.parse(currentDate);
				         Date d2 = sdf.parse(expiryDate);
				         long difference_In_Time= d2.getTime() - d1.getTime();
				         long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
			  
				         long difference_In_Days = (difference_In_Time/ (1000 * 60 * 60 * 24))% 365;
				         if( (difference_In_Years == 0) && (difference_In_Days <= 31)) {
				        	 System.out.println("There are contracts expiring in a month time");
				        	 return true;
				         }
				         
					}
				}
				

			}
		
			
		}
		catch(Exception e) {
			System.out.println("Error!!!");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
		return false;
		
    }
    

}
