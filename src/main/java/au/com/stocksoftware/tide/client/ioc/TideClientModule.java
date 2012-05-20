package au.com.stocksoftware.tide.client.ioc;

import au.com.stocksoftware.tide.client.presenter.AdminPresenter;
import au.com.stocksoftware.tide.client.presenter.GlobalAsyncCallback;
import au.com.stocksoftware.tide.client.view.AdminView;
import au.com.stocksoftware.tide.client.view.Presenter;
import au.com.stocksoftware.tide.client.view.View;
import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import au.com.stocksoftware.tide.client.view.ui.AdminUI;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.name.Names;

public class TideClientModule
  extends AbstractGinModule
{
  protected void configure()
  {
    bind( EventBus.class ).to( SimpleEventBus.class ).asEagerSingleton();
    bind( AdminPanel.class ).asEagerSingleton();
    //bindPresenter( AdminView.Presenter.class, AdminPresenter.class, AdminView.class, AdminUI.class );
    bindNamedService( "GLOBAL", AsyncCallback.class, GlobalAsyncCallback.class );
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
}