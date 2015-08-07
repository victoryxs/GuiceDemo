/**
 * Created by victoryxs on 2015/8/7.
 */
public interface BillingService {
    public Receipt chargeOrdr(PizzaOrder order, CreditCard creditCard);
}
