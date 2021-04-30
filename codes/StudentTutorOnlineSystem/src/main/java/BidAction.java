import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public abstract class BidAction {
    protected static String myApiKey=OnlineMatchingClient.myApiKey;

    public ArrayList<String> getBidInfo(String bidId){
        ArrayList<String> bidInfo = new ArrayList<String>();

        String endpoint = "bid/"+bidId;
        String subName = "";
        String subDesc = "";
        String comp = "";
        String weeklysess = "";
        String hoursperless = "";
        String rate = "";
        String initiatorId = "";
        String subId = "";
        String initiatorgname = "";
        String initiatorfname = "";
        HttpResponse<String> response = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);


            String subjectName = userNode.get("subject").get("name").toString();
            String subjectDesc = userNode.get("subject").get("description").toString();
            subId = userNode.get("subject").get("id").toString();
            subName = GuiAction.removeQuotations(subjectName);
            subDesc = GuiAction.removeQuotations(subjectDesc);
            comp=userNode.get("additionalInfo").get("requiredCompetency").toString();
            weeklysess=userNode.get("additionalInfo").get("weeklySessions").toString();
            hoursperless=userNode.get("additionalInfo").get("hoursPerLesson").toString();
            initiatorId=userNode.get("initiator").get("id").toString();
            initiatorgname=userNode.get("initiator").get("givenName").toString();
            initiatorfname=userNode.get("initiator").get("familyName").toString();

            rate=userNode.get("additionalInfo").get("rate").toString();


        }
        catch (Exception e){
            System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
        String studentFullName=GuiAction.removeQuotations(initiatorgname)+" " +GuiAction.removeQuotations(initiatorfname);
        bidInfo.add(subName);
        bidInfo.add(subDesc);
        bidInfo.add(GuiAction.removeQuotations(comp));
        bidInfo.add(GuiAction.removeQuotations(weeklysess));
        bidInfo.add(GuiAction.removeQuotations(hoursperless));
        bidInfo.add(GuiAction.removeQuotations(rate));
        bidInfo.add(GuiAction.removeQuotations(initiatorId));
        bidInfo.add(GuiAction.removeQuotations(subId));
        bidInfo.add(studentFullName);
        return bidInfo;
    }

    public String TutorQualification(String userId) {
        String endpoint = "user?fields=qualifications";
        String tutorQ = "";
        HttpResponse<String> compResponse = GuiAction.initiateWebApiGET(endpoint, myApiKey);
        try{
            ObjectNode[] userNode = new ObjectMapper().readValue(compResponse.body(), ObjectNode[].class);
            for (JsonNode node : userNode) {
                if(node.get("id").toString().contains(userId)){
                    for(JsonNode n:node.get("qualifications")){
                        tutorQ+=GuiAction.removeQuotations(n.get("title").toString())+" | ";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tutorQ.equals("")){
            tutorQ="unknown";
        }
        return tutorQ;

    }


}
