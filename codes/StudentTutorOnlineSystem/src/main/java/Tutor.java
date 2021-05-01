import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Vector;

public class Tutor implements  User, ActionListener {
    String userName;
    String givenName;
    String familyName;
    String userId;

    private GUIcontext context;

    private JPanel homepage;
    private JLabel welcome;
    private JButton viewContract,viewRequest,viewMessage,signContract,viewDetails,closeBtn; 	//Btn
    private Vector comboBoxItems=new Vector();
    private ArrayList<String> contractIds= new ArrayList<>();
    private JLabel contractNotif;
    private  JComboBox allContracts;
    private JFrame frame;




    @Override
    public void signContract() {
        // Creating instance of JFrame
       frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




        JPanel contractPanel = new JPanel();
        // adding panel to frame
        frame.add(contractPanel);
        contractPanel.setLayout(null);
        contractPanel.setBackground(new Color(172, 209, 233));


        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        contractPanel.add(closeBtn);

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
    public void create(String uName, String gName,String fName,String uId) {
        this.userName=uName;
        this.givenName=gName;
        this.familyName=fName;
        this.userId=uId;
        checkContract();
    }

    @Override
    public void showHomePage() {
        JFrame homeFrame = new JFrame();
        // Setting the width and height of frame
        homeFrame.setSize(900, 500);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homepage= new JPanel();
        homeFrame.add(homepage);
        homepage.setBackground(new Color(172, 209, 233));
        homepage.setLayout(null);

        welcome = new JLabel("Welcome:"+userName);
        welcome.setBounds(100,50,400,25);
        homepage.add(welcome);

        viewRequest = new JButton("View Student Requests");
        viewRequest.setBounds(100, 100, 600, 25);
        viewRequest.addActionListener(this);
        homepage.add(viewRequest);

        viewContract = new JButton("View Contracts");
        viewContract.setBounds(100, 200, 600, 25);
        viewContract.addActionListener(this);
        homepage.add(viewContract);

        viewMessage = new JButton("View Messages from Close bids");
        viewMessage.setBounds(100, 300, 600, 25);
        viewMessage.addActionListener(this);
        homepage.add(viewMessage);

        //this button is disabled until theres a contract pending signature from this tutor
        signContract = new JButton("Sign Contracts");
        signContract.setBounds(100, 400, 600, 25);
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

        homeFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==viewContract){
            context=new GUIcontext(new viewContractAction(userId, "Tutor"));
            context.showUI();
        }
        else if (e.getSource()==viewRequest){
            //
            context= new GUIcontext(new ViewRequestAction(userId,givenName+" "+familyName));
            context.showUI();
            //TutorGUI t=new TutorGUI();
        }
        else if (e.getSource()==viewMessage){
            context=new GUIcontext(new ViewMessagesToTutorAction(userId));
            context.showUI();

        }
        //sign contract button
        else if(e.getSource()==signContract){
            signContract();
        }
        //close signContract window
        else if(e.getSource()==closeBtn){
            frame.setVisible(false);
        }
        //view details of the contract selected
        else if (e.getSource()==viewDetails){
            context=new GUIcontext(new createContractAction(contractIds.get(allContracts.getSelectedIndex()),"tutor"));
            context.showUI();

        }

    }

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
                        String contract=node.get("subject").get("name").toString()+" , "+node.get("subject").get("description").toString()+","
                                + " contract between" +node.get("firstParty").get("givenName").toString()+" and "+node.get("secondParty").get("givenName").toString();
                        //dateSigned can be null is none of the parties signed it or if one party signed it
                        if(node.get("additionalInfo").toString().equals("{}")==false){
                            String userType=node.get("additionalInfo").get("firstPartySigned").toString();
                            if(userType.contains("tutor")==false)//means tutor hasnt already agrred
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

