import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
/*a class used by Tutor to view close bid request made by student and send message to the student*/
public class CloseBidAction extends BidAction implements ActionListener {
    private String bidId,userId,userFullName,studentId;
    private JLabel warning;
    private ArrayList<String> bidInfo;
    private JFrame frame;
    private JPanel panel;
    private JTextArea message;
    private JButton sendMessage,closeBtn;

    public CloseBidAction(String bidid, String uId,String fullName,String studentid){
        bidId=bidid;
        userId=uId;
        userFullName=fullName;
        studentId=studentid;
        showUI();
    }
//method to show ui
    private void showUI(){
        bidInfo=getBidInfo(bidId);

        // Creating instance of JFrame
        frame = new JFrame("Close Bid Request");
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

        //show details
        JLabel subName=new JLabel("subject Name: "+bidInfo.get(0) );
        subName.setBounds(10,50,340,25);
        panel.add(subName);

        JLabel subDesc=new JLabel("subject Description: "+bidInfo.get(1) );
        subDesc.setBounds(10,80,340,25);
        panel.add(subDesc);

        JLabel requiredComp=new JLabel("Required Competency: "+bidInfo.get(2) );
        requiredComp.setBounds(10,110,340,25);
        panel.add(requiredComp);

        JLabel weekSess=new JLabel("Number of session per week: "+bidInfo.get(3) );
        weekSess.setBounds(10,140,340,25);
        panel.add(weekSess);

        JLabel Hlp=new JLabel("Hours per Lesson: "+bidInfo.get(4) );
        Hlp.setBounds(10,170,340,25);
        panel.add(Hlp);

        JLabel rate=new JLabel("Rate: "+bidInfo.get(5) );
        rate.setBounds(10,200,340,25);
        panel.add(rate);

        JLabel instruction=new JLabel("If you want to bid on this request.Please write a message to the requester");
        instruction.setBounds(10,230,500,25);
        instruction.setForeground(Color.RED);
        panel.add(instruction);

        message = new JTextArea();
        message.setBounds(10,260,340,100);
        panel.add(message);

        sendMessage = new JButton("Send Message");
        sendMessage.setBounds(10, 370, 300, 25);
        sendMessage.addActionListener(this);
        panel.add(sendMessage);

        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        panel.add(closeBtn);

        //add a label to show warning later
        warning=new JLabel();
        warning.setBounds(10,400,340,25);
        panel.add(warning);

        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==closeBtn){
            frame.setVisible(false);
        }
        else if (e.getSource()==sendMessage){
            String msg=message.getText();
            if (msg.equals("")){
                warning.setText("Please fill in details about your offer in the text field above");
                warning.setForeground(Color.RED);
            }
            else{
                storeMessage(msg);
            }
        }
    }
//methos to store message by tutor to student
    private void storeMessage(String msg){
        String jsonString = null;
        // create the message object
        JSONObject msgInfo=new JSONObject();
        msgInfo.put("bidId", bidId);
        msgInfo.put("posterId", userId);
        msgInfo.put("datePosted", new Date().toInstant().toString());
        msgInfo.put("content", userFullName+": "+msg);
        JSONObject additionalInfo=new JSONObject();
        additionalInfo.put("to",studentId);
        msgInfo.put("additionalInfo", additionalInfo);

        // convert message to JSON string
        jsonString = msgInfo.toString();
        HttpResponse<String> userResponse=GuiAction.updateWebApi("message",myApiKey,jsonString);

        warning.setText("your response has been saved successfully");
    }
}
