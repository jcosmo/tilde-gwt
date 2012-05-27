package au.com.stocksoftware.tide.client.view;

import au.com.stocksoftware.tide.client.place.AdminPlace.AdminTask;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public interface AdminView
  extends View<AdminView.Presenter>
{
  void showTask( AdminTask task );

  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
  }
}
