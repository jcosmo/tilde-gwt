package au.com.stocksoftware.tide.client.ioc;

import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.SimplePanel;

@GinModules( { TideClientModule.class, TideGwtServicesModule.class, TideGwtRpcServicesModule.class } )
public interface TideGinjector
  extends Ginjector
{
  Application getApplication();

  SimplePanel getMainPanel();
  
  PlaceHistoryHandler getPlaceHistoryHandler();
}
