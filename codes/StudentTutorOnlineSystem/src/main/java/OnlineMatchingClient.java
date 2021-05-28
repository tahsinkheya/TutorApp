import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class OnlineMatchingClient implements ActionListener {
    private UserFacade facadeUser;

    protected static final String myApiKey = "";

    // Provide the root URL for the web service. All web service request URLs start with this root URL.
    private static final String rootUrl = "https://fit3077.com/api/v2";
    private String usersUrl = rootUrl + "/user";
    private static JLabel user,usernameLabel,passwordLabel,loginNotSuccessful;
    private static JTextField usernameText;
    private static JPasswordField passwordText;
    private static JFrame logInFrame;
    // the type of user that logged in: Student or Tutor
    private String userType;
    private static JPanel loginPanel;
    
    // userID needed for initializing bids and messages
    private String userID;

//the main method this starts the program
    public static void main(String[] args){

        //add a frame
        logInFrame=new JFrame();
        logInFrame.setSize(350,250);
        logInFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //add a panel and title
        loginPanel= new JPanel();
        logInFrame.add(loginPanel);
        loginPanel.setBackground(new Color(172, 209, 233));
        loginPanel.setLayout(null);
        user = new JLabel("User Login");
        user.setBounds(130,20,80,25);
        loginPanel.add(user);
        //add labels and textfields for username and password
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
        //add a button
        JButton button= new JButton("Log In");
        button.setBounds(130,110,80,25);
        button.addActionListener(new OnlineMatchingClient());
        loginPanel.add(button);
        //a label for showing messages later
        loginNotSuccessful=new JLabel("");
        loginNotSuccessful.setBounds(10,150,340,25);
        loginPanel.add(loginNotSuccessful);

        logInFrame.setVisible(true);



    }
    private JsonNode decodeJwt(String token){
        //decode the jwt to get user info
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();

        String payload = new String(decoder.decode(chunks[1]));

        ObjectMapper mapper = new ObjectMapper();
        try{
        JsonNode actualObj = mapper.readValue(payload, JsonNode.class);
        return actualObj;}
        catch(Exception e){return null;}
    }
    //a method for user to log in returns true if the process is successful
    public boolean logInUser(String username,String password){
        boolean booleanVal=false;
        String jsonString = "{" + "\"userName\":\"" + username + "\"," + "\"password\":\"" + password + "\"" + "}";
        HttpResponse<String> response = null;
        // A request body needs to be supplied to this endpoint, otherwise a 400 Bad Request error will be returned.
        String usersLoginUrl = usersUrl + "/login";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(usersLoginUrl + "?jwt=true")) // Return a JWT so we can use it in Part 5 later.
                .setHeader("Authorization", myApiKey)
                .header("Content-Type","application/json") // This header needs to be set when sending a JSON request body.
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        ObjectNode jsonNode;
        try{
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jsonNode= new ObjectMapper().readValue(response.body(), ObjectNode.class);
            JsonNode actualObj=decodeJwt(jsonNode.get("jwt").textValue());
            // Find the username of the logged in user in db and find their type. Based on the type initialize their UI page
            if(actualObj.get("isStudent").asBoolean()==true){
                userType="Student";
            }
            else{
            	userType="Tutor";
            }
            userID = actualObj.get("sub").asText();
            //create facadeuser by using the type
            facadeUser= new UserFacade(actualObj.get("username").asText(),actualObj.get("givenName").asText(),actualObj.get("familyName").asText(),userType,userID);
        }
        catch (Exception e){
        }
        //if the signin process is successful
        if (response!=null & response.statusCode()==200){
            booleanVal=true;
        }
        return booleanVal;
    }
    //method for the button
    @Override
    public void actionPerformed(ActionEvent e) {
        //get texts from the text field
        String usernameEntered= usernameText.getText();
        String passwordEntered= passwordText.getText();
        //error handling
        if (usernameEntered.equals("") || passwordEntered.equals("")){
            loginNotSuccessful.setText("None of the fields can be left blank!");
        }
        else {
            boolean loggedIn = logInUser(usernameEntered, passwordEntered);
            if (loggedIn == false) {
                //show error messages
                loginNotSuccessful.setText("Login not successful.Username or password incorrect");
            }
            else {
                //call facade method
                logInFrame.setVisible(false);
                facadeUser.displayHomePage();

            }
        }
        

    }
}
