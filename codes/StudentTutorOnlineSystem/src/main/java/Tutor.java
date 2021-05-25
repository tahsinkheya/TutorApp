import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Vector;
/*class that represents a tutor*/
public class Tutor implements  User, ActionListener {
    private String userName;
    private String givenName;
    private String familyName;
    private String userId;

    private GUIcontext context;

    private JPanel homepage;
    private JLabel welcome;
    private JButton viewContract,viewRequest,viewMessage,signContract,dashboard; 	//Btn
    private Vector comboBoxItems=new Vector();
    private ArrayList<String> contractIds= new ArrayList<>();
    private JLabel contractNotif, contractExpAlert;



    //a method to display all unsigned contract of a user
    @Override
    public void signContract() {
        context=new GUIcontext(new ViewContractsToSignAction("tutor",contractIds,comboBoxItems));
        context.showUI();

    }
    @Override
    public void create(String uName, String gName,String fName,String uId) {
        this.userName=uName;
        this.givenName=gName;
        this.familyName=fName;
        this.userId=uId;
        checkContract();
    }
    //a method to show the homepage
    @Override
    public void showHomePage() {
        JFrame homeFrame = new JFrame();
        // Setting the width and height of frame
        homeFrame.setSize(900, 600);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homepage= new JPanel();
        homeFrame.add(homepage);
        homepage.setBackground(new Color(172, 209, 233));
        homepage.setLayout(null);

        welcome = new JLabel("Welcome:"+userName);
        welcome.setBounds(100,20,400,25);
        homepage.add(welcome);

        viewRequest = new JButton("View Student Requests");
        viewRequest.setBounds(100, 100, 600, 25);
        viewRequest.addActionListener(this);
        homepage.add(viewRequest);

        viewContract = new JButton("View Contracts");
        viewContract.setBounds(100, 400, 600, 25);
        viewContract.addActionListener(this);
        homepage.add(viewContract);

        viewMessage = new JButton("View Messages from Close bids");
        viewMessage.setBounds(100, 300, 600, 25);
        viewMessage.addActionListener(this);
        homepage.add(viewMessage);

        dashboard = new JButton("My Monitoring Dashboard");
        dashboard.setBounds(100, 200, 600, 25);
        dashboard.addActionListener(this);
        homepage.add(dashboard);

        //this button is disabled until theres a contract pending signature from this tutor
        signContract = new JButton("Sign Contracts");
        signContract.setBounds(100, 500, 600, 25);
        signContract.addActionListener(this);
        signContract.setEnabled(false);
        homepage.add(signContract);


        if (contractIds.size()>0){
            contractNotif = new JLabel("*1 or more Contract creation is in process, please click on Sign Contracts to accecpt contract clause"+userName);
            contractNotif.setBounds(100,70,500,25);
            contractNotif.setForeground(new Color(200,0,200));
            homepage.add(contractNotif);
            signContract.setEnabled(true);
        }
        
        if(viewContractAction.contractNotification(userId)) {
			contractExpAlert = new JLabel("You have contracts expiring in a month");
			contractExpAlert.setBounds(100, 50, 400, 25);
			contractExpAlert.setForeground(new Color(200,0,200));
			homepage.add(contractExpAlert);
		}

        homeFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==viewContract){
            //show contracts
            context=new GUIcontext(new viewContractAction(userId, "Tutor"));
            context.showUI();
        }
        else if (e.getSource()==viewRequest){
            //shoe student requets
            context= new GUIcontext(new ViewRequestAction(userId,givenName+" "+familyName));
            context.showUI();
        }
        else if (e.getSource()==viewMessage){
            //show messages from close bid
            context=new GUIcontext(new ViewMessagesToTutorAction(userId));
            context.showUI();

        }
        //sign contract button
        else if(e.getSource()==signContract){
            signContract();
        }
        else if(e.getSource()==dashboard){
            //create controller and pass in view
            new Controller(new DashboardView(userId),userId);
        }

    }
    //a method that checks if there are any unsigned contract for the user
    private void checkContract(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("contract", GuiAction.myApiKey);
        try {
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);

            for (ObjectNode node: jsonNodes) {
                String firstId=node.get("firstParty").get("id").toString();
                String secondId=node.get("secondParty").get("id").toString();

                if(firstId.contains(userId) || secondId.contains(userId)){
                    //check if there are any unsigned contract created by tutor when open bid was done
                    if(node.get("dateSigned").toString().equals("null")){
                        getAllContractDets(node);
                    }

                }

            }
        }
        catch(Exception e) { }
    }
    //a method to get all contract details and store it in an arraylist
    private void getAllContractDets(JsonNode node){
        String contract=node.get("subject").get("name").toString()+" , "+node.get("subject").get("description").toString()+","
                + " contract between" +node.get("firstParty").get("givenName").toString()+" and "+node.get("secondParty").get("givenName").toString();
        //dateSigned can be null is none of the parties signed it or if one party signed it
        if(node.get("additionalInfo").toString().equals("{}")==false){
            String userType=node.get("additionalInfo").get("firstPartySigned").toString();
            if(userType.contains("tutor")==false)//means tutor hasnt already agrred
            {//store contractid and details
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

}

