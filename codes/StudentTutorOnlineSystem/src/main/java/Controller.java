import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Controller implements ActionListener, Observer {
    private TutorBidModel model;
    private DashboardView view;
    private  String userId;
    private JButton vieDetails=new JButton("View Details");
    private JButton ReviseOffer=new JButton("Revise/Add My Bid Offer");
    private JButton viewOffer=new JButton("View offers for this bid");
    private JComboBox theBids,theOffers;
    private JLabel refreshMessage=new JLabel("");

    public Controller(DashboardView newView,String tutorId){
        view=newView;
        userId=tutorId;
        //initialize model
        model=new TutorBidModel(userId);
        model.registerObserver(this);
        //setbuttons in view
        vieDetails.addActionListener(this);
        ReviseOffer.addActionListener(this);
        viewOffer.addActionListener(this);
        view.setButtons(vieDetails,ReviseOffer,viewOffer,refreshMessage);
        showallOffers();
    }

    private void showallOffers(){
        final DefaultComboBoxModel offers = new DefaultComboBoxModel(model.getBidList());
        theBids=new JComboBox(offers);
        view.setBidComboBoxes(theBids);
        view.showTutorBids();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==viewOffer){
            int index=theBids.getSelectedIndex();
            showBidOffers(index);
        }
        else if (e.getSource()==vieDetails){
            int index=theOffers.getSelectedIndex();
            showOfferInfo(index);
        }
    }
    private void showBidOffers(int index){
        System.out.println(index);
        System.out.println(model.getBidOffers(index));
        if (theOffers != null){theOffers.removeAllItems();}
        ArrayList<String> offers = model.getBidOffers(index);


        theOffers=new JComboBox(offers.toArray());
        view.setOfferComboBox(theOffers);
        view.showAllOffers();
    }
    private void showOfferInfo(int index){
        String output=model.getBidOfferInfo(index);
        view.displayOffer(output);
    }
    @Override
    public void update() {
        refreshMessage.setText("refreshing..");
        int index=theBids.getSelectedIndex();
        showBidOffers(index);
        refreshMessage.setText("");

    }


}
