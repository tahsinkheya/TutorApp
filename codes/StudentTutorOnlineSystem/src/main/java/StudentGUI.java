import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class StudentGUI {
	public JLabel name;
	public JPanel panel;
	
	public StudentGUI() {
		// Creating instance of JFrame
        JFrame frame = new JFrame("Student Homepage");
        // Setting the width and height of frame
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      
        panel = new JPanel();    
        // adding panel to frame
        frame.add(panel);
        
        panel.setLayout(null);
        
        
        // take user inputs over here
        JLabel actionLabel = new JLabel("Make a matching request");
        actionLabel.setBounds(10,50,300,25);
        panel.add(actionLabel);
        
        
        
        JLabel desc = new JLabel("Specify tutor details");
        desc.setBounds(10,70,300,25);
        desc.setForeground(Color.red);
        panel.add(desc);
        
        
        
        // Required qualification
        JLabel qualification = new JLabel("Specialisation: ");
        qualification.setBounds(10, 100, 100, 25);
        panel.add(qualification);
        
        JTextField qualText = new JTextField(20);
        qualText.setBounds(100,100,165,25);
        panel.add(qualText);
        
        // Types of qualification
        String[] qualificationTypes = {"Bachelor's Degree", "Master's Degree", "Doctoral Degree","Secondary Education"};
        JComboBox qualList = new JComboBox(qualificationTypes);
        qualList.setSelectedIndex(0);
        qualList.setBounds(300, 100, 200, 25);
        panel.add(qualList);
        
        
        // Lesson 
        JLabel lesson = new JLabel("Lesson: ");
        lesson.setBounds(10, 140, 80, 25);
        panel.add(lesson);
        
        JTextField lessText = new JTextField(20);
        lessText.setBounds(100,140,165,25);
        panel.add(lessText);
        
        
        // Time and Day 
        JLabel timeAndDay = new JLabel("Time and Day: ");
        timeAndDay.setBounds(10, 170, 100, 25);
        panel.add(timeAndDay);
        
        // Time Selection
        
        JTextField time = new JTextField(20);
        time.setBounds(100,170,50,25);
        panel.add(time);
        
        String[] allTimes = {"AM", "PM"};
        JComboBox timeList = new JComboBox(allTimes);
        timeList.setBounds(150, 170, 70, 25);
        panel.add(timeList);
        
        // Day selection
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        JComboBox daysBox = new JComboBox(days);
        daysBox.setSelectedIndex(0);
        daysBox.setBounds(250, 170, 200, 25);
        panel.add(daysBox);
        
        // Weekly Sessions
        JLabel session = new JLabel("Weekly Sessions: ");
        session.setBounds(10, 200, 200, 25);
        panel.add(session);
        
        NumberFormat amountFormat = null;
        int num = 2;
        JFormattedTextField sessionIn= new JFormattedTextField(amountFormat);
        sessionIn.setValue(num);
        sessionIn.setBounds(150,200,50,25);
        panel.add(sessionIn);
        
        
        // Payment Rate
     
        JLabel rate = new JLabel("Rate (RM): ");
        rate.setBounds(10, 240, 100, 25);
        panel.add(rate);
        
        JTextField rateIn = new JTextField(20);
        rateIn.setBounds(80, 240, 80, 25);
        panel.add(rateIn);
        
        String[] rateTypes = {"per hour", "per session"};
        JComboBox allRates = new JComboBox(rateTypes);
        allRates.setBounds(180, 240, 100, 25);
        panel.add(allRates);
        
        
        

        // Creating request button
        
        JButton submitButton = new JButton("Make Request");
        submitButton.setBounds(10, 270, 120, 25);
        panel.add(submitButton);
        
        // Setting the frame visibility to true
        frame.setVisible(true);
	}
	

}


