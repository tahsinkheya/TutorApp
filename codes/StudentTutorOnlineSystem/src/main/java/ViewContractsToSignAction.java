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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

// class to show contract that are already ctreated  to users for them to sign*
public class ViewContractsToSignAction implements GuiAction, ActionListener {
    private String contractId;
    private String userType;

    private String studentName,tutorName;
    // this list contains(subname, subdesc,competency,weekly sess, hours per lesson, rate,tutorqualification)
    private ArrayList<String> contractDetails=new ArrayList<>();
    private String firstPartySigned;
    private ArrayList <String> contractIds= new ArrayList<>();
    private  JComboBox allContracts;
    //contains all contracts that need signing
    private Vector comboBoxItems=new Vector();
    //ui elements
    private JPanel panel,contractPanel;
    private JLabel subName,subDesc,requiredComp,Hlp,rate,weekSess,warning;
    private JFrame frame;
    private JCheckBox c1;
    private JButton button,viewDetails;

    @Override
    public void show(){
        // Creating instance of JFrame
        frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        contractPanel = new JPanel();
        // adding panel to frame
        frame.add(contractPanel);
        contractPanel.setLayout(null);


        JLabel relListTitle = new JLabel("All Contracts Pending Confimation");
        relListTitle.setBounds(10,50,450,25);
        contractPanel.add(relListTitle);


        JLabel instruction = new JLabel("Select a contract and then click on view details");
        instruction.setBounds(10,80,400,25);
        instruction.setForeground(Color.red);
        contractPanel.add(instruction);

        final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
        allContracts = new JComboBox(model);
        allContracts.setBounds(10, 120, 700, 25);
        contractPanel.add(allContracts);



        viewDetails = new JButton("View details");
        viewDetails.setBounds(10, 180, 180, 25);
        viewDetails.addActionListener(this);
        contractPanel.add(viewDetails);

        frame.setVisible(true);


    }


    private void showContract() {
        findContractDetails();
        // Creating instance of JFrame
        javax.swing.JFrame newFrame = new JFrame();
        // Setting the width and height of frame
        newFrame.setSize(900, 500);
        newFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setBackground(new Color(172, 209, 233));

        // adding panel to frame
        newFrame.add(panel);
        panel.setLayout(null);
        //add jlabels and checkbox
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


        JLabel expiryDate=new JLabel("Contract Expiry date : "+contractDetails.get(7) );
        expiryDate.setBounds(10,260,340,25);
        panel.add(expiryDate);

        c1 = new JCheckBox("");
        c1.setBounds(10,300,20,20);
        panel.add(c1);

        JLabel confirmText= new JLabel("I agree to this contract content");
        confirmText.setBounds(35,300,380,25);
        panel.add(confirmText);

        warning= new JLabel();
        warning.setBounds(10,370,380,25);
        panel.add(warning);

        button= new JButton("Confirm and Proceed");
        button.setBounds(60,400,320,25);
        button.addActionListener(this);
        panel.add(button);
        newFrame.setVisible(true);

    }

    /*constructor for students and tutors to sign contracts when contract has already been created by buy
     * out process or select tutor process and bid closed process*/
    public ViewContractsToSignAction(String usertype,ArrayList<String> contracts,Vector items){
        contractIds=contracts;
        userType=usertype;
        comboBoxItems=items;
    }
    //method to find details of a contract to display to user
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
                firstPartySigned=""; //if the student/tutor accepts we change the additionalInfo value using patch
            }
            //else one of the party has signed
            else{ firstPartySigned=userType; }
             //set tutor and student name for the contract details
            tutorName=GuiAction.removeQuotations(userNode.get("firstParty").get("givenName").toString())+" "+GuiAction.removeQuotations(userNode.get("firstParty").get("familyName").toString());;
            studentName=GuiAction.removeQuotations(userNode.get("secondParty").get("givenName").toString())+" "+GuiAction.removeQuotations(userNode.get("secondParty").get("familyName").toString());;
            // if theres no additionl info we dont know abt competetncy,weekly session,hpl,rate so we will put unknown for contractInfo
            if (userNode.get("lessonInfo").toString().equals("{}")){
                contractDetails.addAll(Arrays.asList("unknown","unknown","unknown","unknown","unknown","unkown")); }
            else{
                //get details and store them
                getContractDets(userNode); }
        } catch (Exception e) {System.out.println(e.getStackTrace()[0].getLineNumber());}
    }
    // a method to get details abt a contract and put it in a list for use later
    private void getContractDets(JsonNode userNode){
        String comp=userNode.get("lessonInfo").get("competency").toString();
        String weeklySession=userNode.get("lessonInfo").get("weeklySession").toString();
        String hpl=userNode.get("lessonInfo").get("hoursPerLesson").toString();
        String rate=userNode.get("lessonInfo").get("rate").toString();
        String tuteQualification=userNode.get("lessonInfo").get("tutorQualification").toString();
        String contractExpiryDate = userNode.get("expiryDate").toString();

        contractDetails.add(GuiAction.removeQuotations(comp));
        contractDetails.add(GuiAction.removeQuotations(weeklySession));
        contractDetails.add(GuiAction.removeQuotations(hpl));
        contractDetails.add(GuiAction.removeQuotations(rate));
        contractDetails.add(GuiAction.removeQuotations(tuteQualification));
        contractDetails.add(GuiAction.removeQuotations(contractExpiryDate));
        System.out.println(contractDetails.add(GuiAction.removeQuotations(contractExpiryDate)));
    }
    //method to patch a contract this is done only when a contract was created by the system at the ened of an open bid and
    // and one of the parties sign the contract
    private void updateContract(){
        String endpoint="contract/"+contractId;
        // update the contract object since one party has signed
        JSONObject contractInfo=new JSONObject();
        JSONObject additionalInfo=new JSONObject();
        additionalInfo.put("firstPartySigned",userType);
        contractInfo.put("additionalInfo",additionalInfo);
        String jsonString = contractInfo.toString();
        GuiAction.patchWebApi(endpoint,jsonString);

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
                    new ContractSigner(contractId,GuiAction.myApiKey);
                    warning.setText("your contract has been finalised.");
                }
                warning.setForeground(Color.BLUE);
            }
            else{
                warning.setText("please check the chekbox above");
                warning.setForeground(Color.RED);
            }
        }
        else if (e.getSource()==viewDetails){
            contractId=contractIds.get(allContracts.getSelectedIndex());
            frame.setVisible(false);
            showContract();
        }
    }
}
