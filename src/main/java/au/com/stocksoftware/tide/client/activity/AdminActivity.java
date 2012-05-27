package au.com.stocksoftware.tide.client.activity;

import au.com.stocksoftware.tide.client.framework.mvp.PlaceAware;
import au.com.stocksoftware.tide.client.place.AdminPlace;
import au.com.stocksoftware.tide.client.place.AdminPlace.AdminTask;
import au.com.stocksoftware.tide.client.view.AdminView;
import au.com.stocksoftware.tide.client.view.AdminView.Presenter;
import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class AdminActivity
  extends AbstractActivity
  implements PlaceAware, Presenter
{
  private AdminTask _currentTask;

  @Inject
  private AdminView _view;
  
  @Override
  public void start( final AcceptsOneWidget panel, final EventBus eventBus )
  {
    _view.bind( this );
    panel.setWidget( new AdminPanel( _view ) );
  }

  @Override
  public <P extends Place> boolean atPlace( final P place )
  {
    if ( place instanceof AdminPlace )
    {
      final AdminPlace adminPlace = (AdminPlace) place;
      if ( adminPlace.getTask() == _currentTask )
      {
        return true;
      }

      _view.showTask( adminPlace.getTask() );

      _currentTask = adminPlace.getTask();

      return true;
    }
    return false;
  }
}
