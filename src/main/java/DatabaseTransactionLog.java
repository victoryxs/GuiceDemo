import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by victoryxs on 2015/8/7.
 */
public class DatabaseTransactionLog implements TransactingLog {

    public void logChargeResult(ChargeResult chargeResult) {
        if(chargeResult.wasSuccessful()) {
            System.out.println();
        }
        else{
            System.out.println(chargeResult.getDeclineMessage());
        }
    }

}
