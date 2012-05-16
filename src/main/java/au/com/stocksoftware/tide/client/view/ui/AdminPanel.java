package au.com.stocksoftware.tide.client.view.ui;

import com.google.gwt.user.client.ui.SimplePanel;
import au.com.stocksoftware.tide.client.view.AdminView;
import javax.inject.Inject;

public class AdminPanel
  extends SimplePanel
{
  @Inject
  public AdminPanel( final AdminView view )
  {
    add( view.asWidget() );
  }
}
