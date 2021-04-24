import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

/*
 * @author Rafaquat
 *
 */
public class RequestCloser {
	Timer timer;
	String id;
	String key;
	String bidEndTime;
	public RequestCloser(int seconds, String bidId, String apiKey, String closeTime) {
		key = apiKey;
		id  = bidId;
		bidEndTime = closeTime;
		timer = new Timer();
        timer.schedule(new RequestCloserTask(), seconds*1000);
	}
	
	class RequestCloserTask extends TimerTask {
        public void run() {
        	// change the status in db
            String endpoint = "bid/"+id+"/close-down";
            
            JSONObject bidInfo=new JSONObject();
            String close = GraphicalUserInterface.removeQuotations(bidEndTime);
    		bidInfo.put("dateClosedDown", close);
    	
    		String jsonString = bidInfo.toString();
            HttpResponse<String> updateResponse = GraphicalUserInterface.updateWebApi(endpoint, key, jsonString);
            System.out.println(updateResponse.toString());
            System.out.println("BID CLOSED");
            timer.cancel(); //Terminate the timer thread
            
        }
    }
	
}
