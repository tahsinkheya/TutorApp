import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

public class DifferentTutorSameConditions extends ContractRenewal implements ActionListener {
    private String subName,contractId,StudentID,Mons,requiredComp;
    private JLabel message,warning;
    private JFrame previousFrame, currentFrame;
    private JTextField username;
    private JButton button;

    public DifferentTutorSameConditions(JLabel jlabel, String contract, String subname, String stuid, String numOfMons,JFrame previousF,String rcomp){
        subName=subname;
        contractId=contract;
        message=jlabel;
        StudentID=stuid;
        Mons=numOfMons;
        previousFrame=previousF;
        requiredComp=rcomp;
        execute();

    }

    private void execute(){
        boolean subjectMatched=super.checkSubject(subName,contractId);
        if (subjectMatched){
            if (Integer.parseInt(Mons)<3){
                showWarning("please choose number of month greater than equal 3");
            }
            else{
                showMessage();
            }

        }
        else{
            showWarning("the subject of this contract is not "+subName);

        }
    }

    @Override
    void showMessage() {
        previousFrame.setVisible(false);
        //add a frame
        currentFrame=new JFrame();
        currentFrame.setSize(350,250);
        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add a panel and title
        JPanel panel= new JPanel();
        currentFrame.add(panel);
        panel.setBackground(new Color(172, 209, 233));
        panel.setLayout(null);
        JLabel user = new JLabel("Username of the tutor you want to select");
        user.setBounds(30,20,300,25);
        panel.add(user);

        username=new JTextField(20);
        username.setBounds(100,50,165,25);
        panel.add(username);

         button= new JButton("Select");
        button.setBounds(130,80,80,25);
        button.addActionListener(this);
        panel.add(button);

        warning = new JLabel("");
        warning.setBounds(30,110,300,25);
        panel.add(warning);

        currentFrame.setVisible(true);
    }

    @Override
    void showWarning(String msg) {
        message.setText(msg);
        message.setForeground(Color.red);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==button){
            checkTutor(username.getText());
        }
    }

    private void checkTutor(String name){
        boolean found=false;
        String tutorId="";
        HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("user", GuiAction.myApiKey);
        try {
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(contResponse.body(), ObjectNode[].class);

            for (ObjectNode node: jsonNodes) {
                if (node.get("userName").textValue().contains(name)){
                    tutorId=node.get("id").textValue();
                    found=true;
                }
            }
        }catch (Exception e){System.out.println(e.getStackTrace()[0].getLineNumber()); }
        if (found==true){
            int comp=GuiAction.getTuteComp(subName,tutorId);
            if (comp>=Integer.parseInt(requiredComp)+2){
                tutorWarning("contract creation in process waiting for the tutor",Color.BLUE);
                super.storeContract(contractId,tutorId,StudentID,StudentID,Mons);
            }
            else{
                tutorWarning("this tutor is doesnt fullfill the required competency",Color.RED);
            }
        }
        else{tutorWarning("couldnt find any tutor with this username",Color.RED);}




    }

    private void tutorWarning(String msg,Color color){
        warning.setText(msg);
        warning.setForeground(color);
    }
}
