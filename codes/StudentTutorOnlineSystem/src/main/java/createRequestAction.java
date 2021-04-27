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
import java.util.Calendar;
import java.util.Date;

public class createRequestAction implements GuiAction, ActionListener {
    //public createRequestAction(){}
    public JPanel panel;
    private  JLabel competencyT,bidType;
    private  JComboBox compList,  allRates, bidTypes;
    private JButton submitButton;
    private static JTextField subjectText, descText,timeInput,sessionNum,rateIn;
    private boolean bidCreated;
    private String closeTime = null;



    private String userId,givenName,familyName;


    public createRequestAction(String uId,String gname,String fname){
        userId=uId;
        givenName=gname;
        familyName=fname;
    }


    @Override
    public void show() {
        // Creating instance of JFrame
        JFrame frame = new JFrame("Student Homepage");
        // Setting the width and height of frame
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);


        // take user inputs over here
        JLabel actionLabel = new JLabel("Make a matching request");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);



        JLabel desc = new JLabel("Specify request details");
        desc.setBounds(10,50,300,25);
        desc.setForeground(Color.red);
        panel.add(desc);


//
//        // Required qualification
//        JLabel qualification = new JLabel("Specialisation: ");
//        qualification.setBounds(10, 100, 100, 25);
//        panel.add(qualification);
//
//        subjectText = new JTextField(20);
//        subjectText.setBounds(100,100,165,25);
//        panel.add(subjectText);
//
//        // Types of qualification
//        String[] qualificationTypes = {"Bachelor's Degree", "Master's Degree", "Doctoral Degree","Secondary Education"};
//        qualList = new JComboBox(qualificationTypes);
//        qualList.setSelectedIndex(0);
//        qualList.setBounds(300, 100, 200, 25);
//        panel.add(qualList);

        competencyT=new JLabel("specify tutor competency level");
        competencyT.setBounds(10,70,340,25);
        panel.add(competencyT);

        String[] competencies = {"1","2","3","4","5","6","7","8","9","10"};
        compList = new JComboBox(competencies);
        compList.setSelectedIndex(0);
        compList.setBounds(10, 100, 60, 25);
        panel.add(compList);


        // Lesson
        JLabel lesson = new JLabel("Lesson subject: ");
        lesson.setBounds(10, 130, 200, 25);
        panel.add(lesson);

        subjectText = new JTextField(20);
        subjectText.setBounds(10,160,165,25);
        panel.add(subjectText);

        JLabel description = new JLabel("Lesson description: ");
        description.setBounds(10, 190, 200, 25);
        panel.add(description);

        descText = new JTextField(20);
        descText.setBounds(10,220,165,25);
        panel.add(descText);


        // Time and Day
        JLabel time = new JLabel("Hours per session: ");
        time.setBounds(10, 250, 200, 25);
        panel.add(time);

        // hour per session Selection
        timeInput = new JTextField(20);
        timeInput.setBounds(10,280,50,25);
        panel.add(timeInput);
//
//        String[] allTimes = {"AM", "PM"};
//        timeList = new JComboBox(allTimes);
//        timeList.setBounds(150, 170, 70, 25);
//        panel.add(timeList);

        // Day selection
//        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//        daysBox = new JComboBox(days);
//        daysBox.setSelectedIndex(0);
//        daysBox.setBounds(250, 170, 200, 25);
//        panel.add(daysBox);

        // Weekly Sessions
        JLabel session = new JLabel("Number of Weekly Sessions: ");
        session.setBounds(10, 310, 200, 25);
        panel.add(session);


        sessionNum= new JTextField(20);
        sessionNum.setBounds(10,340,50,25);
        panel.add(sessionNum);


        // Payment Rate

        JLabel rate = new JLabel("Rate (RM): ");
        rate.setBounds(10, 370, 100, 25);
        panel.add(rate);

        rateIn = new JTextField(20);
        rateIn.setBounds(80, 370, 80, 25);
        panel.add(rateIn);

        String[] rateTypes = {"per hour", "per session"};
        allRates = new JComboBox(rateTypes);
        allRates.setBounds(180, 370, 100, 25);
        panel.add(allRates);

        // Creating request button

        submitButton = new JButton("Make Request");
        submitButton.setBounds(10, 480, 120, 25);
        submitButton.addActionListener(this);
        panel.add(submitButton);

        bidType=new JLabel("specify the Bid Type");
        bidType.setBounds(10,400,400,25);
        panel.add(bidType);

        String[] bidT = {"Open", "Close"};
        bidTypes = new JComboBox(bidT);
        bidTypes.setBounds(10, 430, 430, 25);
        panel.add(bidTypes);


        // show the current bids
//        JLabel bidSectionHeader = new JLabel("Your current requests: ");
//        bidSectionHeader.setBounds(10, 330, 300, 25);
//        panel.add(bidSectionHeader);
//
//        JLabel instruction = new JLabel("Select a request/bid and click on 'Select Bidder' to close bid");
//        instruction.setBounds(10, 350,1200,25);
//        instruction.setForeground(Color.red);
//        panel.add(instruction);
//
//
//        requestMade = new JLabel("Your Request: ");
//        requestMade.setBounds(10, 380, 800, 25);
//        panel.add(requestMade);
//
//        // Weekly Sessions
//        JLabel responseLabel = new JLabel("All Responses: ");
//        responseLabel.setBounds(10, 420, 200, 25);
//        panel.add(responseLabel);
//
//
//        // all responses
//        allRequests = new JComboBox();
//        allRequests.setBounds(130, 420, 750, 25);
//        panel.add(allRequests);
//
//        requestStatus = new JLabel("Status: ");
//        requestStatus.setBounds(10, 450, 600, 25);
//        panel.add(requestStatus);
//
//
//        selectBtn = new JButton("Select Bidder");
//        selectBtn.setBounds(10, 490, 120, 25);
//        selectBtn.addActionListener(this);
//        panel.add(selectBtn);
//
        // Setting the frame visibility to true
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Tutor Request process started");
        // subject id of the subject that student wants
        String subId = findSubject();
        System.out.println("Subject ID: " + subId);

        // bid id of the new request
        String newBidID = webApiPOST("bid", subId);
        System.out.println("Bid ID: " + newBidID);
        System.out.println("Successfully created a tutor bid/request");
        if(bidCreated) {
            closeBid(newBidID, closeTime);
            bidCreated = false;
        }
        //showAllRequests();
    }

    /* Method to get the subject id for the subject given as input by the user */
    private String findSubject() {
        String subjectID = null;
        boolean subjectFound = false;
        // get the user inputs
        String userSub = subjectText.getText();
        String userDesc = descText.getText();
        userSub= userSub.substring(0, 1).toUpperCase() + userSub.substring(1);
        System.out.println(userSub);
        HttpResponse<String> subResponse = APIRequester.initiateWebApiGET("subject", myApiKey);
        try {
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(subResponse.body(), ObjectNode[].class);

            // look for the subject and description in the database
            for (ObjectNode node: jsonNodes) {
                String subFromDB = node.get("name").asText();
                System.out.println(subFromDB);
                String descFromDB = node.get("description").asText();
                System.out.println(descFromDB);

                if (subFromDB.equals(userSub) & descFromDB.equals(userDesc) ) {
                    System.out.println("Subject found in database");
                    subjectFound = true;
                    subjectID = node.get("id").asText();
                    return subjectID;
                }
            }

            if (subjectFound==false) {
                // if the subject is not found in database, then create new subject
                System.out.println("Subject not found, so creating new subject: " + userSub);
                subjectID = webApiPOST("subject","");
                System.out.println(subjectID);
                return subjectID;
            }
        }
        catch(Exception e) {
            System.out.println(e.getCause());
        }
        return subjectID;
    }

    protected String webApiPOST(String endpoint, String subID) {

        String refId = null;  // id value to get the subject or bid
        String jsonString = null;
        // set the endpoint types to be false
        boolean isSubject = false;
        boolean isBid = false;

        // endpoint.contains is used since we can have subject or subject/subjectID
        if(endpoint.contains("subject")) {
            // create a new JSON object for subject
            jsonString = "{" +
                    "\"name\":\"" + subjectText.getText() + "\"," +
                    "\"description\":\"" + descText.getText()+ "\"" +
                    "}";
            isSubject = true;
        }

        // endpoint.contains is used since we can have subject or subject/subjectID
        else if(endpoint.contains("bid")) {
            //bid type
            String bidT=bidTypes.getSelectedItem().toString();// bidtype
            // find today's date and time
            String bidStartTime = new Date().toInstant().toString();

            Calendar date = Calendar.getInstance();
            long timeInSecs = date.getTimeInMillis();
            String bidEndTime;
            if (bidT.contains("Open")){
                bidEndTime = new Date(timeInSecs + (30*60*1000)).toInstant().toString();}
            else{
                Integer minsInSevenDays=7*24*60;
                bidEndTime = new Date(timeInSecs + (minsInSevenDays*60*1000)).toInstant().toString();
            }
            System.out.println(bidEndTime);

            JSONObject additionalInfo=new JSONObject();
            // create the additional info
            //additionalInfo.put("qualificationLevel", qualList.getSelectedItem().toString());
            additionalInfo.put("requiredCompetency", compList.getSelectedItem().toString());
            additionalInfo.put("weeklySessions", sessionNum.getText());
            String sessionTime = timeInput.getText();
            additionalInfo.put("hoursPerLesson", sessionTime);
            String rate = "RM"+rateIn.getText()+" "+ allRates.getSelectedItem().toString();
            additionalInfo.put("rate", rate);

            // the web api does not accept "dateClosedDown" value when making POST
            additionalInfo.put("requestClosesAt", bidEndTime);
            closeTime = bidEndTime;

            // create the bid
            JSONObject bidInfo=new JSONObject();
            bidInfo.put("type", bidT.toLowerCase());
            bidInfo.put("initiatorId", userId);
            bidInfo.put("dateCreated", bidStartTime);
            bidInfo.put("subjectId", subID);
            bidInfo.put("additionalInfo", additionalInfo);
            jsonString = bidInfo.toString(); // convert to string
            System.out.println("Bid/Request Info: "+jsonString);
            isBid = true;

        }


        // create a new subject or bid in the database
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

            if(isSubject) {
                System.out.println("Created new subject in database");
            }
            else if(isBid) {
                System.out.println("Created new bid in database");
                bidCreated = true;
            }

            // get the id of the newly created object
            ObjectNode jsonNode = new ObjectMapper().readValue(postResponse.body(), ObjectNode.class);
            refId = jsonNode.get("id").asText();
            return refId;

        }
        catch(Exception e){
            System.out.println(e.getCause());
            System.out.println(e.getMessage());

        }
        return refId;
    }

    /*Method to close the bid after 30 minutes or 10080 mins it was created*/
    private void closeBid(String bidId, String closeTime) {
        // bid lasts for 10 seconds for now
        Integer seconds;
        if (bidTypes.getSelectedItem().toString().contains("Open")){
            seconds=10;
//            seconds=10;
        }
        else{
            seconds=7*24*60*60;
        }
        new RequestCloser(seconds, bidId, myApiKey, closeTime);
        System.out.println("Bid opened for 30 minutes.");
    }




}
