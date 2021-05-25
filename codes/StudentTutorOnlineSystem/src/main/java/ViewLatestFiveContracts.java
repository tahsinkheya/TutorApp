import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ViewLatestFiveContracts implements GuiAction, ActionListener {
	private JPanel panel;
	private JComboBox tutorList, contractList;
	private String userId, userFullName,subject,requiredComp;
	private Vector comboBoxItems, contractVector;
	private JButton showContracts,selectContractSameTutor,selectContractDiffTutor,selectContractCond;
	private JTextArea contractField;
	private JSONObject allContractInfo;
	private ArrayList<String> allContracts;
	private ArrayList<String> contractIds=new ArrayList<>();
	private JFrame pageFrame;
	
	public ViewLatestFiveContracts(String id, String studentFullName,String sub,String comp) {
		userId = id;
		userFullName = studentFullName;
		subject=sub;
		requiredComp=comp;
	}
	
	@Override
	public void show() {
		pageFrame = new JFrame("Latest 5 contracts with a tutor");
		// Setting the width and height of frame
		pageFrame.setSize(900, 720);
		pageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel= new JPanel();
		panel.setBackground(new Color(172, 209, 233));
		panel.setLayout(null);
		
		JLabel pageTitle = new JLabel("Latest 5 contracts with a tutor for renewing contract for "+subject);
		pageTitle.setBounds(380,20,700,25);
		panel.add(pageTitle);
		
		JLabel listLabel = new JLabel("Select a tutor and click view contracts");
		listLabel.setBounds(10,50,400,25);
		panel.add(listLabel);
		
		
		comboBoxItems =new Vector();
		tutorList = new JComboBox(comboBoxItems);
		tutorList.setBounds(10, 80, 150, 25);
        panel.add(tutorList);
        
        showContracts = new JButton("Show latest contracts");
        showContracts.setBounds(10, 120, 270, 25);
        showContracts.addActionListener(this);
        panel.add(showContracts);
        
        
        
        JLabel contractListDetails = new JLabel("Contracts will appear here as a list");
        contractListDetails.setBounds(10,180,400,25);
		panel.add(contractListDetails);
        
        contractVector = new Vector();
        contractList = new JComboBox(contractVector);
        contractList.setBounds(10, 210, 300, 25);
        panel.add(contractList);
//"<html>fnord<br />foo</html>"
		selectContractSameTutor = new JButton("<html>Select this contract terms and conditions <br />and this tutor</html>");
		selectContractSameTutor.setBounds(10, 240, 330, 50);
		selectContractSameTutor.addActionListener(this);
		panel.add(selectContractSameTutor);

		selectContractDiffTutor = new JButton("<html>Select this contract terms and conditions<br /> and a different tutor</html>");
		selectContractDiffTutor.setBounds(10, 300, 330, 50);
		selectContractDiffTutor.addActionListener(this);
		panel.add(selectContractDiffTutor);

		selectContractCond = new JButton("<html>Select this tutor with different <br />terms and conditions</html>");
		selectContractCond.setBounds(10, 360, 330, 50);
		selectContractCond.addActionListener(this);
		panel.add(selectContractCond);

        contractField = new JTextArea();
        contractField.setBounds(360, 50, 495, 620);
        contractField.setEditable(false);
		panel.add(contractField);
		
		pageFrame.add(panel);
		pageFrame.setVisible(true);
		
		showTutorsWithContracts();
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showContracts){
			findLatestContracts();
		}
	}
	
	/** 
	 * Method to show the tutors with whom the student has a contract
	**/
	private void showTutorsWithContracts() {
		allContractInfo=new JSONObject();
		allContracts = new ArrayList<String>();
		ArrayList<String> tutorTracker = new ArrayList<String>();
		HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract", myApiKey);
		JSONObject contDetails = new JSONObject();
		
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(contResponse.body(), ObjectNode[].class);
		
			for (ObjectNode node: jsonNodes) {
				// get the signed date for contract to check if it is null or not
				String dateSign = node.get("dateSigned").toString();	
				
				// not null means contract has been finalized
				if (!dateSign.equals("null")) {
					ArrayList<String> userFullNames = viewContractAction.getStudentAndTutorNames(node);
					ArrayList<String> contractDetails = viewContractAction.getcontractDetails(node, userId);
					String studentFullName = userFullNames.get(0);
					String tutorFullName = userFullNames.get(1);
					//System.out.println("Contract between: "+studentFullName +" and "+ tutorFullName);
					//System.out.println("Current Student: "+ userFullName);
					// write to combo box and avoid repeated names
					if(!tutorTracker.contains(tutorFullName)  && (studentFullName.equals(userFullName)) ) {
						tutorTracker.add(tutorFullName);
						comboBoxItems.add(tutorFullName);				
					}
					createContractObj(contractDetails, tutorFullName);
				}
			}
			tutorList.setSelectedIndex(0);
		}
		catch(Exception e) {
			System.out.println("Error!!!");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
		
		
	}
	
	/**
	 * Method to put the contracts into an object array
	 * @param contractDetails
	 * @param tutorName
	 */
	private void createContractObj(ArrayList<String> contractDetails, String tutorName) {
        // create the additional info
		if(!contractDetails.isEmpty()) {
			allContractInfo.put("tutorName", tutorName);
			allContractInfo.put("contract", contractDetails);
	        String jsonString = allContractInfo.toString(); // convert to string
	        allContracts.add(jsonString);
	        //System.out.println(jsonString);	
		}
	}
	
	
	/**
	 * Method to find the latest contracts
	 */
	private void findLatestContracts() {
		// clear the combo box for the newly selected tutor
		contractVector.removeAll(contractVector);

		ArrayList<String> signedDates = new ArrayList<String>();
		//System.out.println(allContracts.toString()+"\n");
		String selectedTutor = tutorList.getSelectedItem().toString();
		JSONParser parser = new JSONParser();  
		JSONObject json; 
		
		for (int counter = 0; counter < allContracts.size(); counter++) { 		      
	          try {
				json = (JSONObject) parser.parse(allContracts.get(counter));
				String tutor = json.get("tutorName").toString();
				
				if(selectedTutor.equals(tutor)) {
					String contract = json.get("contract").toString();
					ArrayList<String> contractArr = (ArrayList<String>) json.get("contract");
					signedDates.add(contractArr.get(7));
				}
				
			} catch (ParseException e) {
				System.out.println("Error!!!");
				System.out.println(e.getMessage());
				System.out.println(e.getCause());
				e.printStackTrace();
			}
	      }
		
		ArrayList<String> latestContractDates = sortDates(signedDates);
		showLatestContracts(latestContractDates);
	}
	
	/**
	 * Method to display the latest contracts. From latest to oldest upto 5 contracts
	 * 
	 */
	private void showLatestContracts(ArrayList<String> latestContractDates) {
		
		String output = "";
		ArrayList<String> signedDates = new ArrayList<String>();
		
		String selectedTutor = tutorList.getSelectedItem().toString();
		JSONParser parser = new JSONParser();  
		JSONObject json; 
		
		for (int counter = 0; counter < allContracts.size(); counter++) { 		      
	          //System.out.println(allContracts.get(counter).toString()); 
	          try {
				json = (JSONObject) parser.parse(allContracts.get(counter));
				//System.out.println(json.get("tutorName").toString());
				String tutor = json.get("tutorName").toString();
				if(selectedTutor.equals(tutor)) {
					ArrayList<String> contract = (ArrayList<String>) json.get("contract");
					String dateFinalized = contract.get(7);
					for (int k = 0; k < latestContractDates.size(); k++) {
						String contDate = latestContractDates.get(k);
						
						if(dateFinalized.contains(contDate)) {
							// the contract details
							String contractComboBoxItem = "";
							String subject = "Subject: "+ contract.get(0);
							String lesson = "Lesson: "+contract.get(1);
							String tutorQualification = "Tutor Qualification: "+contract.get(2);
							String tutorCompetency = "Tutor Competency: "+contract.get(3);
							String weeklySessions = "Weekly Sessions: "+contract.get(4);
							String studyHrs = "Study Hours: "+contract.get(5);
							String rate = "Rate: "+contract.get(6);
							String contractExpiryDate =contract.get(8);
							
							Date signedDate = formatDate(dateFinalized);
							Date expiryDate = formatDate(contractExpiryDate);
							
							String dottedLines = "-------------------------------------------------------------------------------------------------------------------";		
							output +="Contract No: "+(k+1) +"\n"+tutor +"\n"+ subject + "   "+lesson+ "\n"+tutorQualification+ "    "+tutorCompetency+ "\n"+weeklySessions+ "\n"+studyHrs+ "    "+rate+ "\nSigned On: "+signedDate+"    Expires On: "+expiryDate+"\n"+dottedLines+"\n";
							
							// add the tutor and contract order number to the list for contracts
							contractComboBoxItem += (k+1) +")  Contract with : "+tutor ;
							contractVector.add(contractComboBoxItem);
						}
						
					}		
				}
			} catch (ParseException e) {
				System.out.println("Error!!!");
				System.out.println(e.getMessage());
				System.out.println(e.getCause());
				e.printStackTrace();
			}
	          
	      }
		contractList.setSelectedIndex(0);
		//output+= output+output;
		contractField.setText(output);
		
	}
	
	/** Method to sort the dates from past to near-present/present time**/
	private ArrayList<String> sortDates(ArrayList<String> signedDates) {
		ArrayList<String> sortedDates = new ArrayList<String>();
		String output = "";
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList<Date> allDates=new ArrayList<>();
		
		for(String date : signedDates) {
			allDates.add(formatDate(date));
			
		}
		Collections.sort(allDates);
		String[] splitDates;
		// show the last five if more than five contracts are there
		if(allDates.size()>5) {
			System.out.println("More than 5 contracts exist");
			int endIndex = allDates.size()-5;
			
			for (int i=allDates.size()-1; i>=endIndex; i--) {
				splitDates = sdf.format(allDates.get(i)).split(" ");
				output = splitDates[0]+"T"+splitDates[1];
				sortedDates.add(output);
			}
		}
		
		// less than five contracts, so show all five
		else {
			for (int i=0; i<allDates.size(); i++) {
				splitDates = sdf.format(allDates.get(i)).split(" ");
				output = splitDates[0]+"T"+splitDates[1];
				sortedDates.add(output);
			}
		}
		return sortedDates;
		
	}
	
	/**
	 * Method to remove .Z part from date string 
	**/
	static Date formatDate(String date) {
		Date output = new Date();
		String removeDots = date.substring(0, (date.length()-5));
		String[] dateSplit = removeDots.split("T");
		String datePart = dateSplit[0]+" "+ dateSplit[1];
		
		// format until seconds
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			output = sdf.parse(datePart);
			
		} catch (java.text.ParseException e) {
			System.out.println("Error!!!");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			e.printStackTrace();
			e.printStackTrace();
		}
		return output;
	}
}
