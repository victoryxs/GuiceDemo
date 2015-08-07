# ��ʼ

����һ�����˵�ҵ��,���Ƿ������ҵ��������`CreditCardProcessor`��`TransactionLog`���͵Ķ��󣬲���Guice��ܶԸö���Ľ��а󶨡�����`Spring`������ע�롣


```java

public interface BillingService {
    public Receipt chargeOrdr(PizzaOrder order, CreditCard creditCard);
}


public class BillingServiceImpl implements BillingService{


        private final CreditCardProcessor processor;
        private final TransactingLog transactingLog;
    @Inject
      BillingServiceImpl(CreditCardProcessor processor, TransactingLog transactingLog){
            this.processor = processor;
            this.transactingLog = transactingLog;
        }

        public Receipt chargeOrdr(PizzaOrder order, CreditCard creditCard) {

        	try{
            ChargeResult result = processor.charge(creditCard, order.getAmount());
            transactingLog.logChargeResult(result);

             return result.wasSuccessful() ? Receipt.forSuccessfulCharge(order.getAmount()) : Receipt.forDeclinedCharge(result.getDeclineMessage());
         	}catch(UnreachableException e){
         		transactionLog.logConnectException(e);
      			return Receipt.forSystemFailure(e.getMessage());
         	}
        }
}

public class BillingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TransactingLog.class).to(DatabaseTransactionLog.class);
        bind(CreditCardProcessor.class).to(PaypalCreditCardProcessor.class);
        bind(BillingService.class).to(BillingServiceImpl.class);
    }
}


public class BillingServiceImplTest extends TestCase {

    public void testChargeOrdr() throws Exception {
        PizzaOrder pizzaOrder = new PizzaOrder();
        pizzaOrder.setAmount(100);
        CreditCard creditCard = new CreditCard();
        creditCard.setAccount(20);
        Injector injector = Guice.createInjector(new BillingModule());
        BillingService billingService = injector.getInstance(BillingServiceImpl.class);

        billingService.chargeOrdr(pizzaOrder,creditCard);
    }
}

```


# ��

## ��δ���һ����


�̳�`AbstractModule`�����࣬��д���е�`configure`�������ڸ÷����е���`bind()`ȥ����ÿһ���󶨡�ʹ��`Guice.createInjector()`ȥ����һ��ע������Ȼ��ͨ����ע�������á�


## ��ʽ��(��ͨ��)

��ʽ���ǽ��ӿڰ󶨵�����ʵ���ϡ�ͬ��Ҳ֧����ʽ�����ӡ����磺

```java

public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
  	//����DatabaseTransactionLog��Transaction�����࣬MySqlDatabaseTransactionLog��DatabaseTransactionLog�����࣬��TransactionLog��Ҫ��ʱ��ע��������MySqlDatabaseTransactionLog
    bind(TransactionLog.class).to(DatabaseTransactionLog.class);
    bind(DatabaseTransactionLog.class).to(MySqlDatabaseTransactionLog.class);
  }
}

```


## �󶨵�ע��д��(���)

> Note��

> ����`@NAME`����ʽ����ʵ�����Ӱ�


֧�ֶ�ͬһ�ӿڵĶ�󶨣�һ����ʵ�ʵ�ʹ���в���`@NAME`����ʽ��

```java
public class BillingServiceImpl implements BillingService{


        private final CreditCardProcessor processor;
        private final TransactingLog transactingLog;

    @Inject
    BillingServiceImpl(CreditCardProcessor processor,@Named("Oracle") DataBaseTransactionLog transactingLog){
            this.processor = processor;
            this.transactingLog = transactingLog;
        }

        public Receipt chargeOrdr(PizzaOrder order, CreditCard creditCard) {
            ChargeResult result = processor.charge(creditCard, order.getAmount());
            transactingLog.logChargeResult(result);

             return result.wasSuccessful() ? Receipt.forSuccessfulCharge(order.getAmount()) : Receipt.forDeclinedCharge(result.getDeclineMessage());
        }

}


public class BillingModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(DatabaseTransactionLog.class).annotatedWith(Names.named("MySQL")).to(MySqlDatabaseTransactionLog.class);
        bind(DatabaseTransactionLog.class).annotatedWith(Names.named("Oracle")).to(OracleDatabaseTransactionLog.class);
        bind(CreditCardProcessor.class).to(PaypalCreditCardProcessor.class);
     //  bind(BillingService.class).to(BillingServiceImpl.class);
    }
}


```


## ������(�󶨳���)

��Ҫ�Ը�����󶨣������ϵͳ��������

```java

	bind(String.class).annotatedWith(Names.named("url")).toInstance("jdbc:mysql://localhost/card");
    bind(String.class).annotatedWith(Names.named("user")).toInstance("root");
    bind(String.class).annotatedWith(Names.named("password")).toInstance("root");
    bind(String.class).annotatedWith(Names.named("driver")).toInstance("com.mysql.jdbc.Driver");

```


## @Provider����(�����ͽ��г�ʼ��)

����ͨ��`@Provider`����������һ������

```java
public class BillingModule extends AbstractModule{

	@override
	protected void configure(){

	}

	@Provides @Named("MySQL")
    TransactingLog provideTransactionLog(@Named("url")String url, @Named("user")String user, @Named("password")String password, @Named("driver")String driver){
        MySqlDatabaseTransactionLog transactionLog = new MySqlDatabaseTransactionLog();
        transactionLog.setDriver(driver);
        transactionLog.setPassword(password);
        transactionLog.setUrl(url);
        transactionLog.setUser(user);
        return transactionLog;
    }

}

```

## Provider��(�����ͽ��г�ʼ��)

��� `@Provides`������ĺܸ��ӣ��Ϳ������¶���һ��`.toProvider`�İ���ʽ

> Note: ʹ��@Provides�����󣬳����󶨵ķ����Ͳ�������



```java

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


public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
   bind(TransactingLog.class).toProvider(MySQLTransactionLogProvider.class);
  }

}


```


## û�����ñ�ǩ�İ�(�Զ���)


�����ð󶨵Ķ���Ȼ����ʹ�õ�ʱ������`@ImplementedBy`����`@ProvidedBy`��������󶨵Ķ���.�����ֶ���Ҫ���������Զ��󶨵���ʽ������`@ProvidedBy`��Ҫ��`.toProvider`����ʹ�á�

```java

public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
     bind(TransactingLog.class);
     bind(CreditCardProcessor.class);
  }

}



@ProvidedBy(MySQLTransactionLogProvider.class)
public interface TransactingLog {

    public void  logChargeResult(ChargeResult chargeResult);
}

@ImplementedBy(PaypalCreditCardProcessor.class)
public interface CreditCardProcessor {

    public  ChargeResult charge(CreditCard creditCard, double amount);

}
```

## �������󶨣���̫����

```java

public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
    try {
      bind(TransactionLog.class).toConstructor(
          DatabaseTransactionLog.class.getConstructor(DatabaseConnection.class));
    } catch (NoSuchMethodException e) {
      addError(e);
    }
  }
}

```

## ���ð󶨣�ֻ֧��java.unit.logger��


```java

	@Singleton
	public class ConsoleTransactionLog implements TransactionLog {

  		private final Logger logger;

 		 @Inject
		public ConsoleTransactionLog(Logger logger) {
		    this.logger = logger;
 		}

  	public void logConnectException(UnreachableException e) {
    	logger.warning("Connect exception failed, " + e.getMessage());
  	}


  }
```