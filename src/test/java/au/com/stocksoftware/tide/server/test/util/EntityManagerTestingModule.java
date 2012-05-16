package au.com.stocksoftware.tide.server.test.util;

import au.com.stocksoftware.tide.server.entity.core.CoreCatalog;
import com.google.inject.AbstractModule;
import java.lang.reflect.Field;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.sessions.Session;

public class EntityManagerTestingModule
  extends AbstractModule
{
  private static final String UNIT_NAME = "Tide";

  @Override
  protected void configure()
  {
    final Properties dbProperties = DatabaseUtil.initDatabaseProperties();
    final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory( UNIT_NAME, dbProperties );
    final EntityManager em = entityManagerFactory.createEntityManager();
    bind( EntityManager.class ).toInstance( em );

    final Session session = em.unwrap( Session.class );
    requestInjectionForEntityListeners( CoreCatalog.MODELS, session );
  }

  private void requestInjectionForEntityListeners( final Class[] models, final Session session )
  {
    for ( final Class model : models )
    {
      final ClassDescriptor descriptor = session.getClassDescriptor( model );
      for ( final Object o : descriptor.getEventManager().getDefaultEventListeners() )
      {
        System.out.println( "listener: " + o.toString() );
        final Object instance = toEntityListener( (DescriptorEventListener) o );
        requestInjection( instance );
      }
    }
  }

  private Object toEntityListener( final DescriptorEventListener listener )
  {
    try
    {
      final Field field = listener.getClass().getDeclaredField( "m_listener" );
      field.setAccessible( true );
      return field.get( listener );
    }
    catch ( final Throwable t )
    {
      throw new IllegalStateException( "Error retrieving listener", t );
    }
  }
}
