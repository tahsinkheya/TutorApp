
/*
* a class that is used by viewBidOfferAction to view offers to student,
* this instance is later used to create contract
* it is also used by some other classes like student to create contracts
* */
public class OpenBidOffer {
    private String firstPartyId;
    private String secondPartyId;
    private String studentName;
    private String tutorName;

    private String subjectId;
    private String subjectDesc;
    private String tutorQualification;
    private String competency;

    private String weeklySession;
    private String hoursPerLesson;
    private String rate;

    private String fressLesson;
    private String extraInfo;


    public OpenBidOffer(String newFirstId, String newSecondId,  String stuName, String tuteName ){
        firstPartyId=newFirstId;
        secondPartyId=newSecondId;
        studentName=stuName;
        tutorName=tuteName;


    }

    public void setClassInfo(String weeklySess, String hlp, String newRate){
        weeklySession=weeklySess;
        hoursPerLesson=hlp;
        rate=newRate;
    }
    public void setExtraInfo(String fl,String extra){
        fressLesson=fl;
        extraInfo=extra;
    }
    public void setSubjectInfo(String newsubId, String newSubDes,String comp,String qualification){
        subjectId=newsubId;
        subjectDesc=newSubDes;
        tutorQualification=qualification;
        competency=comp;

    }




    //getter methods
    public String getTutorQualification() {
        return tutorQualification;
    }

    public String getFressLesson() {
        return fressLesson;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
    public String getSecondPartyId() {
        return secondPartyId;
    }
    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectDesc() {
        return subjectDesc;
    }

    public String getCompetency() {
        return competency;
    }

    public String getWeeklySession() {
        return weeklySession;
    }

    public String getHoursPerLesson() {
        return hoursPerLesson;
    }

    public String getRate() {
        return rate;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getTutorName() {
        return tutorName;
    }

    public String getFirstPartyId() {
        return firstPartyId;
    }
}
