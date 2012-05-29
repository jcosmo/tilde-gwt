package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.view.AdminView;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.container.Viewport;

public class AdminPanel
  extends Viewport
{
  @Inject
  public AdminPanel( final AdminView view )
  {
    add( view.asWidget() );
  }
}
