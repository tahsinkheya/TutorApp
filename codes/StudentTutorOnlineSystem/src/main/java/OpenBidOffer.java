
/*
* a class that is used by viewBidOfferAction to view offers to student,
* this instance is later used to create contract
* it is also used by some other classes like student to create contracts
* */
public class OpenBidOffer {
    private String firstPartyId;
    private String secondPartyId;
    private String subjectId;
    private String subjectDesc;
    private String competency;
    private String weeklySession;
    private String hoursPerLesson;
    private String rate;



    private String studentName;
    private String tutorName;
    private String fressLesson;
    private String extraInfo;


    private String tutorQualification;



    public OpenBidOffer(String newFirstId, String newSecondId, String newsubId, String newSubDes, String comp, String weeklySess, String hlp, String newRate, String stuName, String tuteName,String fl,String extra,String qualification ){
        firstPartyId=newFirstId;
        secondPartyId=newSecondId;
        subjectId=newsubId;
        subjectDesc=newSubDes;
        competency=comp;
        weeklySession=weeklySess;
        hoursPerLesson=hlp;
        rate=newRate;
        studentName=stuName;
        tutorName=tuteName;
        fressLesson=fl;
        extraInfo=extra;
        tutorQualification=qualification;

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
