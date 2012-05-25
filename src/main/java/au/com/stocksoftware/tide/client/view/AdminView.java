package au.com.stocksoftware.tide.client.view;

import com.google.gwt.user.client.ui.Label;

public interface AdminView
  extends View<AdminView.Presenter>
{
  Label getTaskLabel();

  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
  }
}
