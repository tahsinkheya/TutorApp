import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public abstract class BidAction {
    static String myApiKey=OnlineMatchingClient.myApiKey;

    public ArrayList<String> getBidInfo(String bidId){
        ArrayList<String> bidInfo = new ArrayList<String>();

        String endpoint = "bid/"+bidId;
        String subName = "";
        String subDesc = "";
        String comp = "";
        String weeklysess = "";
        String hoursperless = "";
        String rate = "";
        HttpResponse<String> response = APIRequester.initiateWebApiGET(endpoint, myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);


            String subjectName = userNode.get("subject").get("name").toString();
            String subjectDesc = userNode.get("subject").get("description").toString();
            subName = GuiAction.removeQuotations(subjectName);
            subDesc = GuiAction.removeQuotations(subjectDesc);
            comp=userNode.get("additionalInfo").get("requiredCompetency").toString();
            weeklysess=userNode.get("additionalInfo").get("weeklySessions").toString();
            hoursperless=userNode.get("additionalInfo").get("hoursPerLesson").toString();

            rate=userNode.get("additionalInfo").get("rate").toString();
            System.out.println("Subject in the bid: "+subName);
            System.out.println("Subject in the des: "+subDesc);
            System.out.println("com: "+comp);
            System.out.println("ws: "+weeklysess);
            System.out.println("hpl: "+hoursperless);
            System.out.println("rate: "+rate);


        }
        catch (Exception e){
            System.out.println("Error!!!");
            System.out.println(e.getCause());
        }
        bidInfo.add(subName);
        bidInfo.add(subDesc);
        bidInfo.add(GuiAction.removeQuotations(comp));
        bidInfo.add(GuiAction.removeQuotations(weeklysess));
        bidInfo.add(GuiAction.removeQuotations(hoursperless));
        bidInfo.add(GuiAction.removeQuotations(rate));
        return bidInfo;
    }


}
