import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;
// a class that renews contract witht the same tutor as the contract with same terms and conds
public class SameTutorSameConditions extends ContractRenewal{
    private String subName, contractId,StudentID,Mons;
    private JLabel message;
//constructor
    public SameTutorSameConditions(JLabel jlabel,String contract,String subname,String stuid,String numOfMons){
        subName=subname;
        contractId=contract;
        message=jlabel;
        StudentID=stuid;
        Mons=numOfMons;
        execute();

    }
    //check if the subject is same and call helper methods to implment the feature
    private void execute(){
        boolean subjectMatched=super.checkSubject(subName,contractId);
        if (subjectMatched){
            showMessage();
            if (Integer.parseInt(Mons)<3){
                showWarning("please choose number of month greater than equal 3");
            }
            else{
                prepareToStore();}
        }
        else{
            showWarning("the subject of this contract is not "+subName);

        }
    }
    // a method that finds tutor id and calls the super class to create new contract
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
    //a method to show message to user
    @Override
    void showMessage() {
        message.setText("Contract renewal in process, waiting for the tutor");
        message.setForeground(Color.blue);
    }
// a methods to show warning to user
    @Override
    void showWarning(String msg) {
        message.setText(msg);
        message.setForeground(Color.red);
    }
}
