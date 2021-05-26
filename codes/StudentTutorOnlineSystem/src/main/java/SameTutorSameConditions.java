import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;

public class SameTutorSameConditions extends ContractRenewal{
    private String subName, contractId,StudentID,Mons;
    private JLabel message;

    public SameTutorSameConditions(JLabel jlabel,String contract,String subname,String stuid,String numOfMons){
        subName=subname;
        contractId=contract;
        message=jlabel;
        StudentID=stuid;
        Mons=numOfMons;
        execute();

    }
    //check if the subject is same

    private void execute(){
        boolean subjectMatched=super.checkSubject(subName,contractId);
        if (subjectMatched){
            showMessage();
            if (Integer.parseInt(Mons)<3){
                showWarning("please choose number of month greater than equal 3");
            }
            prepareToStore();
        }
        else{
            showWarning("the subject of this contract is not "+subName);

        }
    }
    private void prepareToStore(){
        String secondId="";
        String firstId="";
        HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract/"+contractId, GuiAction.myApiKey);
        try {
            ObjectNode jsonNode = new ObjectMapper().readValue(contResponse.body(), ObjectNode.class);
            firstId=jsonNode.get("firstParty").get("id").asText();
            secondId=jsonNode.get("secondParty").get("id").asText();

        }
        catch (Exception e){System.out.println(e.getStackTrace()[0].getLineNumber()); }
        super.storeContract(contractId,firstId,secondId,StudentID,Mons);
    }
    @Override
    void showMessage() {
        message.setText("Contract renewal in process, waiting for the tutor");
        message.setForeground(Color.blue);
    }

    @Override
    void showWarning(String msg) {
        message.setText(msg);
        message.setForeground(Color.red);
    }
}
