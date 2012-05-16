package au.com.stocksoftware.tide.server.test.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import org.realityforge.guiceyloops.JEETestingModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class AbstractServerTest
{
  private TideFactory _factory;
  private Injector _injector;

  @BeforeMethod
  public void preTest()
    throws NamingException
  {
    _injector = Guice.createInjector( getTestModule(), new EntityManagerTestingModule(), new JEETestingModule() );
    _factory = new TideFactory( _injector );
    em().getTransaction().begin();
  }

  @AfterMethod
  public void postTest()
  {
    em().getTransaction().rollback();
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
      return new ServerTestModule();
    }
  }

  protected final TideFactory factory()
  {
    return _factory;
  }

  protected final <T> T s( final Class<T> type )
  {
    // Flush the entity manager prior to invoking the service. Ensures that the service method can
    // find all created artifacts
    flush();
    return getService( type );
  }

  protected final void flush()
  {
    em().flush();
  }

  protected final void clear()
  {
    em().clear();
  }

  protected final <T> T refresh( final T entity )
  {
    em().refresh( entity );
    return entity;
  }

  protected final EntityManager em()
  {
    return getService( EntityManager.class );
  }

  private <T> T getService( final Class<T> type )
  {
    return getInjector().getInstance( type );
  }

  protected final Injector getInjector()
  {
    return _injector;
  }
}
