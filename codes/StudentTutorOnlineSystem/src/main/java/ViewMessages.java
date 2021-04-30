import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Vector;

public class ViewMessages implements ActionListener {
    private String bidId,userId;
    private JFrame newFrame;
    private JButton closeBtn,offerView,sendMsg;
    private Vector newComboBoxItems=new Vector();
    private  JComboBox moreOffers;
    private JTextArea offerDetails;
    private JLabel warning;
    private ArrayList<String> senders = new ArrayList<String>();


    public ViewMessages(String bidid,String userid) {
        bidId=bidid;
        userId=userid;
        showSenders();
    }
    /*method to get all tutor who sent messages*/
    private void getAllSenders(){
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("message",GuiAction.myApiKey);
        try{
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                String bid=node.get("bidId").toString();
                if (bid.contains(bidId)) {
                    String username = node.get("poster").get("userName").asText();
                    String tutorId=node.get("poster").get("id").asText();
                    if (senders.contains(tutorId) == false) {
                        senders.add(tutorId);
                        newComboBoxItems.add("from: " + username);
                        System.out.println(username);

                    }
                }
            }

        }
        catch(Exception e){

        }
    }
    private void showSenders(){
        getAllSenders();

        //show new frame
        newFrame = new JFrame("View Offer Details");
        // Setting the width and height of frame
        newFrame.setSize(900, 700);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        // adding panel to frame
        newFrame.add(panel);
        panel.setLayout(null);
        panel.setBackground(new Color(172, 209, 233));

        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        panel.add(closeBtn);

        //title
        JLabel actionLabel = new JLabel("View Offer Messages");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select an offer and then click on view messages");
        instruction.setBounds(10,80,1200,25);
        panel.add(instruction);


        final DefaultComboBoxModel model = new DefaultComboBoxModel(newComboBoxItems);

        moreOffers = new JComboBox(model);
        moreOffers.setBounds(10, 120, 700, 25);
        panel.add(moreOffers);


        offerView = new JButton("View details");
        offerView.setBounds(10, 180, 180, 25);
        offerView.addActionListener(this);
        panel.add(offerView);

        offerDetails=new JTextArea();
        offerDetails.setBounds(50,240,700,230);
        panel.add(offerDetails);


        warning=new JLabel();
        warning.setBounds(10,210,700,25);
        panel.add(warning);



        sendMsg = new JButton("Send Message");
        sendMsg.setBounds(10, 480, 300, 25);
        sendMsg.addActionListener(this);
        panel.add(sendMsg);

        newFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
