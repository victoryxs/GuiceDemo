import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;

/**
 * Created by victoryxs on 2015/8/7.
 */
public class BillingServiceImplTest extends TestCase {

    public void testChargeOrdr() throws Exception {
        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.setAmount(100);
        CreditCard creditCard = new CreditCard();
        creditCard.setAccount(200);
       Injector injector = Guice.createInjector(new BillingModule());
       BillingService billingService = injector.getInstance(BillingServiceImpl.class);

        billingService.chargeOrdr(pizzaOrder,creditCard);


    }
}