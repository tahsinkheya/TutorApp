import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * Base class for tutor to view bid info abt an open bid request. the tutor can buyout the bid, view other bid offers and
 */

public class OpenBidAction extends BidAction implements ActionListener {
    private String bidid;
    private String userId;

    private JPanel panel;
    private JLabel subName,subDesc,requiredComp,weekSess,Hlp,rate, contWarning;
    private JButton viewOtherBids,makeBidOffer,buyOutBtn;
    private JFrame frame;
    private static JLabel competencyAlert;
    private ArrayList<String> bidInfo;
    private String userFullName;
    private JTextField contDurationInput;
    public OpenBidAction(String bidId, String uId, String fname){
        bidid=bidId;
        userId=uId;
        userFullName=fname;
        showUI();
    }
    //method to show ui to tutor abt the open bid request
    private void showUI(){
        bidInfo=getBidInfo(bidid);

        // Creating instance of JFrame
        frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        panel.setBackground(new Color(172, 209, 233));

        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);

        // show title
        JLabel actionLabel = new JLabel("Bid Details");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);



        subName=new JLabel("subject Name: "+bidInfo.get(0) );
        subName.setBounds(10,50,340,25);
        panel.add(subName);

        subDesc=new JLabel("subject Description: "+bidInfo.get(1) );
        subDesc.setBounds(10,80,340,25);
        panel.add(subDesc);

        requiredComp=new JLabel("Required Competency: "+bidInfo.get(2) );
        requiredComp.setBounds(10,110,340,25);
        panel.add(requiredComp);

        weekSess=new JLabel("Number of session per week: "+bidInfo.get(3) );
        weekSess.setBounds(10,140,340,25);
        panel.add(weekSess);

        Hlp=new JLabel("Hours per Lesson: "+bidInfo.get(4) );
        Hlp.setBounds(10,170,340,25);
        panel.add(Hlp);

        rate=new JLabel("Rate: "+bidInfo.get(5) );
        rate.setBounds(10,200,340,25);
        panel.add(rate);

        viewOtherBids = new JButton("Subscribe to this Bid");
        viewOtherBids.setBounds(10, 230, 300, 25);
        viewOtherBids.addActionListener(this);
        panel.add(viewOtherBids);

        makeBidOffer = new JButton("Make an offer");
        makeBidOffer.setBounds(10, 270, 300, 25);
        makeBidOffer.addActionListener(this);
        panel.add(makeBidOffer);


        // allow tutor to choose contract duration 
        JLabel contDuration = new JLabel("Please specify contract duration before buying out");
        contDuration.setBounds(10,310,450,25);
        contDuration.setForeground(Color.BLUE);
        panel.add(contDuration);
        
        contDuration = new JLabel("Contract duration");
        contDuration.setBounds(10,350,450,25);
        panel.add(contDuration);
        
        
        contDurationInput = new JTextField(20);
        contDurationInput.setBounds(120, 350, 70, 25);
        contDurationInput.setText("6");
        panel.add(contDurationInput);
        
        contWarning = new JLabel();
        contWarning.setBounds(200, 350, 500, 25);
        panel.add(contWarning);
        		
        		
        buyOutBtn = new JButton("Buy Out Bid");
        buyOutBtn.setBounds(10, 400, 300, 25);
        buyOutBtn.addActionListener(this);
        panel.add(buyOutBtn);
        

        competencyAlert = new JLabel();
        competencyAlert.setBounds(10,430,450,25);
        panel.add(competencyAlert);

        frame.setVisible(true);

    }


    /* Method to find if the tutor's competency in the subject that they specialise in*/
    private int findTutorCompetency(String subName) {
        String endpoint = "user/"+userId+"?fields=competencies.subject";
        int tutorcompetencyLevel = 0;
        HttpResponse<String> compResponse = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode.class);

            for (JsonNode node : userNode.get("competencies")) {
                // get the subject name that the tutor teaches and compare it to the requested one.
                String nodeSubName = node.get("subject").get("name").toString();
                String tutorSubName = GuiAction.removeQuotations(nodeSubName);
                if(tutorSubName.equals(subName)) {
                    tutorcompetencyLevel = node.get("level").asInt();
                    return tutorcompetencyLevel;
                }
            }
        }
        catch (Exception e){
            System.out.println("Error!!!");
        }

        // competency level is zero
        return tutorcompetencyLevel;
    }
    /* Method to find whether the tutor is competent enough to teach the subject in the bid
     * The required competency given in the request can be obtained using the bidId
     *
     * the tutor can only buyout and response to bids that fulfill the competency
     * requirements (two level above the required competency).
     * */
    private boolean isCompetent(int tutorCompetency) {
        boolean retVal=false;
        int competencyRequired=Integer.parseInt(bidInfo.get(2));
        int twoLvlHigher=competencyRequired+2;
        if (tutorCompetency>=twoLvlHigher){
            return true;
        }
        return retVal;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==makeBidOffer){
            String tutorQualification=TutorQualification(userId);
            String subName = bidInfo.get(0);
            int level = findTutorCompetency(subName);
            if (isCompetent(level)==false){
                showMessage("You do not have the required competency to bid on this request","red");
            }

            else{
                //can bid
                frame.setVisible(false);
                new MakeOpenBidOffer(bidid,userId,level,tutorQualification);
            }

        }
        else if (e.getSource()==viewOtherBids){
            frame.setVisible(false);
            new ViewOtherTutorBids(bidid,userId);
        }
        else if (e.getSource()==buyOutBtn){
            String subName = bidInfo.get(0);
            int level = findTutorCompetency(subName);
            if (isCompetent(level)==false){
                showMessage("You do not have the required competency to buy out this bid","red");
            }
            else{
                //can create contract and wait for student to sign
                createContract(userId,level);
                showMessage("contract creation in process. waiting for student","blue");
            }

        }
    }
//method to show warning to user
    private void showMessage(String msg,String colour){
        competencyAlert.setText(msg);
        if (colour.contains("blue")){
            competencyAlert.setForeground(Color.blue);}
        else{
            competencyAlert.setForeground(Color.red);
        }
    }
    //method used by tutor to create contract when buying out a bid
    private void createContract(String userId,int tuteCompetency){
        //lets create a OpenBidOffer and pass it to the createcontractaction class
        String studentId=bidInfo.get(6);
        OpenBidOffer offer=creatOffer(userId,tuteCompetency);
        //tutor is the first party to sign
        String contExpiryDate = GuiAction.getContractExpiryDate(contDurationInput.getText().toString());
        if(contExpiryDate.equals("Contract duration must be atleast 3 months")) {
        	contWarning.setText(contExpiryDate);
        	contWarning.setForeground(Color.RED);
        	competencyAlert.setVisible(false);
        }
        else {
        	createContractAction contract=new createContractAction(offer,"tutor",studentId,bidid, contExpiryDate);
            contract.storeContract();
            competencyAlert.setVisible(true);
        }

    }
    //a method to create an open bid offer instance for use later
    private OpenBidOffer creatOffer(String userId,Integer tuteCompetency){
        String studentId=bidInfo.get(6);
        String tutorQualification=TutorQualification(userId);
        String tutorCompetency=Integer.toString(tuteCompetency);
        //set all info
        OpenBidOffer offer=new OpenBidOffer(userId,studentId,bidInfo.get(8),userFullName);
        offer.setClassInfo(bidInfo.get(3),bidInfo.get(4),bidInfo.get(5));
        offer.setExtraInfo("no","");
        offer.setSubjectInfo(bidInfo.get(7),bidInfo.get(1),tutorCompetency,tutorQualification);
        return offer;
    }


}