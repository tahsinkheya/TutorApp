import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/*class to createcontract, */
public class createContractAction implements GuiAction {
    // this list contains(subname, subdesc,competency,weekly sess, hours per lesson, rate,tutorqualification)
    private ArrayList<String> contractDetails=new ArrayList<>();
    private String studentId,firstPartySigned,bidId;
    private OpenBidOffer acceptedOffer;
    private String contractExpiryDate;



    /*constructor for student/tutor to use to create a contract when selecting a tutor our buying out bid
    or automatic tutor slection* */
    public createContractAction(OpenBidOffer offer,String fps,String stuId,String bidid, String contExpiryDate){
        acceptedOffer=offer;
        firstPartySigned=fps;
        studentId=stuId;
        bidId=bidid;
        contractExpiryDate = contExpiryDate;
    }

    //method to check if student already has 5 contracts
    public boolean checkContract(){
        boolean retVal=false;
        int count=0;
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("contract", GuiAction.myApiKey);
        try {
            ObjectNode[] userNodes = new ObjectMapper().readValue(userResponse.body(), ObjectNode[].class);
            for (ObjectNode node : userNodes) {
                String firstParty=node.get("firstParty").toString();
                String secondParty=node.get("secondParty").toString();
                boolean notConfirmed=node.get("dateSigned").toString().equals("null");
                if (notConfirmed==false) {
                    if (firstParty.contains(studentId) | secondParty.contains(studentId)) {
                        count += 1;
                    }
                }

            }
        }
        catch(Exception e){

        }

        if (count<5){
            retVal=true;
        }
        return retVal;
    }

    //method to stire contract
    public void storeContract(){
        String endpoint="contract";
        String jsonString="";

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        
        // create the contract
        JSONObject contractInfo=new JSONObject();
        contractInfo.put("firstPartyId", acceptedOffer.getFirstPartyId());
        contractInfo.put("secondPartyId", acceptedOffer.getSecondPartyId());
        contractInfo.put("subjectId", acceptedOffer.getSubjectId());
        contractInfo.put("dateCreated", new Date().toInstant().toString());
        contractInfo.put("expiryDate", contractExpiryDate);

        JSONObject lessonInfo=new JSONObject();
        // create the lesson info
        lessonInfo.put("subjectName", acceptedOffer.getSubjectId());
        lessonInfo.put("subjectDesc", acceptedOffer.getSubjectDesc());
        lessonInfo.put("competency", acceptedOffer.getCompetency());
        lessonInfo.put("weeklySession", acceptedOffer.getWeeklySession());
        lessonInfo.put("hoursPerLesson", acceptedOffer.getHoursPerLesson());
        lessonInfo.put("rate", acceptedOffer.getRate());
        lessonInfo.put("studentName", acceptedOffer.getStudentName());
        lessonInfo.put("tutorName", acceptedOffer.getTutorName());
        lessonInfo.put("tutorQualification", acceptedOffer.getTutorQualification());

        JSONObject additionalInfo=new JSONObject();
        //create additional info
        additionalInfo.put("firstPartySigned",firstPartySigned);

        contractInfo.put("lessonInfo", lessonInfo);

        //means that one of the party has signed
        if (firstPartySigned!=""){
            contractInfo.put("additionalInfo",additionalInfo);

        }

        jsonString=contractInfo.toString();
        HttpResponse<String> updateResponse = GuiAction.updateWebApi(endpoint, myApiKey, jsonString);
        //close the request

        if (updateResponse.statusCode()==201){
            new RequestCloser(1, bidId, myApiKey, new Date().toInstant().toString());
        }


    }

    @Override
    public void show() {

    }
}
