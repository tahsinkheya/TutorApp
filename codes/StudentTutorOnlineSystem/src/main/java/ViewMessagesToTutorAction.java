import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.*;
/*
* a class to show messages to the tutor abt close bid*/
public class ViewMessagesToTutorAction implements GuiAction , ActionListener {
    private String tutorId;
    private JFrame newFrame;
    private JButton closeBtn,offerView,sendMsg;

    private Vector newComboBoxItems = new Vector();
    private JComboBox moreOffers;
    private JTextArea offerDetails;
    private JTextField field;
    private JLabel warning,newWarning;
    private ArrayList<String> bids = new ArrayList<String>();
    private ArrayList<String> students = new ArrayList<String>();
    private ArrayList<String> tutorNames = new ArrayList<String>();


    public ViewMessagesToTutorAction(String userId) {
        tutorId=userId;
    }

    @Override
    public void show() {
        //show new frame
        getAllbidMessage();
        newFrame = new JFrame("View Bid Messages");
        // Setting the width and height of frame
        newFrame.setSize(900, 700);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        // adding panel to frame
        newFrame.add(panel);
        panel.setLayout(null);
        panel.setBackground(new Color(172, 209, 233));

        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        panel.add(closeBtn);

        //title
        JLabel actionLabel = new JLabel("View Close Bid Messages");
        actionLabel.setBounds(350, 10, 300, 25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select an bid request then click on view messages");
        instruction.setBounds(10, 80, 1200, 25);
        panel.add(instruction);


        final DefaultComboBoxModel model = new DefaultComboBoxModel(newComboBoxItems);

        moreOffers = new JComboBox(model);
        moreOffers.setBounds(10, 120, 700, 25);
        panel.add(moreOffers);


        offerView = new JButton("View details");
        offerView.setBounds(10, 180, 180, 25);
        offerView.addActionListener(this);
        panel.add(offerView);

        offerDetails = new JTextArea();
        offerDetails.setBounds(50, 240, 700, 230);
        offerDetails.setEditable(false);
        panel.add(offerDetails);


        warning = new JLabel();
        warning.setBounds(10, 210, 700, 25);
        panel.add(warning);

        JLabel text=new JLabel("if you want to send a message to this student please write a message below and click Send");
        text.setBounds(10, 480, 700, 25);
        panel.add(text);

        field=new JTextField(100);
        field.setBounds(10,510,700,25);
        panel.add(field);

        sendMsg = new JButton("Send");
        sendMsg.setBounds(10, 540, 300, 25);
        sendMsg.addActionListener(this);
        //this will be enabled after selecting a tutor
        sendMsg.setEnabled(false);
        panel.add(sendMsg);

        newWarning = new JLabel();
        newWarning.setBounds(10, 570, 700, 25);
        panel.add(newWarning);
        newFrame.setVisible(true);

    }
//a method to get all the messages connected to a bid if its form the student or this tutor
    private void getAllbidMessage(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid?fields=messages", myApiKey);
        try {
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            System.out.println(userResponse.statusCode());
            for (ObjectNode node : userNodes) {
                //check if bud is of type close
                String bidType=node.get("type").asText();
                if (bidType.contains("close")){
                    for (JsonNode msgNode : node.get("messages")) {
                        String tutor = msgNode.get("poster").get("id").asText();
                        // if senders and this tutors id is same then we are gonna add this bid as an option
                        if (tutor.contains(tutorId)){
                            String newBidId=node.get("id").asText();
                            if (bids.contains(newBidId)==false){
                                String studentId=node.get("initiator").get("id").asText();
                                students.add(studentId);
                                bids.add(newBidId);
                                String fullname=GuiAction.removeQuotations(msgNode.get("poster").get("givenName").toString())+" "+GuiAction.removeQuotations(msgNode.get("poster").get("familyName").toString());
                                tutorNames.add(fullname);
                                String studentusername=node.get("initiator").get("userName").asText();
                                String subject=node.get("subject").get("name").asText();
                                String desc=node.get("subject").get("description").asText();
                                newComboBoxItems.add("subject: "+subject+" "+desc+" by "+studentusername);
                            }

                        }
                    }
                }

            }

        }catch(Exception e){}

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==closeBtn){
            newFrame.setVisible(false);
        }
        else if (e.getSource()==offerView){
            if (bids.size()==0){
                warning.setText("you dont have any messages to view");
            }
            else{
                sendMsg.setEnabled(true);
                int index=moreOffers.getSelectedIndex();
                String bidId=bids.get(index);
                String studentId=students.get(index);
                ViewMessages v= new ViewMessages(bidId,tutorId);
                TreeMap<Date,String> tree=v.getMessages(tutorId,studentId,bidId);
                Iterator itr=tree.values().iterator();
                String output="";
                //iterate through TreeMap values iterator
                while(itr.hasNext())
                    output+=itr.next()+"\n";
                offerDetails.setText(output);
                }
        }
        else if(e.getSource()==sendMsg){
            String msg=field.getText();
            int index=moreOffers.getSelectedIndex();
            String bidId=bids.get(index);
            String studentId=students.get(index);
            if (msg.equals("")){
                newWarning.setText("please enter the text you want to send");
            }
            else{
                storeMessage(msg,bidId,tutorId,tutorNames.get(index),studentId);
            }
        }

    }
// a method to store messages in the database
    private void storeMessage(String msg,String bidId,String userId,String userFullName,String studentId){
        String jsonString = null;
        // create the message object
        JSONObject msgInfo=new JSONObject();
        msgInfo.put("bidId", bidId);
        msgInfo.put("posterId", userId);
        msgInfo.put("datePosted", new Date().toInstant().toString());
        msgInfo.put("content", userFullName+": "+msg);
        JSONObject additionalInfo=new JSONObject();
        additionalInfo.put("to",studentId);
        msgInfo.put("additionalInfo", additionalInfo);

        // convert message to JSON string
        jsonString = msgInfo.toString();
        HttpResponse<String> userResponse=GuiAction.updateWebApi("message",myApiKey,jsonString);
        System.out.println(userResponse.statusCode());
        warning.setText("your response has been saved successfully");
    }
}
