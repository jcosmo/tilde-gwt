package au.com.stocksoftware.tide.client.test.util;

import au.com.stocksoftware.tide.client.ioc.TideMockGwtServicesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractClientTest
{
  private Injector _injector;
  private CalendarFactory _factory;

  @BeforeMethod
  public void preTest()
  {
    _injector =
      Guice.createInjector( new TideMockGwtServicesModule(), getTestModule() );
    _factory = new CalendarFactory();
  }

  protected AbstractModule getTestModule()
  {
    final String testModuleClassname = getClass().getName() + "$TestModule";
    try
    {
      return (AbstractModule) Class.forName( testModuleClassname ).newInstance();
    }
    catch ( final Throwable t )
    {
      return new ClientTestModule();
    }
  }

  protected final <T> T s( final Class<T> type )
  {
    return getService( type );
  }

  protected final <T> T s( final String name, final Class<T> type )
  {
    return getNamedService( name, type );
  }

  protected final CalendarFactory factory()
  {
    return _factory;
  }

  private <T> T getService( final Class<T> type )
  {
    return getInjector().getInstance( type );
  }

  private <T> T getNamedService( final String name, final Class<T> type )
  {
    return getInjector().getInstance( Key.get( type, Names.named( name ) ) );
  }

  protected final Injector getInjector()
  {
    return _injector;
  }
}


