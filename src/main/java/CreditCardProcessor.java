import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.ProvidedBy;

/**
 * Created by victoryxs on 2015/8/7.
 */


@ImplementedBy(PaypalCreditCardProcessor.class)
public interface CreditCardProcessor {

    public  ChargeResult charge(CreditCard creditCard, double amount);

}
