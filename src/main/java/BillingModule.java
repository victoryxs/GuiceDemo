/**
 * Created by victoryxs on 2015/8/7.
 */

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;


public class BillingModule extends AbstractModule {
    @Override
    protected void configure() {


    //    bind(TransactingLog.class).annotatedWith(Names.named("MySQL")).to(MySqlDatabaseTransactionLog.class);
    //    bind(TransactingLog.class).annotatedWith(Names.named("Oracle")).to(OracleDatabaseTransactionLog.class);
        bind(CreditCardProcessor.class);

        bind(String.class).annotatedWith(Names.named("url")).toInstance("jdbc:mysql://localhost/card");
        bind(String.class).annotatedWith(Names.named("user")).toInstance("root");
        bind(String.class).annotatedWith(Names.named("password")).toInstance("root");
        bind(String.class).annotatedWith(Names.named("driver")).toInstance("com.mysql.jdbc.Driver");

    //    bind(TransactingLog.class).toProvider(MySQLTransactionLogProvider.class);
        bind(TransactingLog.class);

     //  bind(BillingService.class).to(BillingServiceImpl.class);
    }

//    @Provides @Named("MySQL")
//    TransactingLog provideTransactionLog(@Named("url")String url, @Named("user")String user, @Named("password")String password, @Named("driver")String driver){
//        MySqlDatabaseTransactionLog transactionLog = new MySqlDatabaseTransactionLog();
//        transactionLog.setDriver(driver);
//        transactionLog.setPassword(password);
//        transactionLog.setUrl(url);
//        transactionLog.setUser(user);
//        return transactionLog;
//    }
}
