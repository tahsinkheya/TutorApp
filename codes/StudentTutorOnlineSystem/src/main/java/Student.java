
/**
 * 
 */

/**
 * @author Rafaquat
 *
 */
public class Student implements User {
	String userName;
	String givenName;
	String familyName;
	public static void main(String[] args) {
		StudentGUI studentUI = new StudentGUI();
	}

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
}