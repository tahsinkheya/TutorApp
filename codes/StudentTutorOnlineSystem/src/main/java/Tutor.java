public class Tutor implements  User{
    String userName;
    String givenName;
    String familyName;
    String userId;
    TutorGUI tutorGUI=new TutorGUI();


    @Override
    public boolean signContract() {
        return false;
    }

    @Override
    public void create(String uName, String gName,String fName,String uId) {
        this.userName=uName;
        this.givenName=gName;
        this.familyName=fName;
        this.userId=uId;
    }

    @Override
    public void showHomePage(String name) {
        tutorGUI.showHome(userName);

    }
}
