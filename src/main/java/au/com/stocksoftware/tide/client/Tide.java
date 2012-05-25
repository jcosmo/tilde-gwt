package au.com.stocksoftware.tide.client;

import au.com.stocksoftware.tide.client.ioc.TideGinjector;
import au.com.stocksoftware.tide.client.ioc.TideGwtRpcServicesModule;
import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public final class Tide
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    TideGwtRpcServicesModule.initialize( GWT.getModuleName(), null );
    final TideGinjector injector = GWT.create( TideGinjector.class );

    injector.getApplication().activate();

    final SimplePanel mainPanel = injector.getMainPanel();

    RootPanel.get().add( mainPanel );

      // Goes to place represented on URL or default place
    injector.getPlaceHistoryHandler().handleCurrentHistory();
  }
}