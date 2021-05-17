
/**
 * 
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.Vector;

/**
 * a class that is used to represent a student, shows the homepage for student and other options
 */
public class Student implements User, ActionListener {
	private String userName;
	private String givenName;
	private String familyName;
	private GUIcontext context;
	private String userId;
	private ArrayList <String> contractIds= new ArrayList<>();



	//ui components
	private JButton requestTbutton, viewCbutton, ViewBbutton,signContract,viewDetails;
	private JPanel homepagePanel,contractPanel;
	private  JLabel welcome,contractNotif, contractExpAlert;
	private Vector comboBoxItems=new Vector();
	private static JComboBox allContracts;

	//a method to display all unsigned contract of a user
	@Override
	public void signContract() {
		// Creating instance of JFrame
		JFrame frame = new JFrame();
		// Setting the width and height of frame
		frame.setSize(900, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		contractPanel = new JPanel();
		// adding panel to frame
		frame.add(contractPanel);
		contractPanel.setLayout(null);


		JLabel relListTitle = new JLabel("All Contracts Pending Confimation");
		relListTitle.setBounds(10,50,450,25);
		contractPanel.add(relListTitle);


		JLabel instruction = new JLabel("Select a contract and then click on view details");
		instruction.setBounds(10,80,400,25);
		instruction.setForeground(Color.red);
		contractPanel.add(instruction);

		final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
		allContracts = new JComboBox(model);
		allContracts.setBounds(10, 120, 700, 25);
		contractPanel.add(allContracts);



		viewDetails = new JButton("View details");
		viewDetails.setBounds(10, 180, 180, 25);
		viewDetails.addActionListener(this);
		contractPanel.add(viewDetails);

		frame.setVisible(true);



	}

	@Override
	public void create(String uName, String gName, String fName,String uId) {
		this.userName=uName;
		this.givenName=gName;
		this.familyName=fName;
		this.userId=uId;
		checkRequestClosing();
		checkContract();
		viewContractAction.contractNotification(uId);
	}
//a method to show the homepage
	@Override
	public void showHomePage() {

			JFrame homeFrame = new JFrame();
			// Setting the width and height of frame
			homeFrame.setSize(900, 500);
			homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			homepagePanel= new JPanel();
			homeFrame.add(homepagePanel);
			homepagePanel.setBackground(new Color(172, 209, 233));
			homepagePanel.setLayout(null);

			welcome = new JLabel("Welcome:"+userName);
			welcome.setBounds(100,20,400,25);
			homepagePanel.add(welcome);



			requestTbutton = new JButton("Request Tutor");
			requestTbutton.setBounds(100, 100, 600, 25);
			requestTbutton.addActionListener(this);
			homepagePanel.add(requestTbutton);

			viewCbutton = new JButton("View Contracts");
			viewCbutton.setBounds(100, 200, 600, 25);
			viewCbutton.addActionListener(this);
			homepagePanel.add(viewCbutton);

			ViewBbutton = new JButton("View Requests and their Bids");
			ViewBbutton.setBounds(100, 300, 600, 25);
			ViewBbutton.addActionListener(this);
			homepagePanel.add(ViewBbutton);

			signContract = new JButton("Sign Contracts");
			signContract.setBounds(100, 400, 600, 25);
			signContract.addActionListener(this);
			signContract.setEnabled(false);
			homepagePanel.add(signContract);

			//signContract button is only enabled when thers a pending contract
			if (contractIds.size()>0){
				contractNotif = new JLabel("*1 or more Contract creation is in process, please click on Sign Contracts to accecpt contract clause"+userName);
				contractNotif.setBounds(100,70,400,25);
				contractNotif.setForeground(new Color(200,0,200));
				homepagePanel.add(contractNotif);
				signContract.setEnabled(true);
			}
			
			if(viewContractAction.contractNotification(userId)) {
				contractExpAlert = new JLabel("You have contracts expiring in a month");
				contractExpAlert.setBounds(100, 50, 400, 25);
				contractExpAlert.setForeground(new Color(200,0,200));
				homepagePanel.add(contractExpAlert);
			}
			homeFrame.setVisible(true);

	}



	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == requestTbutton){
			//show make request page
			context= new GUIcontext(new createRequestAction(userId));
			context.showUI();
		}
		else if (e.getSource()==viewCbutton){
			//show all contracts page
			context=new GUIcontext(new viewContractAction(userId, "Student"));
			context.showUI();

		}
		else if (e.getSource()==ViewBbutton){
			//show bids of requests made
			context=new GUIcontext(new ViewBidOfferAction(userId));
			context.showUI();

		}
		else if(e.getSource()==signContract){
			//show contracts to be signed
			signContract();
		}
		else if (e.getSource()==viewDetails){
			//show contract details and ask ythem to sign
			context=new GUIcontext(new createContractAction(contractIds.get(allContracts.getSelectedIndex()),"student"));
			context.showUI();

		}

	}
	// a method that checks if the closing date of a bid is passed the one we store in additionalInfo and closes it if it is or chooses a tutor for open bid
	private void checkRequestClosing(){
		HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid?fields=messages", GuiAction.myApiKey);
		try {
			ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
			for (ObjectNode node : userNodes) {
				//check if bid is of type close
				String bidType = node.get("type").asText();
				String initiator= node.get("initiator").get("id").asText();
				String bidId = node.get("id").asText();
				String subId = node.get("subject").get("id").asText();

				//get todays date
				String today = new Date().toInstant().toString();
				SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

				String bidCloseTime = node.get("additionalInfo").get("requestClosesAt").asText();

				Date todayDate = sourceFormat.parse(today);
				Date endDate = sourceFormat.parse(bidCloseTime);
				boolean close=todayDate.after(endDate);

				boolean currentStudentBid=initiator.contains(userId);

				//we need to close all close bids if they have passed their expiry date by this student
				if (currentStudentBid && bidType.contains("close") && node.get("dateClosedDown").toString().equals("null")){
					//check that todays date is  after closing date
					if(todayDate.after(endDate)==true){
						new RequestCloser(1,bidId,GuiAction.myApiKey,new Date().toInstant().toString());
					}
				}
				else if(currentStudentBid && close==true && bidType.contains("open") && node.get("dateClosedDown").toString().equals("null")){
					//select tutor and close request if one or more offers were receive


					if(node.get("messages").isEmpty()==false){
						selectTutor(node.get("messages"),bidId,subId);
					}
				}



			}
		}
		catch(Exception e){
			System.out.println(e.getStackTrace()[0].getLineNumber());
		}
	}
	//a method that checks if there are any unsigned contract for the user and only show them if they have less than 5 signed contract
	private void checkContract(){
		HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("contract", GuiAction.myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
			int countOfSignContract=0;
			for (ObjectNode node: jsonNodes) {
				String firstId=node.get("firstParty").get("id").toString();
				String secondId=node.get("secondParty").get("id").toString();

				if(firstId.contains(userId) || secondId.contains(userId)){
					//check if there are any unsigned contract created by tutor when open bid was done
					if(node.get("dateSigned").toString().equals("null")){
						String contract=node.get("subject").get("name").toString()+" , "+node.get("subject").get("description").toString()+","
								+ " contract between" +node.get("firstParty").get("givenName").toString()+" and "+node.get("secondParty").get("givenName").toString();
						//dateSigned can be null is none of the parties signed it or if one party signed it
						if(node.get("additionalInfo").toString().equals("{}")==false){
							String userType=node.get("additionalInfo").get("firstPartySigned").toString();
							if(userType.contains("student")==false)//means student hasnt already agrred
							{
								//store contractid and details
								String cId=node.get("id").toString();
								int lenCid=cId.length();
								contractIds.add(cId.substring(1, lenCid-1));
								comboBoxItems.add(contract);
							}
						}
						//now add contracts which none of the parties signed
						else{
							//store contractid and details
							String cId=node.get("id").toString();
							int lenCid=cId.length();
							contractIds.add(cId.substring(1, lenCid-1));
							comboBoxItems.add(contract);
						}


					}
					//else if date signed is not null meaning contract is finalissed
					else{
						countOfSignContract+=1;
					}

				}

			}


			//student shouldnt sign more than 5 so remove all the contracts to be signed
			if(countOfSignContract==5){
				contractIds.clear();
			}
		}
		catch(Exception e) {
			System.out.println("Error");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println(e.getStackTrace()[0].getLineNumber());
		}
	}
	//a method to selsct the last tutor of an open bid
	private void selectTutor(JsonNode messages,String bidId,String subId) {
		TreeMap<Date,OpenBidOffer> map=new TreeMap<>();
		for (JsonNode node : messages) {
			try {
				//get info abt offer
				String datePosted = GuiAction.removeQuotations(node.get("datePosted").toString());
				SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Date newDate = sourceFormat.parse(datePosted);
				String tutorId=node.get("poster").get("id").asText();
				String hrsperLesson=node.get("additionalInfo").get("duration").asText();
				String weeklyS=node.get("additionalInfo").get("numberOfSession").asText();
				String comp=node.get("additionalInfo").get("tutorComp").asText();
				String rate=node.get("additionalInfo").get("rate").asText();
				String tutorQ=node.get("additionalInfo").get("tutorQualification").asText();

				OpenBidOffer offer=new OpenBidOffer(userId,tutorId,userName,"");
				offer.setClassInfo(weeklyS,hrsperLesson,rate);
				offer.setExtraInfo("no","");
				offer.setSubjectInfo(subId,"",comp,tutorQ);

				map.put(newDate,offer);
			} catch (Exception e) {
				System.out.println(e.getCause());
			}
		}



		//get the last bid offer
		OpenBidOffer lastTutor=map.lastEntry().getValue();
		//create contract fps or first personed sign value is null becuase none of the parties signed this contract
		// default contract length is 6 months
		String contExpiryDate = GuiAction.getContractExpiryDate("6");
		createContractAction contract= new createContractAction(lastTutor,"",userId,bidId, contExpiryDate);
		if(contract.checkContract()){
			contract.storeContract();
		}
	}
}