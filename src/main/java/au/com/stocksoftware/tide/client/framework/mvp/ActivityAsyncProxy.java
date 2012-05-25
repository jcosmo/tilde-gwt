package au.com.stocksoftware.tide.client.framework.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Add a proxy around activities so that they can act as split points.
 * Based on a <a href="http://ars-codia.raphaelbauer.com/2011/04/gwt-gin-and-simple-split-points.html">blog post</a>.
 */
public final class ActivityAsyncProxy<T extends Activity>
  implements Activity, PlaceAware
{
  private final AsyncProvider<T> _provider;
  private T _activity;
  private boolean _canceled;
  private Place _place;

  @SuppressWarnings( { "ConstantConditions" } )
  @Inject
  public ActivityAsyncProxy( @Nonnull final AsyncProvider<T> provider )
  {
    assert ( null != provider );
    _provider = provider;
  }

  @Override
  public <P extends Place> boolean atPlace( final P place )
  {
    if ( null == _activity )
    {
      _place = place;
      return true;
    }
    else if ( _activity instanceof PlaceAware )
    {
      return ( (PlaceAware) _activity ).atPlace( place );
    }
    else
    {
      return false;
    }
  }

  @Override
  public String mayStop()
  {
    if ( null != _activity )
    {
      return _activity.mayStop();
    }
    else
    {
      return null;
    }
  }

  @Override
  public void onCancel()
  {
    if ( null != _activity )
    {
      _activity.onCancel();
    }
    else
    {
      _canceled = true;
    }
  }

  @Override
  public void onStop()
  {
    if ( null != _activity )
    {
      _activity.onStop();
    }
  }

  @Override
  public void start( final AcceptsOneWidget panel, final EventBus eventBus )
  {
    if ( null != _activity )
    {
      _activity.start( panel, eventBus );
    }
    else
    {
      _provider.get( new AsyncCallback<T>()
      {
        @SuppressWarnings( { "ConstantConditions" } )
        @Override
        public void onFailure( final Throwable caught )
        {
          //Should never get here...
          assert ( false );
        }

        @Override
        public void onSuccess( final T result )
        {
          if ( !_canceled )
          {
            _activity = result;
            if ( _activity instanceof PlaceAware )
            {
              final boolean updated = ( (PlaceAware) _activity ).atPlace( _place );
              assert ( updated );
            }
            _activity.start( panel, eventBus );
          }
        }
      } );
    }
  }
}