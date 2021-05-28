import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
//a view class for implementing the mvc pattern
public class DashboardView implements ActionListener {
    private String userId;
    private int index;
    private TutorBidModel model;
    private JButton viewDetails, reviseBid,viewOffer,makeOffer,closeBtn;
    private JComboBox allBids,allOffers,allRates,lessonInfo;
    private JFrame frame,newFrame;
    private JPanel panel;
    private JLabel warning,message;
    private boolean noBids=false;
    private boolean noOffers=false;
    private JTextArea info;
    private JLabel refreshInfo;
    private JTextField rateOffered,durationOffered,sessionOffered,extraInfo;
    //constructor
    public DashboardView(String uId){
        userId=uId;
    }
    //setter for buttons
    public void setButtons(JButton details,JButton revise,JButton offer,JLabel refresh){
        viewDetails=details;
        reviseBid=revise;
        viewOffer=offer;
        refreshInfo=refresh;
    }
    //setters for comboboxes
    public void setBidComboBoxes(JComboBox bid ){
        allBids=bid;
        if (bid.getSelectedItem() == null ){
            noBids=true;
        }

    }
    public void setOfferComboBox(JComboBox offers){
        allOffers=offers;
        if (offers.getSelectedItem() == null ){
            noOffers=true;
        }
        else{noOffers=false;}
    }
    //setter for model
    public  void setModel(TutorBidModel newModel){
        model=newModel;
    }
    //method to show all bids
    public void showAllOffers(){
        allOffers.setBounds(10, 220, 700, 25);
        panel.add(allOffers);

        reviseBid.setEnabled(true);
        viewDetails.setEnabled(true);

        if (noOffers){
            showMessage("this bid has no offers yet","red");
            viewDetails.setEnabled(false);
        }

        frame.revalidate();
        frame.repaint();


    }
    //method to display a particular offer
    public void displayOffer(String offerInfo){
        info.setText(offerInfo);
        info.setForeground(Color.blue);
    }
// method to show bids
    public void showTutorBids(){
        // Creating instance of JFrame
        frame = new JFrame("Tutor Dashboard");
        // Setting the width and height of frame
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);
        panel.setBackground(new Color(172, 209, 233));
        // show title
        JLabel relListTitle = new JLabel("All bids subscribed to");
        relListTitle.setBounds(10,50,450,25);
        panel.add(relListTitle);
        viewDetails.setBounds(10,280 , 180, 25);
        viewDetails.setEnabled(false);
        panel.add(viewDetails);

        JLabel instruction = new JLabel("Select a request and then click on view offers for this bid then select an offer and click on view details");
        instruction.setBounds(10,80,1200,25);
        instruction.setForeground(Color.blue);
        panel.add(instruction);

        warning = new JLabel("");
        warning.setBounds(10,100,1200,25);
        panel.add(warning);

        allBids.setBounds(10, 120, 700, 25);
        panel.add(allBids);

        refreshInfo.setBounds(780, 10, 700, 25);
        panel.add(refreshInfo);

        viewOffer.setBounds(10, 180, 180, 25);
        panel.add(viewOffer);
        if (noBids){
            showMessage("you haven't subscribed to any bids","red");
            viewOffer.setEnabled(false);
        }
        info = new JTextArea("this is where bid offer detail is "+"\n"+"displayed after clicking on"+"\n"+" view details");
        info.setBounds(10,310,250,150);
        info.setEditable(false);
        panel.add(info);

        reviseBid.setBounds(10,470,180,25);
        panel.add(reviseBid);
        reviseBid.setEnabled(false);
        frame.setVisible(true);
    }
//method to shhoe message
    private void showMessage(String msg,String color){
        warning.setText(msg);
        if (color.contains("blue")){
             warning.setForeground(Color.BLUE);}
        else{warning.setForeground(Color.red);}
    }
//method to take details abt the current tutor's offer
    public void showTakeOffer(int indexOfBid){
        index=indexOfBid;
        newFrame = new JFrame();
        // Setting the width and height of frame
        newFrame.setSize(900, 600);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel newPanel= new JPanel();
        newFrame.add(newPanel);
        newPanel.setBackground(new Color(172, 209, 233));
        newPanel.setLayout(null);

        JLabel title = new JLabel("Offer a Bid");
        title.setBounds(350,10,300,25);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        newPanel.add(title);

        // rate the tutor wants to oofer
        JLabel rateText = new JLabel("Rate (RM) you are willing to charge: ");
        rateText.setBounds(30, 70, 400, 25);
        newPanel.add(rateText);

        rateOffered = new JTextField(20);
        rateOffered.setBounds(30,100,50,25);
        newPanel.add(rateOffered);

        String[] rateTypes = {"per hour", "per session"};
        allRates = new JComboBox(rateTypes);
        allRates.setBounds(100, 100, 100, 25);
        newPanel.add(allRates);

        //duration
        JLabel duration = new JLabel("Duration of lesson (hours): ");
        duration.setBounds(30, 130, 400, 25);
        newPanel.add(duration);

        durationOffered = new JTextField(20);
        durationOffered.setBounds(30,160,50,25);
        newPanel.add(durationOffered);

        //num of session
        JLabel num = new JLabel("Number of session per week: ");
        num.setBounds(30, 190, 200, 25);
        newPanel.add(num);

        sessionOffered = new JTextField(20);
        sessionOffered.setBounds(30,220,50,25);
        newPanel.add(sessionOffered);

        //option free lessom
        JLabel question = new JLabel("Are you willing to provide 1 free lesson? ");
        question.setBounds(30, 250, 300, 25);
        newPanel.add(question);

        String[] lessonType = {"No","Yes"};
        lessonInfo = new JComboBox(lessonType);
        lessonInfo.setBounds(30, 280, 100, 25);
        newPanel.add(lessonInfo);

        //extra info u want to add
        JLabel info = new JLabel("Extra information you want to add");
        info.setBounds(30, 310, 750, 25);
        newPanel.add(info);
        extraInfo = new JTextField(100);
        extraInfo.setBounds(30,340,100,25);
        newPanel.add(extraInfo);
        //add a button
        makeOffer = new JButton("Confirm");
        makeOffer.setBounds(30, 380, 180, 25);
        makeOffer.addActionListener(this);
        newPanel.add(makeOffer);
        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        newPanel.add(closeBtn);

        message=new JLabel();
        message.setBounds(30,410,180,25);
        newPanel.add(message);

        newFrame.setVisible(true);
    }
    //method to show competetncy warning
    public void showCompWarning(){
        JLabel compWarning = new JLabel("You dont have the required competency to offer a bid");
        compWarning.setBounds(10,490,1200,25);
        compWarning.setForeground(Color.red);
        panel.add(compWarning);
        frame.repaint();
        frame.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==makeOffer){
            message.setText("Your offer is saved");
            message.setForeground(Color.blue);
            JSONObject additionalInfo=new JSONObject();
            additionalInfo.put("rate","RM:"+rateOffered.getText()+" "+allRates.getSelectedItem().toString());
            additionalInfo.put("duration",durationOffered.getText());
            additionalInfo.put("numberOfSession",sessionOffered.getText());
            additionalInfo.put("extraInfo",extraInfo.getText());
            additionalInfo.put("freeLesson",lessonInfo.getSelectedItem().toString());
            model.CreateMessage(index,additionalInfo);
        }
        else{
            newFrame.setVisible(false);
        }
    }
}
