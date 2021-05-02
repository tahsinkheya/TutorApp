import java.awt.Color;
import java.awt.Font;
import java.net.http.HttpResponse;

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
					String firstPartyIdRaw = node.get("firstParty").get("id").toString();
					String firstPartyId = GuiAction.removeQuotations(firstPartyIdRaw);
					String secondPartyIdRaw = node.get("secondParty").get("id").toString();
					String secondPartyId = GuiAction.removeQuotations(secondPartyIdRaw);
					
					// if student is the first party, then tutor is second party and vice versa
					String tutorFullName = "";
					String studentFullName = "";
					if(id.equals(firstPartyId)) {
						// tutor information
						String tutorGivenNameQ =  node.get("secondParty").get("givenName").toString();
						String tutorFamilyNameQ =  node.get("secondParty").get("familyName").toString();
						String tutorGivenName = GuiAction.removeQuotations(tutorGivenNameQ);
						String tutorFamilyName = GuiAction.removeQuotations(tutorFamilyNameQ);
						tutorFullName = tutorGivenName +" "+ tutorFamilyName;
						
						
						String studentGivenNameQ =  node.get("firstParty").get("givenName").toString();
						String studentFamilyNameQ =  node.get("firstParty").get("familyName").toString();
						String studentGivenName = GuiAction.removeQuotations(studentGivenNameQ);
						String studentFamilyName = GuiAction.removeQuotations(studentFamilyNameQ);
						studentFullName = studentGivenName +" "+ studentFamilyName;
						
					}
					else if(id.equals(secondPartyId)) {
						
						// tutor name
						String tutorGivenNameQ =  node.get("firstParty").get("givenName").toString();
						String tutorFamilyNameQ =  node.get("firstParty").get("familyName").toString();
						String tutorGivenName = GuiAction.removeQuotations(tutorGivenNameQ);
						String tutorFamilyName = GuiAction.removeQuotations(tutorFamilyNameQ);
						tutorFullName = tutorGivenName +" "+ tutorFamilyName;
						
						
						String studentGivenNameQ =  node.get("secondParty").get("givenName").toString();
						String studentFamilyNameQ =  node.get("secondParty").get("familyName").toString();
						String studentGivenName = GuiAction.removeQuotations(studentGivenNameQ);
						String studentFamilyName = GuiAction.removeQuotations(studentFamilyNameQ);
						studentFullName = studentGivenName +" "+ studentFamilyName;
					
					}
					
					// has to be a user related to this contract
					if (id.equals(firstPartyId) ||id.equals(secondPartyId)) {
						// subject information
						String subject = GuiAction.removeQuotations(node.get("subject").get("name").toString());
						String lesson = GuiAction.removeQuotations(node.get("subject").get("description").toString());
						String dateFinalized = GuiAction.removeQuotations(node.get("dateSigned").toString());
						
						
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
						
						
						String twoParties = "";
						if (type.equals("Student")) {
							twoParties = "Student: "+ studentFullName + "\n"+ "Tutor: "+ tutorFullName +"\n";
						}
						else {
							// for tutor page, tutor is main user
							twoParties = "Tutor: "+ studentFullName + "\n"+ "Student: "+ tutorFullName +"\n";
						}
						
						String subjectInfo = "Subject: "+ subject + "\n" + "Subject Description: "+ lesson + "\n" +"Tutor Qualification: "+tutorQualification +"\n"+ "Tutor Competency: "+ tutorCompetency + "\n";  
						String sessionInfo  = "Number of Sessions per week: "+weeklySessions + "\n" + "Hours per lession: "+studyHrs +"\n" + "Rate: "+ rate + "\n";
						String signDate = "Contract signed on: "+ dateFinalized + "\n";
						String dottedLine = "--------------------------------------------------------------------------"+"\n";
						output += twoParties +subjectInfo +sessionInfo+signDate+dottedLine;
				
						// console outputs
//						System.out.println("Student: "+ studentFullName);
//						System.out.println("Tutor: "+ tutorFullName);
//						System.out.println("Subject: "+ subject);
//						System.out.println("Subject Description: "+ lesson);
//						System.out.println("Tutor Competency: "+ tutorCompetency);
//						System.out.println("Number of Sessions per week: "+weeklySessions);
//						System.out.println("Hours per lession: "+studyHrs);
//						System.out.println("Rate: "+rate);
//						System.out.println("Contract signed on: "+ dateFinalized);;
//						System.out.println("---------------------------------------------------------------------");
//
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



}
