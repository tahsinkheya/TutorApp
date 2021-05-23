import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

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
        view.setModel(model);
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
        else if (e.getSource()==ReviseOffer){
            //check with model if tutor is competent
            boolean tutorCanBid=model.checkTutorComp(theBids.getSelectedIndex());

            if (tutorCanBid){
                view.showTakeOffer(theBids.getSelectedIndex());
            }
            else{view.showCompWarning();}
        }
    }
    private void showBidOffers(int index){

        ArrayList<String> offers = model.getBidOffers(index);
        System.out.println(offers);

        if (theOffers != null) {

            //when it is called by the update method we need to check if comboxitems need updating so we will check the size
            if (theOffers.getModel().getSize() != offers.size()) {
                theOffers.removeAllItems();
                for (int i = 0; i < offers.size(); i++) {
                    theOffers.addItem(offers.get(i));
                    //theOffers.add("hd");

                }

            }
        }
        else{
            theOffers=new JComboBox(offers.toArray());
        }


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
