package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.activity.AdminUsersPresenter;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class AdminUsersPanel
  extends SimplePanel
{
  private AdminUsersPresenter _presenter;

  @Inject
  public AdminUsersPanel( final AdminUsersPresenter presenter, final AdminUsersView view )
  {
    _presenter = presenter;
    add( view.asWidget() );
  }

  public void notifyShown()
  {
    _presenter.refreshUsers();
  }
}
