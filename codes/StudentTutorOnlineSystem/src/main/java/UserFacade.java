/**
 * A special facade class to hide the complexity of the subsystem classes
 *
 */

public class UserFacade {
    private User user;
    public void createUser(String userName, String givenName,String familyName,String Type){
        if (Type.contains("Student")){
            user=new Student();
            user.create(userName,givenName,familyName);
            //display homepage of Student
        }
        else{
            user=new Tutor();
            user.create(userName,givenName,familyName);
            //display homepage of Tutor
        }
    }
}
