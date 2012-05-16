package au.com.stocksoftware.tide.server.test.util;

import au.com.stocksoftware.tide.server.service.core.UserService;
import au.com.stocksoftware.tide.server.service.tide.UserServiceEJB;
import com.google.inject.AbstractModule;
import javax.inject.Singleton;

public class ServerTestModule
  extends AbstractModule
{
  @Override
  protected void configure()
  {
    bindSingleton( UserService.class, UserServiceEJB.class );
  }

  private <T> void bindSingleton( final Class<T> service, final Class<? extends T> implementation )
  {
    bind( service ).to( implementation ).in( Singleton.class );
  }
}
