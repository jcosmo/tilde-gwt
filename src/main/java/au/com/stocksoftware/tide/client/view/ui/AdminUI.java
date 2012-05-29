package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.place.AdminPlace;
import au.com.stocksoftware.tide.client.place.AdminPlace.AdminTask;
import au.com.stocksoftware.tide.client.view.AdminView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class AdminUI
  extends AbstractUI<AdminView.Presenter>
  implements AdminView, IsWidget
{
  private Component _widget;

  @UiField
  SimpleContainer _contentArea;

  @UiField
  Hyperlink _usersLink;

  @UiField
  Hyperlink _projectsLink;

  private Label _projectsUI;

  @Inject
  private AdminUsersPanel _usersPanel;

  private AdminTask _currentView;

  @Override
  public void showTask( final AdminTask task )
  {
    if ( _currentView == task )
    {
      return;
    }
    if ( null != _currentView )
    {
      _contentArea.remove( 0 );
    }

    switch ( task )
    {
      case PROJECTS:
        _contentArea.setWidget( ensureProjectUI() );
        _currentView = task;
        break;

      case USERS:
        _contentArea.setWidget( _usersPanel );
        _currentView = task;
        _usersPanel.notifyShown();
        break;
    }
  }

  private IsWidget ensureProjectUI()
  {
    if ( null == _projectsUI )
    {
      _projectsUI = new Label( "This is the Projects UI!" );
    }
    return _projectsUI;
  }

  @UiTemplate( "AdminView.ui.xml" )
  interface AdminViewUiBinder
    extends UiBinder<Component, AdminUI>
  {

  }

  private static AdminViewUiBinder _uiBinder = GWT.create( AdminViewUiBinder.class );

  @Inject
  public AdminUI( final PlaceHistoryMapper placeHistoryMapper )
  {
    _widget = _uiBinder.createAndBindUi( this );
    _usersLink.setTargetHistoryToken( placeHistoryMapper.getToken( new AdminPlace( AdminTask.USERS ) ) );
    _projectsLink.setTargetHistoryToken( placeHistoryMapper.getToken( new AdminPlace( AdminTask.PROJECTS ) ) );
  }

  @Override
  public Widget asWidget()
  {
    return _widget;
  }
}
