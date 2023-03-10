import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

/**
 * Base class for making offer to open bids for tutors.
 */

public class MakeOpenBidOffer implements ActionListener {
    private JFrame frame;
    private JTextField rateOffered,durationOffered,sessionOffered,extraInfo;
    private JComboBox allRates, lessonInfo;
    private JButton makeOffer,closeBtn;
    private JLabel warning;
    private String bidid,userId,tutorQ;
    private int tutorComp;

    /**constructor
     *
     */

    public  MakeOpenBidOffer(String bidId,String userid,int comp,String tutorQuali){
        bidid=bidId;
        userId=userid;
        tutorComp=comp;
        tutorQ=tutorQuali;
        showUI();



    }
    //method to shoe the UI
    private void showUI(){
        frame = new JFrame();
        // Setting the width and height of frame
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel= new JPanel();
        frame.add(panel);
        panel.setBackground(new Color(172, 209, 233));
        panel.setLayout(null);

        JLabel title = new JLabel("Offer a Bid");
        title.setBounds(350,10,300,25);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(title);

        // rate the tutor wants to oofer
        JLabel rateText = new JLabel("Rate (RM) you are willing to charge: ");
        rateText.setBounds(30, 70, 400, 25);
        panel.add(rateText);

        rateOffered = new JTextField(20);
        rateOffered.setBounds(30,100,50,25);
        panel.add(rateOffered);

        String[] rateTypes = {"per hour", "per session"};
        allRates = new JComboBox(rateTypes);
        allRates.setBounds(100, 100, 100, 25);
        panel.add(allRates);

        //duration
        JLabel duration = new JLabel("Duration of lesson (hours): ");
        duration.setBounds(30, 130, 400, 25);
        panel.add(duration);

        durationOffered = new JTextField(20);
        durationOffered.setBounds(30,160,50,25);
        panel.add(durationOffered);

        //num of session
        JLabel num = new JLabel("Number of session per week: ");
        num.setBounds(30, 190, 200, 25);
        panel.add(num);

        sessionOffered = new JTextField(20);
        sessionOffered.setBounds(30,220,50,25);
        panel.add(sessionOffered);

        //option free lessom
        JLabel question = new JLabel("Are you willing to provide 1 free lesson? ");
        question.setBounds(30, 250, 300, 25);
        panel.add(question);

        String[] lessonType = {"No","Yes"};
        lessonInfo = new JComboBox(lessonType);
        lessonInfo.setBounds(30, 280, 100, 25);
        panel.add(lessonInfo);

        //extra info u want to add
        JLabel info = new JLabel("Extra information you want to add");
        info.setBounds(30, 310, 750, 25);
        panel.add(info);
        extraInfo = new JTextField(100);
        extraInfo.setBounds(30,340,100,25);
        panel.add(extraInfo);
        //add a button
        makeOffer = new JButton("Confirm");
        makeOffer.setBounds(30, 380, 180, 25);
        makeOffer.addActionListener(this);
        panel.add(makeOffer);
        //add a close button
        closeBtn = new JButton("Close");
        closeBtn.setBounds(800, 10, 100, 25);
        closeBtn.addActionListener(this);
        panel.add(closeBtn);


        warning = new JLabel();
        warning.setBounds(30, 410, 750, 25);
        panel.add(warning);


        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==makeOffer){
            //get values
            String rate=rateOffered.getText();
            String duration=durationOffered.getText();
            String sess=sessionOffered.getText();
            String extra=extraInfo.getText();
            String rateType=allRates.getSelectedItem().toString();
            String freeLess=lessonInfo.getSelectedItem().toString();
            if (rate.equals("") || duration.equals("") || sess.equals("")){
                warning.setText("Please fill in the details about rate, duration and session");
            }
            else{
                // create the message object
                createMessage(rate,rateType,duration,sess,extra,freeLess);
            }
        }
        else if (e.getSource()==closeBtn){
            //close the window
            frame.setVisible(false);
        }
    }

    private void createMessage(String rate,String rateType,String duration,String sess,String extra,String freeLess){
        String jsonString ;
        JSONObject msgInfo=new JSONObject();
        System.out.println(bidid);
        msgInfo.put("bidId", bidid);
        msgInfo.put("posterId", userId);
        msgInfo.put("datePosted", new Date().toInstant().toString());
        msgInfo.put("content", "an open bid offer");
        JSONObject additionalInfo=new JSONObject();
        additionalInfo.put("rate","RM:"+rate+" "+rateType);
        additionalInfo.put("duration",duration);
        additionalInfo.put("numberOfSession",sess);
        additionalInfo.put("extraInfo",extra);
        additionalInfo.put("freeLesson",freeLess);
        additionalInfo.put("tutorComp",String.valueOf(tutorComp));
        additionalInfo.put("tutorQualification",tutorQ);
        msgInfo.put("additionalInfo", additionalInfo);

        // convert message to JSON string
        jsonString = msgInfo.toString();
        postMessage("message",jsonString);
    }


    /* Method to create a new class instances in db
     * This is to store the message in the database */
    private void postMessage(String endpoint, String jsonString) {
        // create a new message in the database
        HttpResponse<String> postResponse = GuiAction.updateWebApi(endpoint,GuiAction.myApiKey,jsonString);
            if (postResponse.statusCode()==201){
                warning.setText("Your offer has been saved");
            }
            else{
                warning.setText("there was an error when saving ur response,please try again");
            }


    }

}

