package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.activity.AdminProjectsPresenter;
import au.com.stocksoftware.tide.client.view.AdminProjectsView;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class AdminProjectsPanel
  extends SimplePanel
{
  private AdminProjectsPresenter _presenter;

  @Inject
  public AdminProjectsPanel( final AdminProjectsPresenter presenter, final AdminProjectsView view )
  {
    _presenter = presenter;
    view.setPresenter( presenter );
    add( view.asWidget() );
  }

  public void notifyShown()
  {
    _presenter.refreshProjects();
  }
}
