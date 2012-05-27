package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.place.AdminPlace;
import au.com.stocksoftware.tide.client.place.AdminPlace.AdminTask;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Component;

public class AdminUsersUI
  extends AbstractUI<AdminUsersView.Presenter>
  implements AdminUsersView, IsWidget
{
  private Component _widget;
  @UiTemplate( "AdminUsersView.ui.xml" )
  interface AdminUsersViewUiBinder
    extends UiBinder<Component, AdminUsersUI>
  {
  }

  private static AdminUsersViewUiBinder _uiBinder = GWT.create( AdminUsersViewUiBinder.class );

  @Inject
  public AdminUsersUI( )
  {
    _widget = _uiBinder.createAndBindUi( this );
  }

  @Override
  public Widget asWidget()
  {
    return _widget;
  }
}
