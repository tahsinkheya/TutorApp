import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ViewBidOfferAction implements GuiAction, ActionListener {
    private JFrame frame;
    private static JComboBox allOffers;
    private Vector comboBoxItems=new Vector();
    private JButton viewDetails;
    private String userId;
    public ViewBidOfferAction(String userid){
        userId=userid;

    }

    @Override
    public void show() {
        showAllOffers();
        frame = new JFrame("View Bid Offers");
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);

        //title
        JLabel actionLabel = new JLabel("All Bid Offers");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select a bid offer and then click on view details");
        instruction.setBounds(10,80,1200,25);
        instruction.setForeground(Color.red);
        panel.add(instruction);

        final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
        allOffers = new JComboBox(model);
        allOffers.setBounds(10, 120, 700, 25);
        panel.add(allOffers);



        viewDetails = new JButton("View details");
        viewDetails.setBounds(10, 180, 180, 25);
        viewDetails.addActionListener(this);
        panel.add(viewDetails);

        frame.setVisible(true);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==viewDetails){

        }
    }

    private void showAllOffers(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid?fields=", myApiKey);
        try{
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                // process the initiator id to remove extra quotations
                String initId = node.get("initiator").get("id").toString();
                String initiatorId = GuiAction.removeQuotations(initId);

                //an extra check to make sure that the bid is actually active
                //get todays date
                String today = new Date().toInstant().toString();
                SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                //get closing date from additionalinfo and remove "" only if its null
                if (node.get("additionalInfo").toString().equals("{}")==false) {
                    String closeTimeDb = node.get("additionalInfo").get("requestClosesAt").toString();
                    String bidCloseTime = GuiAction.removeQuotations(closeTimeDb);
                    Date todayDate = sourceFormat.parse(today);
                    Date endDate = sourceFormat.parse(bidCloseTime);

                    if (initiatorId.contains(userId) && node.get("dateClosedDown").toString().equals("null") && todayDate.after(endDate) == false) {
                        String subjectName = node.get("subject").get("name").toString();
                        String desc = node.get("subject").get("description").toString();
                        String bidT = node.get("type").toString();
                        String output = "Subject: "+subjectName  +"    "+ "Topic: "+ desc+"    "+"bid type "+bidT+" closing on:" +
                                endDate;

                        comboBoxItems.add(output);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    private void showAllRequests() {
//        // get all the bids with messages
//        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid?fields=messages", myApiKey);
//        try {
//            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
//            for (ObjectNode node : userNodes) {
//                // process the initiator id to remove extra quotations
//                String initId = node.get("initiator").get("id").toString();
//                String initiatorId = APIRequester.removeQuotations(initId);
//
//                // find requests made by student by comparing userId and initiatorId
//                if(initiatorId.equals(userId) & node.get("dateClosedDown").toString().equals("null") ) {
//                    System.out.println("Found bid made by student");
//                    String bidId = APIRequester.removeQuotations(node.get("id").toString());
//                    System.out.println("The bid id is: " + bidId);
//
//                    String closeTime = node.get("additionalInfo").get("requestClosesAt").toString();
//                    String subjectName = node.get("subject").get("name").toString();
//                    String desc = node.get("subject").get("description").toString();
//
//                    // each bid can have multiple messages so loop
//                    String msg = "";
//                    String msgSender="";
//                    for (JsonNode msgNode : node.get("messages")) {
//                        msg = msgNode.get("content").toString();
//                        msgSender = msgNode.get("poster").get("userName").toString();
//                        // update the jcombo box as more tutors reply
//                        output = "Subject: "+subjectName  +"    "+ "Topic: "+ desc+"    "+"Bid: "+ msg+"    "+ "From: "+ msgSender;
//                        allRequests.addItem(output);
//                    }
//
//                    studentRequest = "Subject: "+subjectName  +"    "+ "Topic: "+ desc;
//                    if(bidCreated) {
//                        closeBid(bidId, closeTime);
//                        bidCreated = false;
//                    }
//
//                    requestMade.setText("Your Request: "+ studentRequest);
//                    requestStatus.setText("Tutor request will remain open for the next 30 minutes");
//
//                }
//            }
//        }
//        catch(Exception e) {
//            System.out.println(e.getCause());
//        }
//    }

}
