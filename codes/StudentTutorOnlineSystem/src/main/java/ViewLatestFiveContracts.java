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
	private JComboBox tutorList;
	private String userId;
	private Vector comboBoxItems;
	private JButton showContracts;
	private JTextArea contractField;
	private JSONObject allContractInfo;
	private ArrayList<String> allContracts;
	private JScrollPane scrollBar;
	private JFrame pageFrame;
	
	public ViewLatestFiveContracts(String id) {
		userId = id;
	}
	
	@Override
	public void show() {
		pageFrame = new JFrame("Latest 5 contracts with a tutor");
		// Setting the width and height of frame
		pageFrame.setSize(900, 600);
		pageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel= new JPanel();
		panel.setBackground(new Color(172, 209, 233));
		panel.setLayout(null);
		
		JLabel pageTitle = new JLabel("View Latest 5 contracts with a tutor");
		pageTitle.setBounds(400,20,400,25);
		panel.add(pageTitle);
		
		JLabel listLabel = new JLabel("Select a tutor and click view contracts");
		listLabel.setBounds(10,50,400,25);
		panel.add(listLabel);
		
		
		comboBoxItems =new Vector();
		tutorList = new JComboBox(comboBoxItems);
		tutorList.setBounds(10, 80, 500, 25);
        panel.add(tutorList);
        
        showContracts = new JButton("Show latest contracts");
        showContracts.setBounds(10, 120, 270, 25);
        showContracts.addActionListener(this);
        panel.add(showContracts);
        
        contractField = new JTextArea();
        contractField.setBounds(10, 150, 450, 700);
        contractField.setEditable(false);
		panel.add(contractField);
		
		// making the page scrollable
        //scrollBar = new JScrollPane (contractField);
        //scrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //getContentPane().add(scrollBar);
        //panel.add(scrollBar);
        
		pageFrame.add(panel);
		pageFrame.setVisible(true);
		
		
		showTutorsWithContracts();
		
		//showLatestContracts();
		
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
					
					//System.out.println(node.toString());
					//System.out.println("Inside");
					ArrayList<String> userFullNames = viewContractAction.getStudentAndTutorNames(node);
					ArrayList<String> contractDetails = viewContractAction.getcontractDetails(node, userId);
					String tutorFullName = userFullNames.get(1);
					
					
					if(!tutorTracker.contains(tutorFullName)) {
						//System.out.println("combo box");
						
						tutorTracker.add(tutorFullName);
						comboBoxItems.add(tutorFullName);				
					
						//System.out.println(tutorFullName);
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
	
	
	private void createContractObj(ArrayList<String> contractDetails, String tutorName) {
        // create the additional info
		if(!contractDetails.isEmpty()) {
			allContractInfo.put("tutorName", tutorName);
			allContractInfo.put("contract", contractDetails);
	        String jsonString = allContractInfo.toString(); // convert to string
	        allContracts.add(jsonString);
	        System.out.println(jsonString);
	        //System.out.println("ALL: "+allContracts.size());
			
		}
	}
	
	private void findLatestContracts() {
		ArrayList<String> signedDates = new ArrayList<String>();
		System.out.println(allContracts.toString()+"\n");
		String selectedTutor = tutorList.getSelectedItem().toString();
		JSONParser parser = new JSONParser();  
		JSONObject json; 
		// "2021-05-16T15:29:10.742Z", "2021-05-16T15:32:57.558Z"
		
		for (int counter = 0; counter < allContracts.size(); counter++) { 		      
	          //System.out.println(allContracts.get(counter).toString()); 
	          try {
				json = (JSONObject) parser.parse(allContracts.get(counter));
				//System.out.println(json.toString());
				//System.out.println(json.get("tutorName").toString());
				String tutor = json.get("tutorName").toString();
				
				
				
				if(selectedTutor.equals(tutor)) {
					String contract = json.get("contract").toString();
					//System.out.println(contract);
					ArrayList<String> contractArr = (ArrayList<String>) json.get("contract");
					signedDates.add(contractArr.get(7));
					//System.out.println("Date signed: "+contractArr.get(7));
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
	
	
	private void showLatestContracts(ArrayList<String> latestContractDates) {
		String output = "";
		System.out.println("The second one \n\n\n");
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
						System.out.println("Date finalized: "+ dateFinalized+"Cont Date: "+contDate);
						if(dateFinalized.contains(contDate)) {
							
							String subject = "Subject: "+ contract.get(0);
							String lesson = "Lesson: "+contract.get(1);
							String tutorQualification = "Tutor Qualification: "+contract.get(2);
							String tutorCompetency = "Tutor Competency: "+contract.get(3);
							String weeklySessions = "Weekly Sessions: "+contract.get(4);
							String studyHrs = "Study Hours: "+contract.get(5);
							String rate = "Rate: "+contract.get(6);
							String contractExpiryDate = "Contract Expiry Date: "+contract.get(8);
							String dottedLines = "---------------------------------------------------------------------------------";
							
							output +=tutor +"\n"+ subject + "\n"+lesson+ "\n"+tutorQualification+ "\n"+tutorCompetency+ "\n"+weeklySessions+ "\n"+studyHrs+ "\n"+rate+ "\n"+contractExpiryDate+"\n"+dottedLines+"\n"+"\n";
							
							//System.out.println(output);
							
						}
					}
					//contractField.setText(output);
					
					
				}
			} catch (ParseException e) {
				System.out.println("Error!!!");
				System.out.println(e.getMessage());
				System.out.println(e.getCause());
				e.printStackTrace();
			}
	          
	          
	      }
		System.out.println("End");
		
		contractField.setText(output);
		//pageFrame.add(scrollBar);
		System.out.println(output);
		//String latestContracts = sortDates(signedDates);
		
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
		/**
		allDates.add(formatDate("2021-11-16T15:32:57.558Z"));
		allDates.add(formatDate("2021-12-16T15:32:57.558Z"));
		allDates.add(formatDate("2021-07-16T15:32:57.558Z"));
		allDates.add(formatDate("2021-08-16T15:32:57.558Z"));
		allDates.add(formatDate("2021-10-16T15:32:57.558Z"));
		**/
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
				//output+= sdf.format(allDates.get(i))+ "\n";
				//System.out.println(output);
			}
		}
		
		// less than five contracts, so show all five
		else {
			for (int i=0; i<allDates.size(); i++) {
				splitDates = sdf.format(allDates.get(i)).split(" ");
				output = splitDates[0]+"T"+splitDates[1];
				sortedDates.add(output);
				//output+= sdf.format(allDates.get(i))+ "\n";
				//System.out.println(output);
			}
		}
		return sortedDates;
		
	}
	
	
	
	private Date formatDate(String date) {
		Date output = new Date();
		String removeDots = date.substring(0, (date.length()-5));
		String[] dateSplit = removeDots.split("T");
		String datePart = dateSplit[0]+" "+ dateSplit[1];
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
