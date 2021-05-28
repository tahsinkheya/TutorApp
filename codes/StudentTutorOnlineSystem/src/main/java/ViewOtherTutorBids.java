import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Vector;
/*a class to view other tutor bid info to tutors abt a bid*/
public class ViewOtherTutorBids implements ActionListener {
    private String bidId;
    private String userId;

    private JFrame newFrame;
    private JButton closeBtn,offerView;
    private JLabel newWarning;
    private Vector newComboBoxItems=new Vector();
    private static JComboBox moreOffers;
    private JTextArea offerDetails;
    private ArrayList<String> tutorids = new ArrayList<String>();
    private ArrayList<String> offerInfo = new ArrayList<String>();


    public ViewOtherTutorBids(String bidid, String userid) {
        bidId=bidid;
        userId=userid;
        showUI();
    }
    //getter methods for instance variable
    private String getBidId(){return bidId;}
    private String getuserId(){return userId;}
    //get offers to a bid if the tutor id is not same
    private void getOffer(){
        // get all  messages of the bid
        String endpoint="bid/"+getBidId()+"?fields=messages";
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);

        try {
            ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
            for (JsonNode msgNode : userNode.get("messages")) {
                if (userNode.get("additionalInfo").toString().equals("{}")==false){
                    //get all details
                    String  msgSender = msgNode.get("poster").get("userName").toString();
                    String  msgSenderId = GuiAction.removeQuotations(msgNode.get("poster").get("id").toString());
                    //check thats its not this tutor
                    if (msgSenderId.contains(getuserId())==false) {
                        //check if there is any other offer from this tutor
                        int occurrences = 0;
                        if (tutorids.size() != 0) {
                            occurrences = (int) tutorids.stream().filter(tutor -> msgSenderId.equals(tutor)).count();
                        }
                        tutorids.add(msgSenderId);


                        String duration = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("duration").toString());
                        String rate = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("rate").toString());
                        String numberOfSession = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("numberOfSession").toString());
                        String freelesson = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("freeLesson").toString());
                        String competency = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("tutorComp").toString());
                        String extra = (msgNode.get("additionalInfo").get("extraInfo").toString());

                        String tutor = GuiAction.removeQuotations(msgNode.get("poster").get("givenName").toString()) + " " + GuiAction.removeQuotations(msgNode.get("poster").get("familyName").toString());
                        //since a tutor can bid multiple time we add a counter if its more than one in the option
                        String option = "";
                        if (occurrences != 0) {
                            option += (occurrences + 1) + " ";
                        }

                        newComboBoxItems.add(option + "from: " + GuiAction.removeQuotations((msgSender)));
                        String newOffer="By "+tutor+"\n" +"Duration Offered:"+duration+" hrs per lesson"+"\n"+
                                "Rate:"+rate+"\n"+"Number of weekly session:"+numberOfSession+"\n"+
                                "a fress lesson was offered:"+freelesson+"\n"+
                                "extra information from the tutor:"+extra+"\n"
                                +"Tutor competency in this subject:"+competency;

                        offerInfo.add(newOffer);
                    }
//
                }
            }

        }
        catch(Exception e) {
            System.out.println(e.getCause());
            System.out.println(e.getStackTrace()[0].getLineNumber());

        }
    }
//method to show the ui to the tutors
    private void showUI(){
        //get all offers and put it in an arraylist
        getOffer();
        //show new frame
        newFrame = new JFrame("View Other Tutor Offer Details");
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
        JLabel actionLabel = new JLabel("View Offer Details");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);


        JLabel instruction = new JLabel("Select an offer and then click on view details");
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


        newWarning=new JLabel();
        newWarning.setBounds(10,210,700,25);
        panel.add(newWarning);



        newFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==closeBtn){
            newFrame.setVisible(false);
        }
        else if (e.getSource()==offerView){
            if (offerInfo.size()==0){
                newWarning.setText("no other tutor has offered a bid yet");
                newWarning.setForeground(Color.RED);
            }
            else {
                int index = moreOffers.getSelectedIndex();
                String output=offerInfo.get(index);
                offerDetails.setText(output);
                offerDetails.setForeground(Color.blue);
            }

        }
    }
}
