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

public class OpenBid extends BidAction implements ActionListener {
    private String bidid;
    private String userId;

    private JPanel panel;
    private JLabel subName,subDesc,requiredComp,weekSess,Hlp,rate;
    private JButton viewOtherBids,makeBidOffer,buyOutBtn;
    private static JLabel competencyAlert;
    private ArrayList<String> bidInfo;
    private String userFullName;
    public OpenBid(String bidId,String uId,String fname){
        bidid=bidId;
        userId=uId;
        userFullName=fname;
        showUI();
    }

    private void showUI(){
        bidInfo=getBidInfo(bidid);
        System.out.println("hdjh");
        // Creating instance of JFrame
        JFrame frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        panel.setBackground(new Color(172, 209, 233));

        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);

        // take user inputs over here
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

        viewOtherBids = new JButton("View other offers to this request");
        viewOtherBids.setBounds(10, 230, 300, 25);
        viewOtherBids.addActionListener(this);
        panel.add(viewOtherBids);

        makeBidOffer = new JButton("Make an offer");
        makeBidOffer.setBounds(10, 270, 300, 25);
        makeBidOffer.addActionListener(this);
        panel.add(makeBidOffer);


        competencyAlert = new JLabel();
        competencyAlert.setBounds(10,350,450,25);
        panel.add(competencyAlert);


        buyOutBtn = new JButton("Buy Out Bid");
        buyOutBtn.setBounds(10, 310, 300, 25);
        buyOutBtn.addActionListener(this);
        panel.add(buyOutBtn);
        frame.setVisible(true);



    }

    /* Method to find if the tutor's competency in the subject that they specialise in*/
    private int findTutorCompetency(String subName) {
        System.out.println("Inside the finding Competency function");
        String endpoint = "user/"+userId+"?fields=competencies.subject";
        int tutorcompetencyLevel = 0;
        HttpResponse<String> compResponse = APIRequester.initiateWebApiGET(endpoint, myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode.class);

            for (JsonNode node : userNode.get("competencies")) {
                // get the subject name that the tutor teaches and compare it to the requested one.
                String nodeSubName = node.get("subject").get("name").toString();
                String tutorSubName = APIRequester.removeQuotations(nodeSubName);
                if(tutorSubName.equals(subName)) {
                    System.out.println("Found the subject for which competency is needed");
                    tutorcompetencyLevel = node.get("level").asInt();
                    System.out.println("Competency Level is: "+tutorcompetencyLevel);
                    return tutorcompetencyLevel;
                }
            }
        }
        catch (Exception e){
            System.out.println("Error!!!");
            System.out.println(e.getCause());
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
        System.out.println("Inside the Competency check function");
        int competencyRequired=Integer.parseInt(bidInfo.get(2));
        System.out.println(competencyRequired);
        System.out.println(tutorCompetency);
        int twoLvlHigher=competencyRequired+2;
        if (tutorCompetency>=twoLvlHigher){
            return true;
        }
        return retVal;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==makeBidOffer){
            System.out.println("make");
            String subName = bidInfo.get(0);
            int level = findTutorCompetency(subName);
            if (isCompetent(level)==false){
                System.out.println("sdghsd");
                competencyAlert.setText("You do not have the required competency to bid on this request");
                competencyAlert.setForeground(Color.RED);
            }

            else{
                //can bid
                System.out.println("bod,bid!");
            }

//            String jsonString = null;
//            // create the message object
//            JSONObject msgInfo=new JSONObject();
//            msgInfo.put("bidId","a62e7937-ed0f-428d-ad1e-b8066242db4f" );
//            msgInfo.put("posterId","a753826d-3c66-4f89-8136-9af896b5bfd9" );
//            msgInfo.put("datePosted", new Date().toInstant().toString());
//            msgInfo.put("content", "this is a test");
//            JSONObject additionalInfo=new JSONObject();
//            msgInfo.put("additionalInfo", additionalInfo);
//
//            // convert message to JSON string
//            jsonString = msgInfo.toString();
//            webApiPOST("message", jsonString);
        }
        else if (e.getSource()==viewOtherBids){
            System.out.println("view");
        }
        else if (e.getSource()==buyOutBtn){
            String subName = bidInfo.get(0);
            int level = findTutorCompetency(subName);
            if (isCompetent(level)==false){
                competencyAlert.setText("You do not have the required competency to buy out this bid");
                competencyAlert.setForeground(Color.RED);
            }
            else{
                //can create contract and wait for student to sign
                createContract(userId);
            }

        }
    }

    private void createContract(String userId){
        String studentId=bidInfo.get(6);
        String endpoint="contract";
        Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        String contractEndTime;
        String jsonString="";
        //set a contract end time
        contractEndTime = new Date(timeInSecs + (365*24*60*60*1000)).toInstant().toString();
        String refId = null;  // id value to get the contract

        // create the contract
        JSONObject contractInfo=new JSONObject();
        //lets make tuor first party
        contractInfo.put("firstPartyId", userId);
        contractInfo.put("secondPartyId", studentId);
        contractInfo.put("subjectId", bidInfo.get(7));
        contractInfo.put("dateCreated", new Date().toInstant().toString());
        contractInfo.put("expiryDate",contractEndTime );

        JSONObject additionalInfo=new JSONObject();
        // create the additional info
        additionalInfo.put("subjectName", bidInfo.get(0));
        additionalInfo.put("subjectDesc", bidInfo.get(1));
        additionalInfo.put("competency", bidInfo.get(2));
        additionalInfo.put("weeklySession", bidInfo.get(3));
        additionalInfo.put("hoursPerLesson", bidInfo.get(4));
        additionalInfo.put("rate", bidInfo.get(5));
        additionalInfo.put("studentName", bidInfo.get(8));
        additionalInfo.put("tutorName", userFullName);

        contractInfo.put("additionalInfo", additionalInfo);

        //additionalInfo.put();



        jsonString = contractInfo.toString();
        // create a new contract in the database
        String Url = "https://fit3077.com/api/v1/"+endpoint;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Url))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (postResponse.statusCode()==201){
                competencyAlert.setText("Contract creation is in process. Waiting for student to confirm");
                competencyAlert.setForeground(new Color(0,102,0));
                new RequestCloser(5, bidid, myApiKey, new Date().toInstant().toString());
                System.out.println("closing bid in 5 s");
            }

            // get the id of the newly created object
            ObjectNode jsonNode = new ObjectMapper().readValue(postResponse.body(), ObjectNode.class);
            refId = jsonNode.get("id").asText();
            System.out.println(refId);
            //refId;

        }
        catch(Exception e){
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace()[0].getLineNumber());


        }

    }

    private String webApiPOST(String endpoint, String jsonString) {
        String refId = null;  // id value to get the subject or bid

        // create a new message in the database
        String Url = "https://fit3077.com/api/v1/"+endpoint;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Url))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        try {

            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            // get the id of the newly created object
            System.out.println(postResponse.statusCode());
            ObjectNode jsonNode = new ObjectMapper().readValue(postResponse.body(), ObjectNode.class);
            System.out.println("Bid/Message sent successfully");
            //Student.showAllRequests();

        }
        catch(Exception e){
            System.out.println("Error !!!!");
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace()[0].getLineNumber());


        }
        return refId;
    }
}