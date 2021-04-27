import java.net.http.HttpResponse;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;

/*
 * @author Rafaquat
 *
 *This class is used to handle the timer thread. When a new instance is made, the run method
 *in RequestCloserTask is run, which closes the timer and closes the bid
 */
public class RequestCloser {
	private Timer timer;
	private String id;
	private String key;
	private String bidEndTime;
	public RequestCloser(int seconds, String bidId, String apiKey, String closeTime) {
		key = apiKey;
		id  = bidId;
		bidEndTime = closeTime;
		timer = new Timer();
        timer.schedule(new RequestCloserTask(), seconds*1000);
	}
	
	class RequestCloserTask extends TimerTask {
        public void run() {
        	System.out.println(bidEndTime);
        	// change the status in db to close the bid
            String endpoint = "bid/"+id+"/close-down";
            
            JSONObject bidInfo=new JSONObject();
//            String close = GuiAction.removeQuotations(bidEndTime);
//            System.out.println(close);
			bidInfo.put("dateClosedDown", bidEndTime);
    	
    		String jsonString = bidInfo.toString();
            HttpResponse<String> updateResponse = GuiAction.updateWebApi(endpoint, key, jsonString);
            System.out.println(updateResponse.toString());
            System.out.println("BID CLOSED");
            timer.cancel(); //Terminate the timer thread
            //patch
        }
    }
	
}
