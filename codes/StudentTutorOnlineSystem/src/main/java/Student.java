
/**
 * 
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * @author Rafaquat
 *
 */
public class Student implements User, ActionListener {
	private static StudentGUI studentGUI= new StudentGUI();
	private String userName;
	private String givenName;
	private String familyName;
	private GUIcontext context;
	String userId;
	private ArrayList <String> contractIds= new ArrayList<>();



	//ui components
	private JButton requestTbutton, viewCbutton, ViewBbutton,signContract;
	private JPanel homepagePanel,contractPanel;
	private  JLabel welcome,contractNotif;



	//StudentGUI studentGUI;
	

	@Override
	public boolean signContract() {
		ArrayList <String> output=new ArrayList<>();
		ArrayList<JsonNode> contractDetails=new ArrayList<>();
		for (String c: contractIds) {
			String endpoint = "contract/"+c;
			HttpResponse<String> response = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
			try {
				ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
				String contract=userNode.get("subject").get("name").toString()+" , "+userNode.get("subject").get("description").toString()+
						" , "+userNode.get("additionalInfo").get("tutorName").toString();
				output.add(contract);
				contractDetails.add(userNode.get("addtionalInfo"));


			} catch (Exception e) {
				System.out.println("Error!!!");
				System.out.println(e.getCause());
			}
		}

		// take user inputs over here
		JLabel relListTitle = new JLabel("All Contracts Pending Confimation");
		relListTitle.setBounds(10,50,450,25);
		contractPanel.add(relListTitle);


		JLabel instruction = new JLabel("Select a contract and then click on view details");
		instruction.setBounds(10,80,1200,25);
		instruction.setForeground(Color.red);
		contractPanel.add(instruction);


		return false;

	}

	@Override
	public void create(String uName, String gName, String fName,String uId) {
		this.userName=uName;
		this.givenName=gName;
		this.familyName=fName;
		this.userId=uId;
		checkContract();
		System.out.println("len of clist"+contractIds.size());

	}

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
			welcome.setBounds(100,50,400,25);
			homepagePanel.add(welcome);

			if (contractIds.size()>0){
				contractNotif = new JLabel("*1 or more Contract creation is in process, please click on Sign Contracts to accecpt contract clause"+userName);
				contractNotif.setBounds(100,70,400,25);
				contractNotif.setForeground(new Color(200,0,200));
				homepagePanel.add(contractNotif);
			}

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
			homepagePanel.add(signContract);

			homeFrame.setVisible(true);

	}

	static void showAllRequests(){
		studentGUI.showAllRequests();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == requestTbutton){
			//show make request page
			context= new GUIcontext(new createRequestAction(userId,givenName,familyName));
			context.showUI();
		}
		else if (e.getSource()==viewCbutton){
			//show all contracts page
			//context=new GUIcontext(new createContractAction())

		}
		else if (e.getSource()==ViewBbutton){
			//show bids of requests made
			System.out.println("3");

		}
		else if(e.getSource()==signContract){
			//show contracts to be signed
			signContract();
		}

	}

	private void checkContract(){

		System.out.println(contractIds.size());
		HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("contract", GuiAction.myApiKey);
		try {
			ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);

			for (ObjectNode node: jsonNodes) {
				String firstId=node.get("firstParty").get("id").toString();
				String secondId=node.get("secondParty").get("id").toString();


				if(firstId.contains(userId) || secondId.contains(userId)){
					//check if there are any unsigned contract created by tutor when open bid was done
					if(node.get("dateSigned").toString().equals("null")){
						String cId=node.get("id").toString();
						int lenCid=cId.length();
						contractIds.add(cId.substring(1, lenCid-1));
					}
				}

			}
		}
		catch(Exception e) {
			System.out.println("Error");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println(e.getStackTrace()[0].getLineNumber());
		}
	}
}