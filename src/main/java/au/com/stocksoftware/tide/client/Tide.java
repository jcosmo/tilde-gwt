package au.com.stocksoftware.tide.client;

import au.com.stocksoftware.tide.client.ioc.TideGinjector;
import au.com.stocksoftware.tide.client.ioc.TideGwtRpcServicesModule;
import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import au.com.stocksoftware.tide.client.view.ui.AdminUI;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public final class Tide
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    //TideGwtRpcServicesModule.initialize( GWT.getModuleName(), null );
    //final TideGinjector injector = GWT.create( TideGinjector.class );

    //final AdminPanel mainPanel = injector.getMainPanel();
    RootLayoutPanel.get().add( new AdminUI() );
  }
}