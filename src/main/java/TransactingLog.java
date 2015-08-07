import com.google.inject.ImplementedBy;
import com.google.inject.ProvidedBy;

/**
 * Created by victoryxs on 2015/8/7.
 */


@ProvidedBy(MySQLTransactionLogProvider.class)
public interface TransactingLog {

    public void  logChargeResult(ChargeResult chargeResult);
}
