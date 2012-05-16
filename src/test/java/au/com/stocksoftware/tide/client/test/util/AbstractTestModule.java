package au.com.stocksoftware.tide.client.test.util;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import javax.inject.Singleton;
import static org.mockito.Mockito.mock;

public class AbstractTestModule
  extends AbstractModule
  {
    @Override
    protected void configure()
    {
      bindStandardServices();
    }

    protected final void bindStandardServices()
    {
      bindService( EventBus.class, SimpleEventBus.class );
    }

    protected final <T> void bindNamedService( final String name,
                                               final Class<T> service,
                                               final Class<? extends T> implementation )
    {
      bind( service ).annotatedWith( Names.named( name ) ).to( implementation ).asEagerSingleton();
    }

    protected final <T> void bindService( final Class<T> viewClass, final Class<? extends T> implementation )
    {
      bind( viewClass ).to( implementation ).in( Singleton.class );
    }

    protected final <T> void bindMockService( final Class<T> classToMock )
    {
      bind( classToMock ).toInstance( mock( classToMock ) );
    }

    protected final <T> void bindNamedMockService( final String name, final Class<T> classToMock )
    {
      bind( classToMock ).annotatedWith( Names.named( name ) ).toInstance( mock( classToMock ) );
    }
}
