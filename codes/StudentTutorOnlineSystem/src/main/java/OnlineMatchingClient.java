import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;




public class OnlineMatchingClient implements ActionListener {
    /*
    NOTE: In order to access the web service, you will need to include your API key in the Authorization header of all requests you make.
    Your personal API key can be obtained here: https://fit3077.com
   */
    private static final String myApiKey = "your api key here";

    // Provide the root URL for the web service. All web service request URLs start with this root URL.
    private static final String rootUrl = "https://fit3077.com/api/v1";
    String usersUrl = rootUrl + "/user";
    private static JLabel user,usernameLabel,passwordLabel;
    private static JTextField usernameText;
    private static JPasswordField passwordText;
    
    // the type of user that logged in: Student or Tutor
    private static String userType;

    public static void main(String[] args){
        JFrame logInFrame=new JFrame();
        logInFrame.setSize(350,200);
        logInFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add a panel
        JPanel loginPanel= new JPanel();
        logInFrame.add(loginPanel);
        loginPanel.setBackground(new Color(172, 209, 233));
        loginPanel.setLayout(null);
        user = new JLabel("User Login");
        user.setBounds(130,20,80,25);
        loginPanel.add(user);

        usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10,50,80,25);
        loginPanel.add(usernameLabel);

        Integer usernameLength= 20;
        usernameText=new JTextField(usernameLength);
        usernameText.setBounds(100,50,165,25);
        loginPanel.add(usernameText);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10,80,80,25);
        loginPanel.add(passwordLabel);

        Integer passwordLength= 20;
        passwordText=new JPasswordField(passwordLength);
        passwordText.setBounds(100,80,165,25);
        loginPanel.add(passwordText);

        JButton button= new JButton("Log In");
        button.setBounds(130,110,80,25);
        button.addActionListener(new OnlineMatchingClient());
        loginPanel.add(button);

        logInFrame.setVisible(true);



    }
    public boolean logInUser(String username,String password){
        boolean booleanVal=false;
        String jsonString = "{" +
                "\"userName\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(usersUrl))
                .setHeader("Authorization", myApiKey)
                .GET()
                .build();
        HttpResponse<String> response = null;
        
        
        // Find the username of the logged in user in db and find their type. Based on the type initialize their UI page
        try {
        	response=client.send(request, HttpResponse.BodyHandlers.ofString());
        	//System.out.println("Full JSON response: " + response.body());
        	
        	// The GET /user endpoint returns a JSON array, so we can loop through the response as we could with a normal array/list.
            ObjectNode[] jsonNodes = new ObjectMapper().readValue(response.body(), ObjectNode[].class);
            String usernameEntered= usernameText.getText(); // input username
            for (ObjectNode node: jsonNodes) {
              String usernameFromDB = node.get("userName").asText();
              if(usernameFromDB.equals(usernameEntered)) { // if user name matches, then check usertype
            	  if (node.get("isStudent").booleanValue()) {
            		  userType = "Student";
            	  }
            	  else {
            		  userType = "Tutor";
            	  }
              }
            }
        }
        catch(Exception e) {
        	System.out.println(e.getCause());
        }
        
        

        // Note the POST() method being used here, and the request body is supplied to it.
        // A request body needs to be supplied to this endpoint, otherwise a 400 Bad Request error will be returned.
        String usersLoginUrl = usersUrl + "/login";
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(usersLoginUrl + "?jwt=true")) // Return a JWT so we can use it in Part 5 later.
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        
        try{
            response = client.send(request, HttpResponse.BodyHandlers.ofString());}
        catch (Exception e){
            System.out.println(e.getCause());
        }

//        response = client.send(request, HttpResponse.BodyHandlers.ofString());

//        System.out.println("Part 4\n----");
//        System.out.println(request.uri());
        if (response.statusCode()==200){
            booleanVal=true;
        }
//        System.out.println("Response code: " + response.statusCode());
//        System.out.println("Full JSON response: " + response.body()); // The JWT token that has just been issued will be returned since we set ?jwt=true.
//        System.out.println("----\n\n");
        return booleanVal;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String usernameEntered= usernameText.getText();
        String passwordEntered= passwordText.getText();
        System.out.println(usernameEntered);
        System.out.println(passwordEntered);
        boolean loggedIn=logInUser(usernameEntered,passwordEntered);
        
        // open the student homepage after login
        if (userType.equals("Student")) {
        	StudentGUI studentUI = new StudentGUI();
            JLabel name = new JLabel("Welcome: " + usernameEntered);
            studentUI.name = name;
            studentUI.name.setBounds(10,20,150,25);
            studentUI.panel.add(name);
        }
        

    }
}
