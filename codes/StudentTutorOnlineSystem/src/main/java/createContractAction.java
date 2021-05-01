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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class createContractAction implements GuiAction, ActionListener {
    private String contractId;
    // this list contains(subname, subdesc,competency,weekly sess, hours per lesson, rate,tutorqualification)
    private ArrayList<String> contractDetails=new ArrayList<>();

    private JPanel panel;
    private String studentName,tutorName;
    private JLabel subName,subDesc,requiredComp,Hlp,rate,weekSess,warning;
    private JCheckBox c1;
    private String studentId,firstPartySigned,bidId;
    private OpenBidOffer acceptedOffer;


    private JButton button;
    /* 1st constructor for students and tutors to aign contracts when contract has already been created by buy
    * out process or select tutor process and bid closed process*/
    public createContractAction(String contractid){
        contractId=contractid;
        findContractDetails();
    }

    /*2nd constructor for student to use to create a contract when selecting a tutor
    * */
    public createContractAction(OpenBidOffer offer,String fps,String stuId,String bidid){
        acceptedOffer=offer;
        firstPartySigned=fps;
        studentId=stuId;
        bidId=bidid;
    }

    //method to check if student already has 5 contracts
    public boolean checkContract(){
        boolean retVal=false;
        int count=0;
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("contract", GuiAction.myApiKey);
        try {
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                String firstParty=node.get("firstParty").toString();
                String secondParty=node.get("secondParty").toString();
                if (firstParty.contains(studentId) | secondParty.contains(studentId)){
                    count+=1;
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

    //method to stire contract
    public void storeContract(){
        String endpoint="contract";
        String jsonString="";

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        //get date 1 yr from now
        cal.add(Calendar.YEAR, 1);
        String contractEndTime = cal.getTime().toInstant().toString();

        // create the contract
        JSONObject contractInfo=new JSONObject();
        contractInfo.put("firstPartyId", acceptedOffer.getFirstPartyId());
        contractInfo.put("secondPartyId", acceptedOffer.getSecondPartyId());
        contractInfo.put("subjectId", acceptedOffer.getSubjectId());
        contractInfo.put("dateCreated", new Date().toInstant().toString());
        contractInfo.put("expiryDate",contractEndTime );

        System.out.println(acceptedOffer.getFirstPartyId());
        System.out.println(acceptedOffer.getSecondPartyId());
        System.out.println(acceptedOffer.getSubjectId());
        System.out.println(new Date().toInstant().toString());
        System.out.println(contractEndTime);

        JSONObject lessonInfo=new JSONObject();
        // create the lesson info
        lessonInfo.put("subjectName", acceptedOffer.getSubjectId());
        lessonInfo.put("subjectDesc", acceptedOffer.getSubjectDesc());
        lessonInfo.put("competency", acceptedOffer.getCompetency());
        lessonInfo.put("weeklySession", acceptedOffer.getWeeklySession());
        lessonInfo.put("hoursPerLesson", acceptedOffer.getHoursPerLesson());
        lessonInfo.put("rate", acceptedOffer.getRate());
        lessonInfo.put("studentName", acceptedOffer.getStudentName());
        lessonInfo.put("tutorName", acceptedOffer.getTutorName());
        lessonInfo.put("tutorQualification", acceptedOffer.getTutorQualification());

        JSONObject additionalInfo=new JSONObject();
        //create additional info
        additionalInfo.put("firstPartySigned",firstPartySigned);

        contractInfo.put("lessonInfo", lessonInfo);

        //means that one of the party has signed
        if (firstPartySigned!=""){
            contractInfo.put("additionalInfo",additionalInfo);

        }

        jsonString=contractInfo.toString();
        HttpResponse<String> updateResponse = GuiAction.updateWebApi(endpoint, myApiKey, jsonString);
        System.out.println("status"+updateResponse.statusCode());
        if (updateResponse.statusCode()==201){
            new RequestCloser(1, bidId, myApiKey, new Date().toInstant().toString());
        }


    }

    private void findContractDetails(){
        String endpoint = "contract/"+contractId;
		HttpResponse<String> response = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
			try {
                ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
                String subName=userNode.get("subject").get("name").toString();
                String subDesc=userNode.get("subject").get("description").toString();
                //add the subject name and description by removing the quotation marks
                contractDetails.add(GuiAction.removeQuotations(subName));
                contractDetails.add(GuiAction.removeQuotations(subDesc));
                //we need to check if for this contract any of the parties has already signed
                String str=userNode.get("additionalInfo").toString();
                //if none of the parties has signed then additionalInfo will be empty
                if (str.equals("{}")){
                    firstPartySigned=""; //if the student accepts we change the additionalInfo value using patch
                }
                //else one of the party has signed
                else{
                    firstPartySigned="yes";
                }
                String firstGname=GuiAction.removeQuotations(userNode.get("firstParty").get("givenName").toString());
                String firstFname=GuiAction.removeQuotations(userNode.get("firstParty").get("familyName").toString());
                String secondGname=GuiAction.removeQuotations(userNode.get("secondParty").get("givenName").toString());
                String secondFname=GuiAction.removeQuotations(userNode.get("secondParty").get("familyName").toString());
                //set tutot and student name for the contract details
                tutorName=firstGname+" "+firstFname;
                studentName=secondGname+" "+secondFname;
                // if theres no additionl info we dont know abt competetncy,weekly session,hpl,rate so we will put unknown for contractInfo
               if (userNode.get("lessonInfo").toString().equals("{}")){

                   contractDetails.addAll(Arrays.asList("unknown","unknown","unknown","unknown","unknown"));
               }
               else{

                   String comp=userNode.get("lessonInfo").get("competency").toString();
                   String weeklySession=userNode.get("lessonInfo").get("weeklySession").toString();
                   String hpl=userNode.get("lessonInfo").get("hoursPerLesson").toString();
                   String rate=userNode.get("lessonInfo").get("rate").toString();
                   String tuteQualification=userNode.get("lessonInfo").get("tutorQualification").toString();
                   contractDetails.add(GuiAction.removeQuotations(comp));
                   contractDetails.add(GuiAction.removeQuotations(weeklySession));
                   contractDetails.add(GuiAction.removeQuotations(hpl));
                   contractDetails.add(GuiAction.removeQuotations(rate));
                   contractDetails.add(GuiAction.removeQuotations(tuteQualification));
               }


			} catch (Exception e) {
                e.printStackTrace();
            }
//			for (String d:contractDetails)
//			{
//			    System.out.println(d);
//            }
    }
    @Override
    public void show() {
        // Creating instance of JFrame
        javax.swing.JFrame frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        panel.setBackground(new Color(172, 209, 233));

        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);

        JLabel agreementText = new JLabel("This agreement made on "+new SimpleDateFormat("dd-MM-yyyy").format(new Date())+" between "+studentName+" and "+tutorName);
        agreementText.setBounds(50,20,800,30);
        agreementText.setForeground(Color.BLUE);
        agreementText.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(agreementText);

        subName=new JLabel("subject Name: "+contractDetails.get(0) );
        subName.setBounds(10,50,340,25);
        panel.add(subName);

        subDesc=new JLabel("subject Description: "+contractDetails.get(1) );
        subDesc.setBounds(10,80,340,25);
        panel.add(subDesc);

        requiredComp=new JLabel("Tutor Competency: "+contractDetails.get(2) );
        requiredComp.setBounds(10,110,340,25);
        panel.add(requiredComp);

        weekSess=new JLabel("Number of session per week: "+contractDetails.get(3) );
        weekSess.setBounds(10,140,340,25);
        panel.add(weekSess);

        Hlp=new JLabel("Hours per Lesson: "+contractDetails.get(4) );
        Hlp.setBounds(10,170,340,25);
        panel.add(Hlp);

        rate=new JLabel("Rate: "+contractDetails.get(5) );
        rate.setBounds(10,200,340,25);
        panel.add(rate);


        JLabel qualification=new JLabel("Tutor Qualification/s: "+contractDetails.get(6) );
        qualification.setBounds(10,230,340,25);
        panel.add(qualification);


        c1 = new JCheckBox("");
        c1.setBounds(10,270,20,20);
        panel.add(c1);

        JLabel confirmText= new JLabel("I agree to this contract content");
        confirmText.setBounds(30,270,380,25);
        panel.add(confirmText);

        warning= new JLabel();
        warning.setBounds(10,300,380,25);
        panel.add(warning);

        button= new JButton("Confirm and Proceed");
        button.setBounds(60,400,320,25);
        button.addActionListener(this);
        panel.add(button);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==button){
            if (c1.isSelected()==true){
                //proceed to confirming contract
                if (firstPartySigned.equals("")) //since now one party has signed update that in the contract
                {
                    warning.setText("contract creation in process, waiting for other party");
                    updateContract();
                }
                //both parties have now signed the contract
                else{
                    new ContractSigner(contractId,"");
                    warning.setText("your contract has been finalised.");

                }
                warning.setForeground(Color.BLUE);
            }
            else{
                warning.setText("please check the chekbox above");
                warning.setForeground(Color.RED);
            }
        }
    }

    private void updateContract(){
        String endpoint="contract/"+contractId;
        // create the contract object
        JSONObject contractInfo=new JSONObject();
        JSONObject additionalInfo=new JSONObject();
        additionalInfo.put("firstPartySigned","check");
        contractInfo.put("additionalInfo",additionalInfo);
        String jsonString = contractInfo.toString();

        //System.out.println("The abstract class has this: "+jsonString);
        String Url = "https://fit3077.com/api/v1/"+endpoint;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Url))
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json")
                .method("PATCH",HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
        }
        catch (Exception e){
            System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
    }
}
