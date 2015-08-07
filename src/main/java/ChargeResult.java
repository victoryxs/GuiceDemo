/**
 * Created by victoryxs on 2015/8/7.
 */
public class ChargeResult {

    private boolean successful;
    private String declineMessage;

    public static final boolean SUCCESS = true;
    public static final boolean FAILURE = false;

    public void setSuccessful(boolean successful){
        this.successful = successful;
    }

    public boolean wasSuccessful(){
       return successful;
   }

    public  void setDeclineMessage(String declineMessage){
        this.declineMessage = declineMessage;
    }
    public String getDeclineMessage(){
        return declineMessage;
    }

}
