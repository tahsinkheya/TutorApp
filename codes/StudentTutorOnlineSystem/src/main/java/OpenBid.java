import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OpenBid extends BidAction implements ActionListener {
    private String bidid;

    private JPanel panel;
    private JLabel subName,subDesc,requiredComp,weekSess,Hlp,rate;
    private JButton viewOtherBids,makeBidOffer;
    public OpenBid(String bidId){
        bidid=bidId;
        showUI();
    }

    private void showUI(){
        ArrayList<String> bidInfo=getBidInfo(bidid);

        // Creating instance of JFrame
        JFrame frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        panel = new JPanel();
        panel.setBackground(new Color(172, 209, 233));

        // adding panel to frame
        frame.add(panel);
        panel.setLayout(null);

        // take user inputs over here
        JLabel actionLabel = new JLabel("Bid Details");
        actionLabel.setBounds(350,10,300,25);
        actionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(actionLabel);



        subName=new JLabel("subject Name: "+bidInfo.get(0) );
        subName.setBounds(10,50,340,25);
        panel.add(subName);

        subDesc=new JLabel("subject Description: "+bidInfo.get(1) );
        subDesc.setBounds(10,80,340,25);
        panel.add(subDesc);

        requiredComp=new JLabel("Required Competency: "+bidInfo.get(2) );
        requiredComp.setBounds(10,110,340,25);
        panel.add(requiredComp);

        weekSess=new JLabel("Number of session per week: "+bidInfo.get(3) );
        weekSess.setBounds(10,140,340,25);
        panel.add(weekSess);

        Hlp=new JLabel("Hours per Lesson: "+bidInfo.get(4) );
        Hlp.setBounds(10,170,340,25);
        panel.add(Hlp);

        rate=new JLabel("Rate: "+bidInfo.get(5) );
        rate.setBounds(10,200,340,25);
        panel.add(rate);

        viewOtherBids = new JButton("View other offers to this request");
        viewOtherBids.setBounds(10, 230, 300, 25);
        viewOtherBids.addActionListener(this);
        panel.add(viewOtherBids);

        makeBidOffer = new JButton("Make an offer");
        makeBidOffer.setBounds(10, 270, 300, 25);
        makeBidOffer.addActionListener(this);
        panel.add(makeBidOffer);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==makeBidOffer){
            System.out.println("make");
        }
        else if (e.getSource()==viewOtherBids){
            System.out.println("view");
        }
    }
}