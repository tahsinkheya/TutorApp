
/**
 * 
 */

/**
 * @author Rafaquat
 *
 */
public class Student implements User {
	private static StudentGUI studentGUI= new StudentGUI();
	String userName;
	String givenName;
	String familyName;

	//StudentGUI studentGUI;
	

	@Override
	public boolean signContract() {
		return false;
	}

	@Override
	public void create(String uName, String gName, String fName) {
		this.userName=uName;
		this.givenName=gName;
		this.familyName=fName;

	}

	@Override
	public void showHomePage(String name) {
		studentGUI.studentHomepage(userName);
	}

	static void showAllRequests(){
		studentGUI.showAllRequests();
	}


}