package au.com.stocksoftware.tide.client.framework.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import javax.inject.Provider;
import java.util.HashMap;

/**
 * Abstract ActivityMapper to simplify binding activity mappers to places.
 *
 * Strongly inspired by the SimpleActivityMapper in the ginmvp project.
 */
public abstract class AbstractActivityMapper
  implements ActivityMapper
{
  private final HashMap<Class<? extends Place>, Provider<? extends Activity>> _providers =
    new HashMap<Class<? extends Place>, Provider<? extends Activity>>();

  private Activity _currentActivity;

  @Override
  public Activity getActivity( final Place place )
  {
    if ( null != _currentActivity && _currentActivity instanceof PlaceAware )
    {
      if ( ( (PlaceAware) _currentActivity ).atPlace( place ) )
      {
        return _currentActivity;
      }
      _currentActivity = null;
    }
    final Provider<? extends Activity> provider = _providers.get( place.getClass() );
    if ( null != provider )
    {
      final Activity activity = provider.get();
      if ( activity instanceof PlaceAware )
      {
        final boolean changed = ( (PlaceAware) activity ).atPlace( place );
        assert ( changed );
      }
      _currentActivity = activity;
      return activity;
    }
    return null;
  }

  protected final <P extends Place, A extends Activity>
  void registerProvider( final Class<P> placeClass, final Provider<A> provider )
  {
    _providers.put( placeClass, provider );
  }
}