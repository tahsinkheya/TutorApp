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
    private JFrame newFrame,frame;
    private JButton closeBtn, offerView, sendMsg,select,newcloseBtn,confirm;
    private Vector newComboBoxItems = new Vector();
    private JComboBox moreOffers,allRates;
    private JTextArea offerDetails;
    private JLabel warning,newWarning,offerWarning;
    private ArrayList<String> senders = new ArrayList<String>();
    private JTextField field,weeklySeeion,hours,rate;
    private String studentName;
    private String acceptedTutor;


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
                    if (senders.contains(tutorId) == false && tutorId.contains(userId)==false) {
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

        select = new JButton("Select this Tutor and close request");
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
        //close the main frame
        if (e.getSource() == closeBtn) {
            newFrame.setVisible(false);
        }
        //view messages from the selcted sender
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
        //store message
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
        //tutor selction
        else if(e.getSource()==select){
            boolean x=checkContract(userId); //check if student already has 5 ocntracts
            if (x==false){
                newWarning.setText("You already have 5 one-to-one contracts");
            }
            else{
                int index = moreOffers.getSelectedIndex();
                String tutorId = senders.get(index);
                newFrame.setVisible(false);
                getCloseBidOfferDetails(tutorId);
            }
        }
        //close the window for taking info abt the accepted offer
        else if(e.getSource()==newcloseBtn){
            frame.setVisible(false);
        }
        //the student has mentioned all details abt the offer selected
        else if(e.getSource()==confirm){
            String newRate="RM: "+rate.getText()+" "+allRates.getSelectedItem();
            String weeklyS=weeklySeeion.getText();
            String horsPerLson=hours.getText();
            if (newRate.equals("") | weeklyS.equals("") |horsPerLson.equals("")){
                offerWarning.setText("please fill all the fields");
                offerWarning.setForeground(Color.red);
            }
            else{
                createContract(weeklyS,horsPerLson,newRate);
                offerWarning.setText("contract creation in process, waiting for tutor");
                offerWarning.setForeground(Color.BLUE);}

        }

    }

    private void createContract(String weeklySess,String hrsperlsn,String rate){
        //get the subject id from the bid id and find tutor q and tutor competency
        //bid/gashd?fields=
        String subName="";
        String subId="";
        HttpResponse<String> response = GuiAction.initiateWebApiGET("bid/"+bidId, GuiAction.myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
            subName = userNode.get("subject").get("name").asText();
            subId = userNode.get("subject").get("id").asText();}
        catch (Exception e){
        }
        //get tutor competency in the subject
        String endpoint = "user/"+acceptedTutor+"?fields=competencies.subject";
        int tutorcompetencyLevel = 0;
        HttpResponse<String> compResponse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode.class);

            for (JsonNode node : userNode.get("competencies")) {
                // get the subject name that the tutor teaches and compare it to the requested one.
                String nodeSubName = node.get("subject").get("name").toString();
                String tutorSubName = GuiAction.removeQuotations(nodeSubName);
                if(tutorSubName.equals(subName)) {
                    System.out.println("Found the subject for which competency is needed");
                    tutorcompetencyLevel = node.get("level").asInt();
                }
            }
        }
        catch (Exception e){}

        //get tutor qualification
        endpoint = "user?fields=qualifications";
        String tutorQ = "";
        HttpResponse<String> newResonse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
        try{
            ObjectNode[] userNode = new ObjectMapper().readValue(newResonse.body(), ObjectNode[].class);
            for (JsonNode node : userNode) {
                if(node.get("id").toString().contains(acceptedTutor)){
                    for(JsonNode n:node.get("qualifications")){
                        tutorQ+=GuiAction.removeQuotations(n.get("title").toString())+" | ";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tutorQ.equals("")){
            tutorQ="unknown";
        }


        System.out.println(subName);
        System.out.println(tutorcompetencyLevel);
        System.out.println(tutorQ);



        OpenBidOffer offer=new OpenBidOffer(userId,acceptedTutor,subId,subName,Integer.toString(tutorcompetencyLevel),weeklySess,hrsperlsn,rate,"","","","",tutorQ);
        //create contract with first party to sign as student
        createContractAction contract=new createContractAction(offer,"student",userId,bidId);
        contract.storeContract();
    }



    private void getCloseBidOfferDetails(String tutorid){
        acceptedTutor=tutorid;
        //show new frame
        frame = new JFrame("Add Offer Details");
        // Setting the width and height of frame
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel newPanel = new JPanel();
        // adding panel to frame
        frame.add(newPanel);
        newPanel.setLayout(null);
        newPanel.setBackground(new Color(172, 209, 233));

        //add a close button
        newcloseBtn = new JButton("Close");
        newcloseBtn.setBounds(800, 10, 100, 25);
        newcloseBtn.addActionListener(this);
        newPanel.add(newcloseBtn);

        //title
        JLabel actionLabel = new JLabel("Fill in detials about the offer for your contract");
        actionLabel.setBounds(200, 10, 400, 25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        newPanel.add(actionLabel);


        JLabel instruction = new JLabel("please fill all information about the offer you accepted");
        instruction.setBounds(10, 80, 1200, 25);
        instruction.setForeground(Color.RED);
        newPanel.add(instruction);

        JLabel week = new JLabel("Number of weekly sessions");
        week.setBounds(10, 110, 1200, 25);
        newPanel.add(week);

        weeklySeeion=new JTextField(20);
        weeklySeeion.setBounds(10,140,80,25);
        newPanel.add(weeklySeeion);

        JLabel hrsPerLesson = new JLabel("Hours Per Lesson");
        hrsPerLesson.setBounds(10, 170, 1200, 25);
        newPanel.add(hrsPerLesson);

        hours=new JTextField(20);
        hours.setBounds(10,200,80,25);
        newPanel.add(hours);

        JLabel newRate = new JLabel("Rate");
        newRate.setBounds(10, 230, 1200, 25);
        newPanel.add(newRate);

        rate=new JTextField(20);
        rate.setBounds(10,260,80,25);
        newPanel.add(rate);

        String[] rateTypes = {"per hour", "per session"};
        allRates = new JComboBox(rateTypes);
        allRates.setBounds(110, 260, 100, 25);
        newPanel.add(allRates);

        //add a close button
        confirm = new JButton("Confirm");
        confirm.setBounds(10, 310, 100, 25);
        confirm.addActionListener(this);
        newPanel.add(confirm);

        offerWarning = new JLabel("");
        offerWarning.setBounds(10, 340, 1200, 25);
        newPanel.add(offerWarning);

        frame.setVisible(true);


    }

    private boolean checkContract(String studentId){
        boolean retVal=false;
        int count=0;
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("contract", GuiAction.myApiKey);
        try {
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                String firstParty=node.get("firstParty").toString();
                String secondParty=node.get("secondParty").toString();
                if (node.get("dateSigned").isNull()==false) {
                    if (firstParty.contains(studentId) | secondParty.contains(studentId)) {
                        count += 1;
                    }
                }

            }
        }
        catch(Exception e){

        }
        System.out.println(count);
        if (count<5){
            retVal=true;
        }
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
