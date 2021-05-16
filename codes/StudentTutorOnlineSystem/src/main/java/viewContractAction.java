import java.awt.Color;
import java.awt.Font;
import java.net.http.HttpResponse;
import java.util.ArrayList;

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


        JPanel panel = new JPanel();
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
					//boolean firstPartyType = node.get("firstParty").get("isStudent").asBoolean();
					//boolean secondPartyType = node.get("secondParty").get("isStudent").asBoolean();
					
					ArrayList<String> userFullNames = getStudentAndTutorNames(node);
					String studentFullName = userFullNames.get(0);
					//System.out.println(studentFullName);
					String tutorFullName = userFullNames.get(1);
					//System.out.println(tutorFullName);
					
					ArrayList<String> details = getcontractDetails(node);
					
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
						
						
						String twoParties = "Student: "+ studentFullName + "\n"+ "Tutor: "+ tutorFullName +"\n";
						String subjectInfo = "Subject: "+ subject + "\n" + "Subject Description: "+ lesson + "\n" +"Tutor Qualification: "+tutorQualification +"\n"+ "Tutor Competency: "+ tutorCompetency + "\n";  
						String sessionInfo  = "Number of Sessions per week: "+weeklySessions + "\n" + "Hours per lession: "+studyHrs +"\n" + "Rate: "+ rate + "\n";
						String signDate = "Contract signed on: "+ dateFinalized + "\n" +"Contract expires on: "+ contractExpiryDate + "\n";
						String dottedLine = "--------------------------------------------------------------------------"+"\n";
						output += twoParties +subjectInfo +sessionInfo+signDate+dottedLine;
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
    
    /**
     * Method to get the student and tutor full names
     * @param node
     * @return ArrayList containing student and tutor full names
     */
    private ArrayList<String> getStudentAndTutorNames(ObjectNode node) {
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
    private ArrayList<String> getcontractDetails(ObjectNode node) {
    	ArrayList<String> contractDetails = new ArrayList<String>();
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
		}
		else {
			System.out.println("Match not found");
		}
		
		return contractDetails;
    }
    

}
