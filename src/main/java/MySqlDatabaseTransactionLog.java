import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.sql.Connection;

/**
 * Created by victoryxs on 2015/8/7.
 */
public class MySqlDatabaseTransactionLog implements TransactingLog {

    private String url;

    private String driver;

    private String user;

    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void logChargeResult(ChargeResult chargeResult) {
        if(chargeResult.wasSuccessful()) {
            System.out.println("have connected to " + url + "/" + user + ":" + password);
            System.out.println("MySQL Success");
        }
        else{
            System.out.println("MySQL " + chargeResult.getDeclineMessage());
        }
    }
}
