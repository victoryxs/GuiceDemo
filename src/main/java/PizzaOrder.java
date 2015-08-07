/**
 * Created by victoryxs on 2015/8/7.
 */
public class PizzaOrder {

    private double amount;

    public void setAmount(double amount){
        this.amount = amount;
    }
    public double getAmount(){
        return amount;
    }

    public String toString(){
        return "Amount:" + this.amount;
    }

}
