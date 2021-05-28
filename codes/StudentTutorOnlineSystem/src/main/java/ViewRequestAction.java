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
/*a class to show tutors the requests made by students*/
public class ViewRequestAction implements GuiAction, ActionListener {

    private String userId,fullName;
    private static JComboBox allRequests;
    // list of all students' bid id since it is needed make message
    private ArrayList<String> allStudentBidList = new ArrayList<String>();
    private ArrayList<String> bidType = new ArrayList<String>();
    private ArrayList<String> studentId = new ArrayList<String>();
    private JButton closeBtn;
    public JPanel panel;
    private JButton viewDetails;
    private Vector comboBoxItems=new Vector();
    private JLabel warning;
    private JFrame frame;
//constructor
    public ViewRequestAction(String uId,String name){
        userId=uId;
        fullName=name;

    }
    //getter methods
    private String getUserId(){return userId;}
    private String getfullName(){return fullName;}
    /* Show the tutor all the requests made by students  */
    @Override
    public void show() {
        showAllStudentRequests();
        // Creating instance of JFrame
        frame = new JFrame("Tutor Homepage");
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);
        panel.setBackground(new Color(172, 209, 233));

        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        panel.add(closeBtn);

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
        frame.setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewDetails) {
            //show request details
            shoeReqDetail();

        }
        else if (e.getSource()==closeBtn){
            frame.setVisible(false);
        }
    }
// a method that is called when a button is clisked to show bid details by using BIDAction class
    private void shoeReqDetail(){
        if (bidType.size()!=0) {
            String bidid = getSelectedRequest();
            String bidTypeOfselected = getSelectedBidType();
            int selectedRequestPos = allRequests.getSelectedIndex();
            String selectedStudentId=studentId.get(selectedRequestPos);
            if (bidTypeOfselected.contains("open")) {
                //create open bid action  and close depending on the type
                new OpenBidAction(bidid, getUserId(), getfullName()); }
            else {
                new CloseBidAction(bidid, getUserId(),getfullName(),selectedStudentId); }
        }
        else{
            warning.setText("there are no active requests available");
            warning.setForeground(Color.red); }
    }
    //gets all student requests and put them in the drop down menu
    private void showAllStudentRequests(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("user?fields=initiatedBids", myApiKey);
        try {
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node: jsonNodes) {
                for (JsonNode bidNode : node.get("initiatedBids")) {
                    // show bids that are not closed down
                    if(bidNode.get("dateClosedDown").toString().equals("null") ) {
                        String closeTimeDb = "";
                        String bidCloseTime = "";
                        // this will throw an exception. The requested bid always has additional info, so this exception will not cause any problem
                        if(bidNode.get("additionalInfo").equals(null)) {
                            System.out.println("Additional info is null"); }
                        else {
                            try{
                                closeTimeDb = bidNode.get("additionalInfo").get("requestClosesAt").toString();
                                bidCloseTime = GuiAction.removeQuotations(closeTimeDb); }
                            catch(Exception e){System.out.println("requestClosesAt not found");}
//
                        }

                        putOpenRequests(bidNode,node,closeTimeDb,bidCloseTime);
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println(e.getCause());
        }
    }
//a method to put details abt an active request in the jcombobox
    private void putOpenRequests(JsonNode bidNode,JsonNode node,String closeTimeDb, String bidCloseTime ) throws ParseException {
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
                String studId=node.get("id").toString();
                studentId.add(GuiAction.removeQuotations(studId)); } }
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
