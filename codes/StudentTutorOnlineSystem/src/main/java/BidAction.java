import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpResponse;
import java.util.ArrayList;
/*
* base class for tutors to call when they want to view open and close bids*/
public abstract class BidAction {
    protected static String myApiKey=OnlineMatchingClient.myApiKey;
    private ArrayList<String> bidInfo = new ArrayList<String>();
    private void getSubInfo(ObjectNode userNode){
        String subjectName = userNode.get("subject").get("name").toString();
        String subjectDesc = userNode.get("subject").get("description").toString();
        String subName = GuiAction.removeQuotations(subjectName);
        String subDesc = GuiAction.removeQuotations(subjectDesc);
        bidInfo.add(subName);
        bidInfo.add(subDesc);
    }

    private void getLessonInfo(ObjectNode userNode){
        String comp=userNode.get("additionalInfo").get("requiredCompetency").toString();
        String weeklysess=userNode.get("additionalInfo").get("weeklySessions").toString();
        String hoursperless=userNode.get("additionalInfo").get("hoursPerLesson").toString();
        String rate=userNode.get("additionalInfo").get("rate").toString();
        bidInfo.add(GuiAction.removeQuotations(comp));
        bidInfo.add(GuiAction.removeQuotations(weeklysess));
        bidInfo.add(GuiAction.removeQuotations(hoursperless));
        bidInfo.add(GuiAction.removeQuotations(rate));
    }

    //method called by subclasses to get info abt the bid
    public ArrayList<String> getBidInfo(String bidId){
        //initialise strings
        String endpoint = "bid/"+bidId;
        String initiatorId = "";
        String subId = "";
        String initiatorgname = "";
        String initiatorfname = "";
        HttpResponse<String> response = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
            //get all info abt bid and store them in the arraylist
            getSubInfo(userNode);
            getLessonInfo(userNode);
            subId = userNode.get("subject").get("id").toString();
            initiatorId=userNode.get("initiator").get("id").toString();
            initiatorgname=userNode.get("initiator").get("givenName").toString();
            initiatorfname=userNode.get("initiator").get("familyName").toString();
             }
        catch (Exception e){
            System.out.println("Error!!!"); }
        String studentFullName=GuiAction.removeQuotations(initiatorgname)+" " +GuiAction.removeQuotations(initiatorfname);
        bidInfo.add(GuiAction.removeQuotations(initiatorId));
        bidInfo.add(GuiAction.removeQuotations(subId));
        bidInfo.add(studentFullName);
        return bidInfo;
    }
    //method called by subclasses to get tutor qualifications when making a bid offer
    public String TutorQualification(String userId) {
        String endpoint = "user?fields=qualifications";
        String tutorQ = "";
        HttpResponse<String> compResponse = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try{
            ObjectNode[] userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode[].class);
            for (JsonNode node : userNode) {
                if(node.get("id").toString().contains(userId)){//check that the tutorid=userid
                    for(JsonNode n:node.get("qualifications")){
                        tutorQ+=GuiAction.removeQuotations(n.get("title").toString())+" | ";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if tutor qualifictaion is not known then
        if (tutorQ.equals("")){
            tutorQ="unknown";
        }
        return tutorQ;

    }


}
