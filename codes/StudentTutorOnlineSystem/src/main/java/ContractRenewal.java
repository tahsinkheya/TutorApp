import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public abstract class ContractRenewal {

     abstract void showMessage();
     abstract void showWarning(String msg);
     public void storeContract(String contractId,String firstUid,String SeconUid,String userId,String mons){

         //create open bid offer
        OpenBidOffer offer=new OpenBidOffer(firstUid,SeconUid,"","");
        ArrayList <String> info;
        if(firstUid.contains(userId))// the first party is student
        {
             info=getContractInfo(contractId,SeconUid); }
        else{info=getContractInfo(contractId,firstUid);}

        offer.setClassInfo(info.get(0),info.get(1),info.get(2),info.get(3));
        offer.setSubjectInfo(info.get(4),info.get(5),info.get(6),info.get(7));
        offer.setExtraInfo("",""); // since theres no extra info lets set those to
          createContractAction contract=new createContractAction(offer,"student",userId,"",GuiAction.getContractExpiryDate(mons));
          contract.storeContract();
     }

     boolean checkSubject(String subName, String contractId){
          boolean subjectMatched=false;
          HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract/"+contractId, GuiAction.myApiKey);
          try {
               ObjectNode jsonNode = new ObjectMapper().readValue(contResponse.body(), ObjectNode.class);
               String subject=jsonNode.get("subject").get("name").asText();
               if(subject.contains(subName)){
                    subjectMatched=true;
               }

          }
          catch (Exception e){System.out.println(e.getStackTrace()[0].getLineNumber()); }
          return subjectMatched;
     }



     private ArrayList<String> getContractInfo(String contractId,String tutorId){
          ArrayList<String> contractInfo=new ArrayList<>();
          HttpResponse<String> contResponse = GuiAction.initiateWebApiGET("contract/"+contractId, GuiAction.myApiKey);
          try {
               ObjectNode jsonNode = new ObjectMapper().readValue(contResponse.body(), ObjectNode.class);
               String week=jsonNode.get("lessonInfo").get("weeklySession").asText();
               String hlp=jsonNode.get("lessonInfo").get("hoursPerLesson").asText();
               String newrte=jsonNode.get("lessonInfo").get("rate").asText();
               String reqComp=jsonNode.get("lessonInfo").get("requiredCompetency").asText();
               String subId=jsonNode.get("subject").get("id").asText();
               String subName=jsonNode.get("subject").get("name").asText();

               contractInfo.add(week);
               contractInfo.add(hlp);
               contractInfo.add(newrte);
               contractInfo.add(reqComp);
               contractInfo.add(subId);
               contractInfo.add(subName);
               contractInfo.add(Integer.toString(GuiAction.getTuteComp(subName,tutorId)));

          }
          catch (Exception e){System.out.println(e.getStackTrace()[0].getLineNumber()); }
          contractInfo.add(GuiAction.getTutorQualification(tutorId));
          return contractInfo;
     }

}
