import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
// a class that renews contract witht the same tutor as the contract with different terms and conds
public class SameTutorDifferntConditions extends ContractRenewal implements ActionListener {

    private String studentId,contractId,subname,NoMons,reqComp;
    private JLabel text,offerWarning;
    private JFrame previousFrame,frame;
    private JButton newcloseBtn,confirm;
    private JTextField weeklySeeion,hours,rate;
    private JComboBox allRates;
    //setter method
    public void setRequiredComp(String num){
        reqComp=num;
    }
    //constructior
    public  SameTutorDifferntConditions(JLabel jLabel,String contract,String subject, String stuid,String mons,JFrame frame,String comp){
        text=jLabel;
        contractId=contract;
        subname=subject;
        studentId=stuid;
        NoMons=mons;
        previousFrame=frame;
        setRequiredComp(comp);
        execute();

    }
// method that calls other helper methods to excecute the feature
    private void execute(){
        boolean subjectMatched=super.checkSubject(subname,contractId);
        if (subjectMatched){

            if (Integer.parseInt(NoMons)<3){
                showWarning("please choose number of month greater than equal 3");
            }
            else{
                previousFrame.setVisible(false);
                showMessage();}
        }
        else{
            showWarning("the subject of this contract is not "+subname);

        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==newcloseBtn){
            frame.setVisible(false);
        }
        else if (e.getSource()==confirm){
            getContractDets();
        }
    }
// method to get contract details from the user
    private void getContractDets(){
        String newRate="RM: "+rate.getText()+" "+allRates.getSelectedItem();
        String weeklyS=weeklySeeion.getText();
        String horsPerLson=hours.getText();
        String tutorId="";
        String subId="";
        String subjectName="";
        HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract/"+contractId, GuiAction.myApiKey);
        try {
            ObjectNode jsonNode = new ObjectMapper().readValue(contResponse.body(), ObjectNode.class);
            String firstId=jsonNode.get("firstParty").get("id").asText();
            String secondId=jsonNode.get("secondParty").get("id").asText();
            subId=jsonNode.get("subject").get("id").asText();
            subjectName=jsonNode.get("subject").get("name").asText();
            if(firstId.contains(studentId)){
                tutorId=secondId;
            }
            else{tutorId=firstId;}


        }
        catch (Exception e){System.out.println(e.getStackTrace()[0].getLineNumber()); }
        OpenBidOffer offer=new OpenBidOffer(studentId,tutorId,"","");
        offer.setClassInfo(weeklyS,horsPerLson,newRate,reqComp);
        int tuteComp=GuiAction.getTuteComp(subjectName,tutorId);
        String tuteQ=GuiAction.getTutorQualification(tutorId);
        offer.setSubjectInfo(subId,subjectName,String.valueOf(tuteComp),tuteQ);
        offer.setExtraInfo("","");
        storeContract(offer);

    }


    private void storeContract(OpenBidOffer offer){
        createContractAction contract=new createContractAction(offer,"student",studentId,"",GuiAction.getContractExpiryDate(NoMons));
        contract.storeContract();
        offerWarning.setText("Contract creation in process waiting for the tutor");
    }
// method to show ui to user to get info abt new terms and conditions
    @Override
    void showMessage() {
        frame = new JFrame("Add Contract Details");
        // Setting the width and height of frame
        frame.setSize(900, 600);
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


        JLabel instruction = new JLabel("please fill all information");
        instruction.setBounds(10, 80, 1200, 25);
        instruction.setForeground(Color.BLUE);
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
        confirm.setBounds(10, 350, 100, 25);
        confirm.addActionListener(this);
        newPanel.add(confirm);

        offerWarning = new JLabel("");
        offerWarning.setBounds(10, 370, 1200, 25);
        newPanel.add(offerWarning);

        frame.setVisible(true);


    }
//method to show warning to user
    @Override
    void showWarning(String msg) {
        text.setText(msg);
        text.setForeground(Color.red);
    }
}
