# 开始

定义一个付账的业务,我们发现这个业务依赖于`CreditCardProcessor`和`TransactionLog`类型的对象，采用Guice框架对该对象的进行绑定。类似`Spring`的依赖注入。


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


# 绑定

## 如何创建一个绑定


继承`AbstractModule`抽象类，重写其中的`configure`方法。在该方法中调用`bind()`去声明每一个绑定。使用`Guice.createInjector()`去创建一个注入器，然后通过该注入器调用。


## 链式绑定(普通绑定)

链式绑定是将接口绑定到他的实现上。同样也支持链式的链接。例如：

```java

public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
  	//其中DatabaseTransactionLog是Transaction的子类，MySqlDatabaseTransactionLog是DatabaseTransactionLog的子类，当TransactionLog被要求时，注入器返回MySqlDatabaseTransactionLog
    bind(TransactionLog.class).to(DatabaseTransactionLog.class);
    bind(DatabaseTransactionLog.class).to(MySqlDatabaseTransactionLog.class);
  }
}

```


## 绑定的注释写法(多绑定)

> Note：

> 采用`@NAME`的形式不能实现链接绑定


支持对同一接口的多绑定，一般在实际的使用中采用`@NAME`的形式。

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


## 常量绑定(绑定常量)

不要对复杂类绑定，会减慢系统的启动。

```java

	bind(String.class).annotatedWith(Names.named("url")).toInstance("jdbc:mysql://localhost/card");
    bind(String.class).annotatedWith(Names.named("user")).toInstance("root");
    bind(String.class).annotatedWith(Names.named("password")).toInstance("root");
    bind(String.class).annotatedWith(Names.named("driver")).toInstance("com.mysql.jdbc.Driver");

```


## @Provider方法(对类型进行初始化)

可以通过`@Provider`方法来创建一个对象。

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

## Provider绑定(对类型进行初始化)

如果 `@Provides`方法变的很复杂，就可以重新定义一个`.toProvider`的绑定形式

> Note: 使用@Provides方法后，常量绑定的方法就不适用啦



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


## 没有设置标签的绑定(自动绑定)


不设置绑定的对象，然后在使用的时候利用`@ImplementedBy`或者`@ProvidedBy`来定义其绑定的对象.但是手动绑定要优于这种自动绑定的形式。其中`@ProvidedBy`需要和`.toProvider`联合使用。

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

## 构造器绑定（不太懂）

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

## 内置绑定（只支持java.unit.logger）


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