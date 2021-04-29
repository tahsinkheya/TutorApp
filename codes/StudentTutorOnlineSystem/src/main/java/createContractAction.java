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


    private JButton button;
    /* 1st constructor for students and tutors to aign contracts when contract has already been created by buy
    * out process or select tutor process and bid closed process*/
    public createContractAction(String contractid){
        contractId=contractid;
        findContractDetails();
    }
    /*2nd constructor for student to use to create a contract when selecting a tutor
    * */
    public createContractAction(OpenBidOffer acceptedOffer,String studentSign,String tutorSign){
        String endpoint="contract";
        Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        String contractEndTime;
        String jsonString="";
        //set a contract end time
        contractEndTime = new Date(timeInSecs + (365*24*60*60*1000)).toInstant().toString();

        // create the contract
        JSONObject contractInfo=new JSONObject();
        //lets make tuor first party
        System.out.println(acceptedOffer.getFirstPartyId());
        System.out.println(acceptedOffer.getSecondPartyId());
        System.out.println(acceptedOffer.getSubjectId());
        contractInfo.put("firstPartyId", acceptedOffer.getFirstPartyId());
        contractInfo.put("secondPartyId", acceptedOffer.getSecondPartyId());
        contractInfo.put("subjectId", acceptedOffer.getSubjectId());
        contractInfo.put("dateCreated", new Date().toInstant().toString());
        contractInfo.put("expiryDate",contractEndTime );

        JSONObject additionalInfo=new JSONObject();
        // create the additional info
        additionalInfo.put("subjectName", acceptedOffer.getStudentName());
        additionalInfo.put("subjectDesc", acceptedOffer.getSubjectDesc());
        additionalInfo.put("competency", acceptedOffer.getCompetency());
        additionalInfo.put("weeklySession", acceptedOffer.getWeeklySession());
        additionalInfo.put("hoursPerLesson", acceptedOffer.getHoursPerLesson());
        additionalInfo.put("rate", acceptedOffer.getRate());
        additionalInfo.put("studentName", acceptedOffer.getStudentName());
        additionalInfo.put("tutorName", acceptedOffer.getTutorName());
        additionalInfo.put("tutorQualification", acceptedOffer.getTutorQualification());
        additionalInfo.put("tutorSign", tutorSign);
        additionalInfo.put("studentSign", studentSign);


        contractInfo.put("additionalInfo", additionalInfo);

        jsonString=contractInfo.toString();
        HttpResponse<String> updateResponse = GuiAction.updateWebApi(endpoint, myApiKey, jsonString);
        System.out.println("status"+updateResponse.statusCode());




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
                // if theres no additionl info we dont know abt competetncy,weekly session,hpl,rate so we will put unknown for contractInfo
               if (userNode.get("additionalInfo").toString().equals("{}")){
                   String firstGname=GuiAction.removeQuotations(userNode.get("firstParty").get("givenName").toString());
                   String firstFname=GuiAction.removeQuotations(userNode.get("firstParty").get("familyName").toString());
                   String secondGname=GuiAction.removeQuotations(userNode.get("secondParty").get("givenName").toString());
                   String secondFname=GuiAction.removeQuotations(userNode.get("secondParty").get("familyName").toString());
                   //set tutot and student name for the contract details
                   tutorName=firstGname+" "+firstFname;
                   studentName=secondGname+" "+secondFname;
                   contractDetails.addAll(Arrays.asList("unknown","unknown","unknown","unknown","unknown"));
               }
               else{

                   String comp=userNode.get("additionalInfo").get("competency").toString();
                   String weeklySession=userNode.get("additionalInfo").get("weeklySession").toString();
                   String hpl=userNode.get("additionalInfo").get("hoursPerLesson").toString();
                   String rate=userNode.get("additionalInfo").get("rate").toString();
                   String stuname=userNode.get("additionalInfo").get("studentName").toString();
                   String tutename=userNode.get("additionalInfo").get("tutorName").toString();
                   contractDetails.add(GuiAction.removeQuotations(comp));
                   contractDetails.add(GuiAction.removeQuotations(weeklySession));
                   contractDetails.add(GuiAction.removeQuotations(hpl));
                   contractDetails.add(GuiAction.removeQuotations(rate));
                   studentName=GuiAction.removeQuotations(stuname);
                   tutorName=GuiAction.removeQuotations(tutename);
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




//
        subName=new JLabel("subject Name: "+contractDetails.get(0) );
        subName.setBounds(10,50,340,25);
        panel.add(subName);

        subDesc=new JLabel("subject Description: "+contractDetails.get(1) );
        subDesc.setBounds(10,80,340,25);
        panel.add(subDesc);

        requiredComp=new JLabel("Required Competency: "+contractDetails.get(2) );
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
        rate.setBounds(10,230,340,25);
        panel.add(rate);


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
                new ContractSigner(contractId,"");
                warning.setText("your contract has been finalised.");
                warning.setForeground(Color.BLUE);
            }
            else{
                warning.setText("please check the chekbox above");
                warning.setForeground(Color.RED);
            }
        }
    }
}
