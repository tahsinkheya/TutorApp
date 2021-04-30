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
import java.util.*;

public class ViewMessages implements ActionListener {
    private String bidId, userId;
    private JFrame newFrame;
    private JButton closeBtn, offerView, sendMsg,select;
    private Vector newComboBoxItems = new Vector();
    private JComboBox moreOffers;
    private JTextArea offerDetails;
    private JLabel warning,newWarning;
    private ArrayList<String> senders = new ArrayList<String>();
    private JTextField field;
    private String studentName;


    public ViewMessages(String bidid, String userid) {
        bidId = bidid;
        userId = userid;

    }

    /*method to get all tutor who sent messages*/
    private void getAllSenders() {
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("message", GuiAction.myApiKey);
        try {
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                String bid = node.get("bidId").toString();
                if (bid.contains(bidId)) {
                    String username = node.get("poster").get("userName").asText();
                    String tutorId = node.get("poster").get("id").asText();
                    if (senders.contains(tutorId) == false) {
                        senders.add(tutorId);
                        newComboBoxItems.add("from: " + username);

                    }
                }
            }

        } catch (Exception e) {

        }
    }

    void showSenders() {
        getAllSenders();

        //show new frame
        newFrame = new JFrame("View Offer Details");
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
        JLabel actionLabel = new JLabel("View Offer Messages");
        actionLabel.setBounds(350, 10, 300, 25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select an offer and then click on view messages");
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

        JLabel text=new JLabel("if you want to send a message to this tutor please write a message below and click Send");
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

        select = new JButton("Select tthis Tutor and close request");
        select.setBounds(10, 570, 300, 25);
        select.addActionListener(this);
        //this will be enabled after selecting a tutor
        select.setEnabled(false);
        panel.add(select);

        newWarning = new JLabel();
        newWarning.setBounds(10, 600, 700, 25);
        panel.add(newWarning);
        newFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            newFrame.setVisible(false);

        }
        else if (e.getSource() == offerView) {
            if (senders.size() == 0) {
                //no messages to show
                warning.setText("no tutor has sent any messages for this bid");
            } else {
                sendMsg.setEnabled(true);
                select.setEnabled(true);
                int index = moreOffers.getSelectedIndex();
                String tutorId = senders.get(index);
                viewMessages(tutorId, userId, bidId);
            }
        }
        else if(e.getSource()==sendMsg){
            String msg=field.getText();
            if (msg.equals("")){
                warning.setText("please enter the message you want to send");
                warning.setForeground(Color.RED);
            }
            else{
                int index = moreOffers.getSelectedIndex();
                String tutorId = senders.get(index);

                String jsonString = null;
                // create the message object
                JSONObject msgInfo=new JSONObject();
                msgInfo.put("bidId", bidId);
                msgInfo.put("posterId", userId);
                msgInfo.put("datePosted", new Date().toInstant().toString());
                msgInfo.put("content", studentName+": "+msg);
                JSONObject additionalInfo=new JSONObject();
                additionalInfo.put("tutorId",tutorId);
                msgInfo.put("additionalInfo", additionalInfo);

                // convert message to JSON string
                jsonString = msgInfo.toString();
                HttpResponse<String> postResponse = GuiAction.updateWebApi("message",GuiAction.myApiKey,jsonString);
                newWarning.setText("message sent!");

            }
        }
        else if(e.getSource()==select){
            boolean x=checkContract(userId);
        }

    }

    private boolean checkContract(String userId){
        boolean retVal=false;

        return retVal;

    }
    private void viewMessages(String tutorId, String studentId, String bidid){
        TreeMap<Date,String> treeMap=getMessages(tutorId,studentId,bidid);
        Iterator itr=treeMap.values().iterator();
        String output="";
        //iterate through TreeMap values iterator
        while(itr.hasNext())
            output+=itr.next()+"\n";
        offerDetails.setText(output);

    }

    public TreeMap<Date, String> getMessages(String tutorId, String studentId, String bidid) {
        TreeMap treeMap = new TreeMap<Date, String>();
        //bid/jdgasjdh?fields=messages
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid/"+bidid+"?fields=messages", GuiAction.myApiKey);
        try {
            System.out.println(userResponse.statusCode());
            ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
            String fname=GuiAction.removeQuotations(userNode.get("initiator").get("familyName").toString());
            String gname=GuiAction.removeQuotations(userNode.get("initiator").get("givenName").toString());
            studentName=gname+" "+fname;
            for (JsonNode msgNode : userNode.get("messages")) {
                //convert the srting date the message was posted to date
                String msg=GuiAction.removeQuotations(msgNode.get("content").toString());
                String datePosted=GuiAction.removeQuotations(msgNode.get("datePosted").toString());
                SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date newDate = sourceFormat.parse(datePosted);
                //we want the messages send by the tutor
                if (msgNode.get("poster").get("id").toString().contains(tutorId)){
                    treeMap.put(newDate,msg);
                }
                //we want what the student sent to this tutor-- additional info of message contains the id of the tutor the message is sent to
                else if (msgNode.get("poster").get("id").toString().contains(studentId) && msgNode.get("additionalInfo").toString().equals("{}")==false){
                    String messagedTutor=msgNode.get("additionalInfo").get("tutorId").toString();
                    if (messagedTutor.contains(tutorId)){
                        String message=GuiAction.removeQuotations(msgNode.get("content").toString());
                        treeMap.put(newDate,message);
                    }
                }
            }

        } catch (Exception e) {
        }

        return treeMap;
    }
}
