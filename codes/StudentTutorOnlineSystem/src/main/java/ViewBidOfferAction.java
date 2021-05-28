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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;


/* a class to display offers of bid request made by student to student
* */
public class ViewBidOfferAction implements GuiAction, ActionListener {
    private JFrame frame,newFrame;
    private static JComboBox allOffers;
    private static JComboBox moreOffers;
    private Vector comboBoxItems=new Vector();
    private Vector newComboBoxItems=new Vector();
    private JButton viewDetails,offerView,closeBid,closeBtn,close;
    private String userId;
    private ArrayList<String> bidType = new ArrayList<String>();
    private ArrayList<String> bidIds = new ArrayList<String>();
    private ArrayList<OpenBidOffer> offerInfo = new ArrayList<OpenBidOffer>();
    private ArrayList<String> tutorids = new ArrayList<String>();
    private JLabel warning,newWarning, contWarning;
    private JTextArea offerDetails;
    private JPanel panel;
    private String bidid;
    private JTextField contDurationInput;

    public ViewBidOfferAction(String userid){
        userId=userid;

    }

    @Override
    public void show() {
        showAllRequests();
        frame = new JFrame("View Bid Offers");
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);
        panel.setBackground(new Color(172, 209, 233));

        //add a close buttond
        //add a close button
        close = new JButton("Close");
        close.setBounds(800, 10, 100, 25);
        close.addActionListener(this);
        panel.add(close);

        //title
        JLabel actionLabel = new JLabel("All Requests You Made");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select a request and then click on view details to view offers");
        instruction.setBounds(10,80,1200,25);
        panel.add(instruction);

        final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
        allOffers = new JComboBox(model);
        allOffers.setBounds(10, 120, 700, 25);
        panel.add(allOffers);



        viewDetails = new JButton("View details");
        viewDetails.setBounds(10, 180, 180, 25);
        viewDetails.addActionListener(this);
        panel.add(viewDetails);

        warning=new JLabel();
        warning.setBounds(10,220,200,25);
        panel.add(warning);

        frame.setVisible(true);


    }
    // a method that is called when viewdetails button is clicked to show details
    private void showAllBidViewDetails(){
        if (bidType.size()==0){
            warning.setText("there are no offers for this bid");
            warning.setForeground(Color.RED);
        }
        else{
            int index=allOffers.getSelectedIndex();
            String bidStatus=bidType.get(index);
            String bidid=bidIds.get(index);
            //show bids made
            if (bidStatus.contains("open")){
                //show open bid offers
                showOpenBidOffers(bidid);
            }
            else{
                //shiow close bid offer
                ViewMessages v=new ViewMessages(bidid,userId);
                v.showSenders();

            }
        }
    }
    //a method to display details of an open bid offer made by a tutor
    private void showOpenBidOfferDetails(){
        if (offerInfo.size()==0){
            newWarning.setText("no offers have been made");
        }
        else {
            int index=moreOffers.getSelectedIndex();

            OpenBidOffer dets=offerInfo.get(index);

            String output="Duration Offered:"+dets.getHoursPerLesson()+" hrs per lesson"+"\n"+
                    "Rate:"+dets.getRate()+"\n"+"Number of weekly session:"+dets.getWeeklySession()+"\n"+
                    "a fress lesson was offered:"+dets.getFressLesson()+"\n"+
                    "extra information from the tutor:"+dets.getExtraInfo()+"\n"+
                    "Tutor competency level for this subject:"+dets.getCompetency();
            closeBid.setEnabled(true);
            offerDetails.setText(output);
            offerDetails.setForeground(Color.blue);

        }
    }
    //a method to choose tutor and close a bid and create a contract
    private void closeOpenBid(){
        int index=moreOffers.getSelectedIndex();
        OpenBidOffer dets=offerInfo.get(index);
        //create contract and set student signed to true since they selected the tutor
        String contExpiryDate = GuiAction.getContractExpiryDate(contDurationInput.getText().toString());
        if(contExpiryDate.equals("Contract duration must be atleast 3 months")) {
            contWarning.setText(contExpiryDate);
            contWarning.setForeground(Color.RED);
        }
        else {
            createContractAction c=new createContractAction(dets,"student",userId,bidid, contExpiryDate);
            if (c.checkContract()==false){
                newWarning.setText("you already have 5 one-to-one contracts");
                newWarning.setForeground(Color.RED);
            }
            else {
                c.storeContract();
                newWarning.setText("Your selection is noted.Contract creation in process, waiting for tutor to sign.");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==viewDetails){
            showAllBidViewDetails();
        }
        //view the student the details of the offer
        else if (e.getSource()==offerView){
            showOpenBidOfferDetails();
        }
        //student choosing a tutor
        else if (e.getSource()==closeBid){
            closeOpenBid();
            
        }
        else if (e.getSource()==closeBtn){
            newFrame.setVisible(false);
        }
        else if (e.getSource()==close){
            frame.setVisible(false);
        }


    }
/**
 * displays ui showing all offers of the bidid given
 * @param bidId: String bid id
 */

    private void showOpenBidOffers(String bidId){
        bidid=bidId;
        showAllOpenRequests(bidId);
        //close previous frame
        frame.setVisible(false);
        //show new frame
        newFrame = new JFrame("View Offer Details");
        // Setting the width and height of frame
        newFrame.setSize(900, 700);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
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
        JLabel actionLabel = new JLabel("View Offer Details");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select an offer and then click on view details");
        instruction.setBounds(10,80,1200,25);
        panel.add(instruction);


        final DefaultComboBoxModel model = new DefaultComboBoxModel(newComboBoxItems);

        moreOffers = new JComboBox(model);
        moreOffers.setBounds(10, 120, 700, 25);
        panel.add(moreOffers);


        offerView = new JButton("View details");
        offerView.setBounds(10, 180, 180, 25);
        offerView.addActionListener(this);
        panel.add(offerView);

        offerDetails=new JTextArea();
        offerDetails.setBounds(50,240,700,230);
        panel.add(offerDetails);


        newWarning=new JLabel();
        newWarning.setBounds(10,210,700,25);
        panel.add(newWarning);



        // allow student to choose contract duration 
        JLabel contDurationHeader = new JLabel("Please specify contract duration before selecting tutor");
        contDurationHeader.setBounds(10,500,450,25);
        contDurationHeader.setForeground(Color.BLUE);
        panel.add(contDurationHeader);
        
        JLabel contDuration = new JLabel("Contract duration");
        contDuration.setBounds(10,530,450,25);
        panel.add(contDuration);
        
        
        contDurationInput = new JTextField(20);
        contDurationInput.setBounds(120, 530, 70, 25);
        contDurationInput.setText("6");
        panel.add(contDurationInput);
        
        contWarning = new JLabel();
        contWarning.setBounds(200, 530, 500, 25);
        panel.add(contWarning);

        closeBid = new JButton("Select this tutor and close request");
        closeBid.setBounds(10, 580, 300, 25);
        closeBid.addActionListener(this);
        closeBid.setEnabled(false);
        panel.add(closeBid);

        newFrame.setVisible(true);



    }
    //an extra check to make sure that the bid is actually active
    private boolean bidExpired(String closeTimeDb) throws ParseException {
        //get todays date
        String today = new Date().toInstant().toString();
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String bidCloseTime = GuiAction.removeQuotations(closeTimeDb);
        Date todayDate = sourceFormat.parse(today);
        Date endDate = sourceFormat.parse(bidCloseTime);
        return todayDate.after(endDate) == false;
    }

    /* Method to show the current bids opened by the student  */
    private void showAllRequests(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid?fields=", myApiKey);
        try{
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                // process the initiator id to remove extra quotations
                String initId = node.get("initiator").get("id").toString();
                String initiatorId = GuiAction.removeQuotations(initId);
                //get closing date from additionalinfo and remove "" only if its not null
                if (node.get("additionalInfo").toString().equals("{}")==false) {
                    String closeTimeDb = node.get("additionalInfo").get("requestClosesAt").toString();
                    boolean check=bidExpired(closeTimeDb);
                    //show offer if not expired
                    if (initiatorId.contains(userId) && node.get("dateClosedDown").toString().equals("null") && check) {
                        String subjectName = node.get("subject").get("name").toString();
                        String desc = node.get("subject").get("description").toString();
                        String bidT = node.get("type").toString();
                        String bidid = node.get("id").toString();
                        String output = "Subject: "+subjectName  +"    "+ "Topic: "+ desc+"    "+"bid type "+bidT+" closing on:" + GuiAction.removeQuotations(closeTimeDb);
                        comboBoxItems.add(output);
                        bidType.add(GuiAction.removeQuotations(bidT));
                        bidIds.add(GuiAction.removeQuotations(bidid)); } } }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
* method to list all bid offers from all tutors for the bidid provided
* */
    private void showAllOpenRequests(String bidId) {
        // get all  messages of the bid
        String endpoint="bid/"+bidId+"?fields=messages";
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
            for (JsonNode msgNode : userNode.get("messages")) {
                if (userNode.get("additionalInfo").toString().equals("{}")==false){
                        //get all details
                        String  msgSender = msgNode.get("poster").get("userName").toString();
                        String  msgSenderId = GuiAction.removeQuotations(msgNode.get("poster").get("id").toString());
                        //check if there is any other offer from this tutor
                        int occurrences=0;
                        if (tutorids.size()!=0) {
                            occurrences = (int) tutorids.stream().filter(tutor -> msgSenderId.equals(tutor)).count();
                        }
                        tutorids.add(msgSenderId);
                        //get ll related info// alldetails={duration,rate,numberOfSession,freelesson,competency,extra,subjectId,qualification,student,tutor,subjectName};
                        String[] alldetails=getLessonDetails(msgNode,userNode);

                        String option="";
                        if (occurrences!=0){
                            option+=(occurrences+1)+" ";
                        }
                        String studId=GuiAction.removeQuotations(userNode.get("initiator").get("id").toString());
                        newComboBoxItems.add(option+"from: "+GuiAction.removeQuotations((msgSender)));
                        //create an open bid offer
                        OpenBidOffer newOffer=new OpenBidOffer(msgSenderId,studId,alldetails[8],alldetails[9]);
                        //set all info
                        newOffer.setClassInfo(alldetails[2],alldetails[0],alldetails[1],alldetails[11]);
                        newOffer.setExtraInfo(alldetails[3],alldetails[5]);
                        newOffer.setSubjectInfo(alldetails[6],alldetails[10],alldetails[4],alldetails[7]);
                        //add the new offer in the arrayList
                        offerInfo.add(newOffer); }
            }
        }
        catch(Exception e) {
            System.out.println(e.getCause());}
    }
    private String [] getLessonDetails(JsonNode msgNode,JsonNode userNode){

        String duration=GuiAction.removeQuotations(msgNode.get("additionalInfo").get("duration").toString());

        String rate=GuiAction.removeQuotations(msgNode.get("additionalInfo").get("rate").toString());

        String numberOfSession=GuiAction.removeQuotations(msgNode.get("additionalInfo").get("numberOfSession").toString());
        String freelesson=GuiAction.removeQuotations(msgNode.get("additionalInfo").get("freeLesson").toString());

        String competency=GuiAction.removeQuotations(msgNode.get("additionalInfo").get("tutorComp").toString());
        String extra=(msgNode.get("additionalInfo").get("extraInfo").toString());
        String subjectId=GuiAction.removeQuotations(userNode.get("subject").get("id").toString());

        String qualification=GuiAction.removeQuotations(msgNode.get("additionalInfo").get("tutorQualification").toString());
        //getting student and tutor full names
        String student=GuiAction.removeQuotations(userNode.get("initiator").get("givenName").toString())+" "+GuiAction.removeQuotations(userNode.get("initiator").get("familyName").toString());


        String tutor=GuiAction.removeQuotations(msgNode.get("poster").get("givenName").toString())+" "+GuiAction.removeQuotations(msgNode.get("poster").get("familyName").toString());


        String subjectName=GuiAction.removeQuotations(userNode.get("subject").get("description").toString());
        String requiredComp=userNode.get("additionalInfo").get("requiredCompetency").asText();

        return new String[]{duration,rate,numberOfSession,freelesson,competency,extra,subjectId,qualification,student,tutor,subjectName,requiredComp};
    }

}
