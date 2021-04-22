import java.util.Timer;
import java.util.TimerTask;

/*
 * @author Rafaquat
 *
 */
public class RequestCloser {
	Timer timer;

	public RequestCloser(int seconds) {
		timer = new Timer();
        timer.schedule(new RequestCloserTask(), seconds*1000);
	}
	
	class RequestCloserTask extends TimerTask {
        public void run() {
            System.out.println("Bid closed successfully");
            timer.cancel(); //Terminate the timer thread
        }
    }
	
}
