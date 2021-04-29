import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class ViewRequestAction implements GuiAction, ActionListener {

    private String userId,fullName;
    private static JComboBox allRequests;
    // list of all students' bid id since it is needed make message
    private ArrayList<String> allStudentBidList = new ArrayList<String>();
    private ArrayList<String> bidType = new ArrayList<String>();

    public JPanel panel;
    private JButton viewDetails;
    private Vector comboBoxItems=new Vector();
    private JLabel warning;

    public ViewRequestAction(String uId,String name){
        userId=uId;
        fullName=name;

    }
    /* Show the tutor all the requests made by students  */
    @Override
    public void show() {
        showAllStudentRequests();
        // Creating instance of JFrame
        JFrame frame = new JFrame("Tutor Homepage");
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);


        // take user inputs over here
        JLabel relListTitle = new JLabel("All requests made by students");
        relListTitle.setBounds(10,50,450,25);
        panel.add(relListTitle);


        JLabel instruction = new JLabel("Select a request and then click on view details");
        instruction.setBounds(10,80,1200,25);
        instruction.setForeground(Color.red);
        panel.add(instruction);

        final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
        allRequests = new JComboBox(model);
        allRequests.setBounds(10, 120, 700, 25);
        panel.add(allRequests);



        viewDetails = new JButton("View details");
        viewDetails.setBounds(10, 180, 180, 25);
        viewDetails.addActionListener(this);
        panel.add(viewDetails);

        warning=new JLabel();
        warning.setBounds(10,240,1200,25);
        panel.add(warning);


        /*
        testBtn = new JButton("Test");
        testBtn.setBounds(10, 280, 80, 25);
        testBtn.addActionListener(this);
        panel.add(testBtn);
        */



        frame.setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewDetails) {
            if (bidType.size()!=0) {
                String bidid = getSelectedRequest();
                String bidTypeOfselected = getSelectedBidType();

                if (bidTypeOfselected.contains("open")) {
                    //create open bid action
                    BidAction b = new OpenBidAction(bidid, userId, fullName);
                } else {
                    //TODO close bid for tutor
                    CloseBidAction c = new CloseBidAction(bidid, userId);
                }
            }
            else{
                warning.setText("there are no active requests available");
                warning.setForeground(Color.red);
            }

        }
    }

    private void showAllStudentRequests(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("user?fields=initiatedBids", myApiKey);
        try {
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            //String output="";
            //allRequests.removeAllItems();
            for (ObjectNode node: jsonNodes) {

                for (JsonNode bidNode : node.get("initiatedBids")) {
                    // show bids that are not closed down
                    if(bidNode.get("dateClosedDown").toString().equals("null") ) {
                        String closeTimeDb = "";
                        String bidCloseTime = "";
                        // this will throw an exception. The requested bid always has additional info, so this exception will not cause any problem
                        if(bidNode.get("additionalInfo").equals(null)) {
                            System.out.println("Additional info is null");
                        }
                        else {
                            try{
                                closeTimeDb = bidNode.get("additionalInfo").get("requestClosesAt").toString();
                                bidCloseTime = GuiAction.removeQuotations(closeTimeDb);
                            }
                            catch(Exception e){System.out.println("requestClosesAt not found");}
//                            System.out.println("heu");
//                            System.out.println(bidNode.get("additionalInfo"));
//                            System.out.println(bidNode.get("additionalInfo").get("requestClosesAt"));

                            //closeTimeDb = bidNode.get("additionalInfo").get("requestClosesAt").toString();
                            //bidCloseTime = APIRequester.removeQuotations(closeTimeDb);
                        }

                        String status = bidNode.get("type").toString();
                        String requester = node.get("userName").toString();
                        String subject = bidNode.get("subject").get("name").toString();
                        String topic = bidNode.get("subject").get("description").toString();
                        String output = "Status: "+status+"    "+"Requested by: "+requester+"    "+ "Subject: "+subject  +"    "+ "Topic: "+ topic+"    " + "Closes at: " + bidCloseTime;
                        // put all available bids in the JCombo Box
                        //make sure only active bids are shown by checking again
                        if (closeTimeDb!=""){
                            String today = new Date().toInstant().toString();
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date todayDate = sourceFormat.parse(today);
                            Date endDate = sourceFormat.parse(bidCloseTime);
                            if (todayDate.after(endDate)==false){
                                comboBoxItems.add(output);
                                String bidId = bidNode.get("id").toString();
                                allStudentBidList.add(bidId);
                                bidType.add(status);
                            }

                            //final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
                            // store all bidIds in allStudentBidList
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println(e.getCause());
            System.out.println(e.getStackTrace()[0].getLineNumber());
        }
    }

    /* Method to get the bid id of the request selected from JComboBox */
    private String getSelectedRequest() {
        // get the index of the selected request
        int selectedRequestPos = allRequests.getSelectedIndex();
        // get the id of the selected request from list
        String bidIdFromList = allStudentBidList.get(selectedRequestPos);
        int bidLength = bidIdFromList.length();
        String selectedBidId = bidIdFromList.substring(1, bidLength-1); // remove additional quotations
        return selectedBidId;
    }

    /* Method to get the bid type of the request selected from JComboBox */
    private String getSelectedBidType() {
        // get the index of the selected request
        int selectedRequestPos = allRequests.getSelectedIndex();
        // get the id of the selected request from list
        String bidT = bidType.get(selectedRequestPos);
        int bidLength = bidT.length();
        String selectedBidType = bidT.substring(1, bidLength-1); // remove additional quotations
        return selectedBidType;
    }
}
