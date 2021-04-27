import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tutor implements  User, ActionListener {
    String userName;
    String givenName;
    String familyName;
    String userId;

    GUIcontext context;

    private JPanel homepage;
    private JLabel welcome;
    private JButton viewContract,viewRequest; 	//Btn


    @Override
    public boolean signContract() {
        return false;
    }

    @Override
    public void create(String uName, String gName,String fName,String uId) {
        this.userName=uName;
        this.givenName=gName;
        this.familyName=fName;
        this.userId=uId;
    }

    @Override
    public void showHomePage() {
        JFrame homeFrame = new JFrame();
        // Setting the width and height of frame
        homeFrame.setSize(900, 500);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homepage= new JPanel();
        homeFrame.add(homepage);
        homepage.setBackground(new Color(172, 209, 233));
        homepage.setLayout(null);

        welcome = new JLabel("Welcome:"+userName);
        welcome.setBounds(100,50,400,25);
        homepage.add(welcome);

        viewRequest = new JButton("View Student Requests");
        viewRequest.setBounds(100, 100, 600, 25);
        viewRequest.addActionListener(this);
        homepage.add(viewRequest);

        viewContract = new JButton("View Contracts");
        viewContract.setBounds(100, 200, 600, 25);
        viewContract.addActionListener(this);
        homepage.add(viewContract);

        homeFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==viewContract){
           // context=new GUIcontext(new createContractAction())
        }
        else if (e.getSource()==viewRequest){
            context= new GUIcontext(new ViewRequestAction());
            context.showUI();
            //TutorGUI t=new TutorGUI();
        }

    }
}
