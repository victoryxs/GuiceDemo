/**
 * Created by victoryxs on 2015/8/7.
 */
public class PaypalCreditCardProcessor implements CreditCardProcessor {
    public ChargeResult charge(CreditCard creditCard, double amount) {
        ChargeResult chargeResult = new ChargeResult();

        if(creditCard.getAccount() >= amount){
            creditCard.setAccount(creditCard.getAccount() - amount);
            chargeResult.setSuccessful(ChargeResult.SUCCESS);
        }else{
            chargeResult.setDeclineMessage("Account is not enough");
        }
        return chargeResult;
    }
}
