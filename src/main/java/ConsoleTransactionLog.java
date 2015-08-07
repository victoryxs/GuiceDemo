import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.logging.Logger;

/**
 * Created by victoryxs on 2015/8/7.
 */

@Singleton
public class ConsoleTransactionLog implements TransactingLog {
    private final Logger logger;

    @Inject
    public ConsoleTransactionLog(Logger logger) {
        this.logger = logger;
    }

    public void logChargeResult(ChargeResult chargeResult) {
        logger.info(chargeResult.getDeclineMessage());
    }
}
