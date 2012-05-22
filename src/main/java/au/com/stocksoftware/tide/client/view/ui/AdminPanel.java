package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.view.AdminView;
import com.sencha.gxt.widget.core.client.container.Viewport;
import javax.inject.Inject;

public class AdminPanel
  extends Viewport
{
  @Inject
  public AdminPanel( final AdminView view )
  {
    add( view.asWidget() );
  }
}
