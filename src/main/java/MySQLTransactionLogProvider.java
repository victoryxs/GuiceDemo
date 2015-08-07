
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import java.sql.Connection;

/**
 * Created by victoryxs on 2015/8/7.
 */


public class MySQLTransactionLogProvider implements Provider<TransactingLog> {

    private String url;
    private String driver;
    private String user;
    private String password;

    @Inject
    MySQLTransactionLogProvider(@Named("url")String url, @Named("driver")String driver, @Named("user")String user, @Named("password")String password){
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }
    public TransactingLog get(){
        MySqlDatabaseTransactionLog transactionLog = new MySqlDatabaseTransactionLog();
        transactionLog.setDriver(driver);
        transactionLog.setPassword(password);
        transactionLog.setUrl(url);
        transactionLog.setUser(user);
        return transactionLog;
    }

}
