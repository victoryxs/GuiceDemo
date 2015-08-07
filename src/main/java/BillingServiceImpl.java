import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by victoryxs on 2015/8/7.
 */
public class BillingServiceImpl implements BillingService{


        private final CreditCardProcessor processor;
        private final TransactingLog transactingLog;


    @Inject
    BillingServiceImpl(CreditCardProcessor processor,TransactingLog transactingLog){
            this.processor = processor;
            this.transactingLog = transactingLog;
        }

        public Receipt chargeOrdr(PizzaOrder order, CreditCard creditCard) {
            ChargeResult result = processor.charge(creditCard, order.getAmount());
            transactingLog.logChargeResult(result);

             return result.wasSuccessful() ? Receipt.forSuccessfulCharge(order.getAmount()) : Receipt.forDeclinedCharge(result.getDeclineMessage());
        }
}
