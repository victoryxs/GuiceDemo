/**
 * Created by victoryxs on 2015/8/7.
 */
public class OracleDatabaseTransactionLog implements TransactingLog {

    public void logChargeResult(ChargeResult chargeResult) {
        if(chargeResult.wasSuccessful()) {
            System.out.println("Oracle Success");
        }
        else{
            System.out.println("Oracle " + chargeResult.getDeclineMessage());
        }
    }
}
