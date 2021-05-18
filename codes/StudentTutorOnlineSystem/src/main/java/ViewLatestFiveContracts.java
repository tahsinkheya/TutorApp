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
import javax.swing.JTextArea;

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
	
	public ViewLatestFiveContracts(String id) {
		userId = id;
	}
	
	@Override
	public void show() {
		JFrame pageFrame = new JFrame("Latest 5 contracts with a tutor");
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
        contractField.setBounds(10, 150, 450, 300);
		panel.add(contractField);
        
		pageFrame.add(panel);
		pageFrame.setVisible(true);
		
		
		showTutorsWithContracts();
		
		//showLatestContracts();
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showContracts){
			//show make request page
			showLatestContracts();
			System.out.println("Show latest 5 contracts");
		}
	}
	
	
	private void showTutorsWithContracts() {
		allContractInfo=new JSONObject();
		allContracts = new ArrayList<String>();
		ArrayList<String> tutorTracker = new ArrayList<String>();
		HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract", myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(contResponse.body(), ObjectNode[].class);
		
			for (ObjectNode node: jsonNodes) {
				// get the signed date for contract to check if it is null or not
				String dateSign = node.get("dateSigned").toString();	
				
				// not null means contract has been finalized
				if (!dateSign.equals("null")) {
					//System.out.println("Inside");
					ArrayList<String> userFullNames = viewContractAction.getStudentAndTutorNames(node);
					ArrayList<String> contractDetails = viewContractAction.getcontractDetails(node, userId);
					String tutorFullName = userFullNames.get(1);
					getLatestContracts(contractDetails, tutorFullName);
					
					if(!tutorTracker.contains(tutorFullName)) {
						tutorTracker.add(tutorFullName);
						comboBoxItems.add(tutorFullName);				
					
						//System.out.println(tutorFullName);
					}
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
	
	
	private void getLatestContracts(ArrayList<String> contractDetails, String tutorName) {
		
        // create the additional info
		if(contractDetails.size() >0 ) {
			allContractInfo.put("tutorName", tutorName);
			allContractInfo.put("contract", contractDetails);
			//allContracts.add(allContractInfo);
	        String jsonString = allContractInfo.toString(); // convert to string
	        allContracts.add(jsonString);
			//System.out.println(jsonString);
			//System.out.println(allContracts.toString());
		}
		
	}
	
	private void showLatestContracts() {
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
				//System.out.println(json.get("tutorName").toString());
				String tutor = json.get("tutorName").toString();
				if(selectedTutor.equals(tutor)) {
					String contract = json.get("contract").toString();
					System.out.println(contract);
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
		sortDates(signedDates);
		
	}
	
	/** Method to sort the dates from past to near-present/present time**/
	private void sortDates(ArrayList<String> signedDates) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		ArrayList<Date> allDates=new ArrayList<>();
		
		for(String date : signedDates) {
			allDates.add(formatDate(date));
			
		}
		Collections.sort(allDates); 
		for (int i=0; i<allDates.size(); i++) {
			System.out.println(allDates.get(i));
		}
		
	}
	
	
	private Date formatDate(String date) {
		Date output = new Date();
		String removeDots = date.substring(0, (date.length()-5));
		String[] dateSplit = removeDots.split("T");
		String datePart = dateSplit[0]+" "+ dateSplit[1];
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
