/**
 * Created by victoryxs on 2015/8/7.
 */


public class Receipt {
    private String receipt;
    Receipt(String receipt){
        this.receipt = receipt;
    }

    public static Receipt forSuccessfulCharge(double amount){
        return new Receipt("have cost" + amount);
    }

    public static Receipt forDeclinedCharge(String declineMessage){
        return new Receipt(declineMessage);
    }




}
