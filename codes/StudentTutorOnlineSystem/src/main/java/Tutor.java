public class Tutor implements  User{
    String userName;
    String givenName;
    String familyName;
    TutorGUI tutorGUI=new TutorGUI();


    @Override
    public boolean signContract() {
        return false;
    }

    @Override
    public void create(String uName, String gName,String fName) {
        this.userName=uName;
        this.givenName=gName;
        this.familyName=fName;
    }

    @Override
    public void showHomePage(String name) {
        tutorGUI.showHome(userName);

    }
}
