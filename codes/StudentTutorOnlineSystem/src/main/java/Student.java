
/**
 * 
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Rafaquat
 *
 */
public class Student implements User, ActionListener {
	private static StudentGUI studentGUI= new StudentGUI();
	String userName;
	String givenName;
	String familyName;
	GUIcontext context;
	String userId;


	//ui components
	private JButton requestTbutton, viewCbutton, ViewBbutton;
	private JPanel homepagePanel;
	private  JLabel welcome;



	//StudentGUI studentGUI;
	

	@Override
	public boolean signContract() {
		return false;
	}

	@Override
	public void create(String uName, String gName, String fName,String uId) {
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

			homepagePanel= new JPanel();
			homeFrame.add(homepagePanel);
			homepagePanel.setBackground(new Color(172, 209, 233));
			homepagePanel.setLayout(null);

			welcome = new JLabel("Welcome:"+userName);
			welcome.setBounds(100,50,400,25);
			homepagePanel.add(welcome);

			requestTbutton = new JButton("Request Tutor");
			requestTbutton.setBounds(100, 100, 600, 25);
			requestTbutton.addActionListener(this);
			homepagePanel.add(requestTbutton);

			viewCbutton = new JButton("View Contracts");
			viewCbutton.setBounds(100, 200, 600, 25);
			viewCbutton.addActionListener(this);
			homepagePanel.add(viewCbutton);

			ViewBbutton = new JButton("View Requests and their Bids");
			ViewBbutton.setBounds(100, 300, 600, 25);
			ViewBbutton.addActionListener(this);
			homepagePanel.add(ViewBbutton);

			homeFrame.setVisible(true);

	}

	static void showAllRequests(){
		studentGUI.showAllRequests();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == requestTbutton){
			//show make request page
			context= new GUIcontext(new createRequestAction(userId,givenName,familyName));
			context.showUI();
		}
		else if (e.getSource()==viewCbutton){
			//show all contracts page
			//context=new GUIcontext(new createContractAction())

		}
		else if (e.getSource()==ViewBbutton){
			//show bids of requests made
			System.out.println("3");

		}

	}
}