/**
 * A special facade class to hide the complexity of the subsystem classes
 *
 */

public class UserFacade {
    private User user;
    public UserFacade(String userName, String givenName, String familyName, String Type){
        if (Type.equals("Student")){
            user=new Student();
            user.create(userName,givenName,familyName);
            System.out.println("uo");
            //display homepage of Student
        }
        else{
            user=new Tutor();
            user.create(userName,givenName,familyName);
            //display homepage of Tutor
        }
    }

    public void displayHomePage(){
        user.showHomePage("");
    }
}
