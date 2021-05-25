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

public class RenewContractAction implements GuiAction, ActionListener {
    private String userId;
    private String userFullName;
    private JFrame renewFrame;
    private JPanel renewPanel;
    private JButton newcloseBtn,selectbtn,viewDetails;
    private Vector newComboBoxItems=new Vector();
    private JComboBox contracts;
    private JLabel warning;
    private ArrayList<String> contractInfo=new ArrayList<>();
    private JTextArea message;
    public RenewContractAction(String id,String userName){
        userId=id;
        userFullName=userName;
        showExpiredContracts();
    }

    @Override
    public void show() {
        renewFrame = new JFrame();
        // Setting the width and height of frame
        renewFrame.setSize(900, 600);
        renewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        renewPanel= new JPanel();
        renewFrame.add(renewPanel);
        renewPanel.setBackground(new Color(172, 209, 233));
        renewPanel.setLayout(null);

        JLabel title = new JLabel("All expired/terminated contracts for you to renew");
        title.setBounds(100,20,700,25);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        renewPanel.add(title);

        final DefaultComboBoxModel model = new DefaultComboBoxModel(newComboBoxItems);
        contracts = new JComboBox(model);
        contracts.setBounds(10, 120, 700, 25);
        renewPanel.add(contracts);

        warning=new JLabel();
        warning.setBounds(10,150,700,25);
        renewPanel.add(warning);


        message = new JTextArea();
        message.setBounds(10,220,340,150);
        message.setEditable(false);
        renewPanel.add(message);
        addButtons();



        renewFrame.setVisible(true);
    }

    private boolean expiredOrTerminated(JsonNode node) throws ParseException {

        String today = new Date().toInstant().toString();
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String bidCloseTime = node.get("expiryDate").asText();

        Date todayDate = sourceFormat.parse(today);
        Date endDate = sourceFormat.parse(bidCloseTime);

        boolean close=todayDate.after(endDate);
        boolean terminated=!node.get("terminationDate").toString().equals("null");
        return (close || terminated);
    }

    private void showExpiredContracts(){
        HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract", myApiKey);
        try {
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(contResponse.body(), ObjectNode[].class);

            for (ObjectNode node: jsonNodes) {
                // get the signed date for contract to check if it is null or not
                String firstParty=node.get("firstParty").get("id").asText();
                String secondParty=node.get("secondParty").get("id").asText();

                if (firstParty.contains(userId) || secondParty.contains(userId)){
                    boolean expiredcontract=expiredOrTerminated(node);
                    boolean signed=!node.get("dateSigned").toString().equals("null");
                    if (expiredcontract){
                        System.out.println(node.get("id").asText());
                    }
                    if(expiredcontract && signed){
                        String firstPartyName=node.get("firstParty").get("userName").asText();
                        String secondPartyName=node.get("secondParty").get("userName").asText();
                        String subName=node.get("subject").get("name").asText();

                        newComboBoxItems.add("between "+firstPartyName+" and "+secondPartyName+" for "+subName);
                        getInfo(node);
                    }
                }

            }
        }catch(Exception e){System.out.println(e.getMessage());
            System.out.println(e.getStackTrace()[0].getLineNumber());
        }
    }

    private void getInfo(JsonNode userNode){

        String tutorName=userNode.get("firstParty").get("givenName").asText()+" "+userNode.get("firstParty").get("familyName").asText();
        String studentName=userNode.get("secondParty").get("givenName").asText()+" "+userNode.get("secondParty").get("familyName").asText();

        String weeklySession=userNode.get("lessonInfo").get("weeklySession").asText();
        String hpl=userNode.get("lessonInfo").get("hoursPerLesson").asText();
        String rate=userNode.get("lessonInfo").get("rate").asText();
        String contractExpiryDate = userNode.get("expiryDate").asText();
        String subName=userNode.get("subject").get("name").asText();
        String subDesc=userNode.get("subject").get("description").asText();
        String compReq=userNode.get("lessonInfo").get("requiredCompetency").asText();
        contractInfo.add("Between "+tutorName+" and "+studentName+"\n" +
                "Subject name:"+subName+"\n"+"Subject desc:"+subDesc+"\n"+
                "Comptency required:"+compReq+"\n"+"Weekly Session:"+weeklySession+"\n"+
                "Hours Per Lesson :"+hpl+"\n"+"Rate:"+rate+"\n"+"Expired on"+contractExpiryDate);

    }
    //a method to add buttons to th ui
    private void addButtons(){
        //add a close button
        newcloseBtn = new JButton("Close");
        newcloseBtn.setBounds(800, 10, 100, 25);
        newcloseBtn.addActionListener(this);
        renewPanel.add(newcloseBtn);
        //add a view details button
        viewDetails = new JButton("View Details of this contract");
        viewDetails.setBounds(10,170 , 300, 25);
        viewDetails.addActionListener(this);
        renewPanel.add(viewDetails);
        //add a select button
        selectbtn = new JButton("Select this Contract");
        selectbtn.setBounds(10,400 , 300, 25);
        selectbtn.addActionListener(this);
        renewPanel.add(selectbtn);
        if (contractInfo.size()==0){
            viewDetails.setEnabled(false);
            selectbtn.setEnabled(false);
            warning.setText("You dont have any expired contracts");
            warning.setForeground(Color.blue);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==newcloseBtn){
            renewFrame.setVisible(false);
        }
        else  if (e.getSource()==viewDetails){
            int index= contracts.getSelectedIndex();
            message.setText(contractInfo.get(index));
            message.setForeground(Color.blue);
        }
    }
}
