import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TutorBidModel {
    private String tutorId;
    private Timer timer=new Timer();
    private Observer observer;
    //private Timer timer;
    private ArrayList<String> bidIds=new ArrayList<>();
    private ArrayList<String> offerInfo=new ArrayList<>();


    public TutorBidModel(String userId){
        tutorId=userId;
    }
//    private ArrayList<OpenBidOffer> getOfferList(String bidId){
//
//    }
    public void registerObserver(Observer obs){
        observer=obs;
    }
    private String getTutorId(){return tutorId;}
    public Vector getBidList(){
        Vector comboBoxItems=new Vector();
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("user/" + getTutorId(), GuiAction.myApiKey);
        try{JsonNode jsonNode = new ObjectMapper().readValue(userResponse.body(), JsonNode.class);
        if (jsonNode.get("additionalInfo").toString().equals("{}") == true) {
            //the tutor is not subscribed to any bids
            return comboBoxItems; }
        else{//convert from string to array
            String previousBid=jsonNode.get("additionalInfo").get("bids").asText();
            String joinedMinusBrackets = previousBid.substring( 1, previousBid.length() - 1);
            String[] bidsSubscribedTo = joinedMinusBrackets.split( ", ");
            for (String bid: bidsSubscribedTo){ // add the previos bids
                //convert to json
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(bid);
                if(expiredBid(json.get("bidId").toString())==false){
                    comboBoxItems.add(json.get("bidInfo"));
                    bidIds.add(json.get("bidId").toString());} }
            }
        }
        catch(Exception e){System.out.println(e.getMessage()) ;
        System.out.println(e.getStackTrace()[0].getLineNumber());

        }
        offerInfo.clear();
        return comboBoxItems;
    }

    public String getBidOfferInfo(int index){
        return offerInfo.get(index);
    }
//helper method used by getBidOffers to get info abt a tutors offer
    private String getOfferInfo(JsonNode msgNode){
        String duration = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("duration").toString());
        String rate = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("rate").toString());
        String numberOfSession = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("numberOfSession").toString());
        String freelesson = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("freeLesson").toString());
        String competency = GuiAction.removeQuotations(msgNode.get("additionalInfo").get("tutorComp").toString());
        String extra = msgNode.get("additionalInfo").get("extraInfo").textValue();
        String tutor = GuiAction.removeQuotations(msgNode.get("poster").get("givenName").toString()) + " " + GuiAction.removeQuotations(msgNode.get("poster").get("familyName").toString());
        String newOffer="By "+tutor+"\n" +"Duration Offered:"+duration+" hrs per lesson"+"\n"+
                "Rate:"+rate+"\n"+"Number of weekly session:"+numberOfSession+"\n"+
                "a fress lesson was offered:"+freelesson+"\n"+
                "extra information from the tutor:"+extra+"\n"
                +"Tutor competency in this subject:"+competency;
        return newOffer;
    }
// a method called by the modeller to get the bid offers for a particular bid
    public ArrayList<String> getBidOffers(int index){
        offerInfo.clear();
        ArrayList<String> offers=new ArrayList<String>();
        String choosenBid=bidIds.get(index);
        String endpoint="bid/"+choosenBid+"?fields=messages";
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
            for (JsonNode msgNode : userNode.get("messages")) {
                if (userNode.get("additionalInfo").toString().equals("{}")==false){
                    //get all details
                    String  msgSender = msgNode.get("poster").get("userName").textValue();
                    String  msgSenderId = msgNode.get("poster").get("id").textValue();
                    if (msgSenderId.contains(getTutorId())==false) {
                        offers.add("from: " + msgSender);
                        offerInfo.add(getOfferInfo(msgNode)); }
                    //current user's offer
                    else{
                        offers.add("My latest offer");
                        offerInfo.add(getOfferInfo(msgNode)); }
                }
            }
        }
        catch(Exception e){System.out.println(e.getMessage());}
        timelyUpdateBidOffer();
        return offers;
    }

    private boolean expiredBid(String bidid) throws JsonProcessingException, ParseException {

        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET("bid/" + bidid, GuiAction.myApiKey);
        JsonNode node = new ObjectMapper().readValue(userResponse.body(), JsonNode.class);
        String today = new Date().toInstant().toString();
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String bidCloseTime = node.get("additionalInfo").get("requestClosesAt").asText();

        Date todayDate = sourceFormat.parse(today);
        Date endDate = sourceFormat.parse(bidCloseTime);
        return todayDate.after(endDate);
    }
//a method that will update the observer(controller) abt bid offers
    private void timelyUpdateBidOffer(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int times = 0;

            @Override
            public void run () {
                if (times == 120) { //the amount of times you want the code executed.120 is the max since 30mins is the time
                    timer.cancel();
                    return;
                }
                observer.update();
                times++;
            }
        }, 15*1000);

    }

    public boolean checkTutorComp(int index){
        boolean retVal=false;
        String choosenBid=bidIds.get(index);
        String endpoint="bid/"+choosenBid+"?fields=";
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
        try {
            ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
            String subName=userNode.get("subject").get("name").textValue();
            int actualComp=GuiAction.getTuteComp(subName,tutorId);
            System.out.println(actualComp);
            int requirendComp=Integer.parseInt(userNode.get("additionalInfo").get("requiredCompetency").textValue());
            System.out.println(requirendComp);
            if(actualComp>=(requirendComp+2)){retVal=true;}
        }
        catch(Exception e){System.out.println(e.getMessage());}
        return retVal;

    }

    public void CreateMessage(int index,JSONObject additionalInfo){
        String choosenBid=bidIds.get(index);
        String endpoint="bid/"+choosenBid+"?fields=messages";
        HttpResponse<String> userResponse = GuiAction.initiateWebApiGET(endpoint, GuiAction.myApiKey);
        boolean reviseBid=false;
        String msgId=null;
        String subName=null;
        try {
            ObjectNode userNode = new ObjectMapper().readValue(userResponse.body(), ObjectNode.class);
            for (JsonNode msgNode : userNode.get("messages")) {
                subName=userNode.get("subject").get("name").textValue();
                if (msgNode.get("poster").get("id").textValue().contains(getTutorId())){
                    //revising the bid
                    reviseBid=true;
                    msgId=msgNode.get("id").textValue();

                }

            }
        }
        catch(Exception e){System.out.println(e.getMessage());}
        String totorQ=GuiAction.getTutorQualification(tutorId);
        int comp=GuiAction.getTuteComp(subName,tutorId);
        additionalInfo.put("tutorComp",String.valueOf(comp));
        additionalInfo.put("tutorQualification",totorQ);
        if (reviseBid){
            updatePreviousOffer(additionalInfo,msgId);
        }
        else{storeNewOffer(additionalInfo,index);}




    }
    private void updatePreviousOffer(JSONObject additionalInfo,String msgId){
        JSONObject userInfo=new JSONObject();
        userInfo.put("additionalInfo",additionalInfo);
        GuiAction.patchWebApi("message/"+msgId,userInfo.toString());

    }

    private void storeNewOffer(JSONObject additionalInfo,int index){
        JSONObject msgInfo=new JSONObject();
        msgInfo.put("bidId", bidIds.get(index));
        msgInfo.put("posterId", tutorId);
        msgInfo.put("datePosted", new Date().toInstant().toString());
        msgInfo.put("content", "an open bid offer");
        msgInfo.put("additionalInfo", additionalInfo);

        // convert message to JSON string
        String jsonString = msgInfo.toString();
        GuiAction.updateWebApi("message",GuiAction.myApiKey,jsonString);
    }



//    class TimelyUpdater extends TimerTask {
//        public void run() {
//            timer.cancel(); //Terminate the timer thread
//
//        }
//    }

}




