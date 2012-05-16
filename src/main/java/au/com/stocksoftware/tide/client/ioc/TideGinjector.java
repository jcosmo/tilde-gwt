package au.com.stocksoftware.tide.client.ioc;

import au.com.stocksoftware.tide.client.view.ui.AdminPanel;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules( { TideClientModule.class, TideGwtServicesModule.class, TideGwtRpcServicesModule.class } )
public interface TideGinjector
  extends Ginjector
{
  AdminPanel getMainPanel();
}
