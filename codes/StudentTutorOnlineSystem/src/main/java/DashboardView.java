import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DashboardView {
    private String userId;
    private JButton viewDetails, reviseBid,viewOffer;
    private JComboBox allBids,allOffers;
    private JFrame frame;
    private JPanel panel;
    private JLabel warning;
    private boolean noBids=false;
    private boolean noOffers=false;
    private JTextArea info;
    private JLabel refreshInfo;
    public DashboardView(String uId){
        userId=uId;
    }
    public void setButtons(JButton details,JButton revise,JButton offer,JLabel refresh){
        viewDetails=details;
        reviseBid=revise;
        viewOffer=offer;
        refreshInfo=refresh;
    }
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
    }
    public void showAllOffers(){
        allOffers.setBounds(10, 220, 700, 25);
        panel.add(allOffers);

        reviseBid.setEnabled(true);
        viewDetails.setEnabled(true);

        if (noOffers){
            showMessage("this bid has no offers yet","red");
            viewDetails.setEnabled(false);
        }
        frame.setVisible(true);

    }
    public void displayOffer(String offerInfo){
        info.setText(offerInfo);
        info.setForeground(Color.blue);
    }

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
        panel.add(viewDetails);

        JLabel instruction = new JLabel("Select a request and then click on view details");
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

    private void showMessage(String msg,String color){
        warning.setText(msg);
        if (color.contains("blue")){
             warning.setForeground(Color.BLUE);}
        else{warning.setForeground(Color.red);}
    }

}
