package au.com.stocksoftware.tide.client.ioc;

import au.com.stocksoftware.tide.client.activity.AdminActivity;
import au.com.stocksoftware.tide.client.activity.AdminUsersPresenter;
import au.com.stocksoftware.tide.client.place.AdminPlace;
import au.com.stocksoftware.tide.client.place.AdminPlace.AdminTask;
import au.com.stocksoftware.tide.client.presenter.GlobalAsyncCallback;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import au.com.stocksoftware.tide.client.view.AdminView;
import au.com.stocksoftware.tide.client.view.Presenter;
import au.com.stocksoftware.tide.client.view.View;
import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import au.com.stocksoftware.tide.client.view.ui.AdminUI;
import au.com.stocksoftware.tide.client.view.ui.AdminUsersUI;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import javax.inject.Singleton;

public class TideClientModule
  extends AbstractGinModule
{
  protected void configure()
  {
    bind( EventBus.class ).to( SimpleEventBus.class ).asEagerSingleton();
    bind( SimplePanel.class ).asEagerSingleton();
    bind( PlaceHistoryMapper.class ).to( ApplicationPlaceHistoryMapper.class ).asEagerSingleton();
    bind( ApplicationActivityMapper.class ).asEagerSingleton();

    bindPresenter( AdminView.Presenter.class, AdminActivity.class, AdminView.class, AdminUI.class );
    bindPresenter( AdminUsersView.Presenter.class, AdminUsersPresenter.class, AdminUsersView.class, AdminUsersUI.class );
    bindNamedService( "GLOBAL", AsyncCallback.class, GlobalAsyncCallback.class );

    bind( Application.class ).asEagerSingleton();
  }

  private <T> void bindNamedService( final String name,
                                     final Class<T> service,
                                     final Class<? extends T> implementation )
  {
    bind( service ).annotatedWith( Names.named( name ) ).to( implementation ).asEagerSingleton();
  }

  private <T> void bindService( final Class<T> service, final Class<? extends T> implementation )
  {
    bind( service ).to( implementation ).asEagerSingleton();
    bind( implementation ).asEagerSingleton();
  }

  private <P extends Presenter, V extends View<P>, U extends V, A extends P>
  void bindPresenter( final Class<P> presenter,
                      final Class<A> presenterImplementation,
                      final Class<V> view,
                      final Class<U> ui )
  {
    bindService( view, ui );
    bindService( presenter, presenterImplementation );
  }

  @Provides
  @Singleton
  public PlaceHistoryHandler getHistoryHandler( final PlaceController placeController,
                                                final PlaceHistoryMapper historyMapper,
                                                final EventBus eventBus )
  {
    final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler( historyMapper );
    historyHandler.register( placeController, eventBus, new AdminPlace( AdminTask.START ) );
    return historyHandler;
  }

  @Provides
  @Singleton
  public PlaceController getPlaceController( final EventBus eventBus )
  {
    return new PlaceController( eventBus );
  }
}