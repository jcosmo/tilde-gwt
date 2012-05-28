package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ListView;

public class AdminUsersUI
  extends AbstractUI<AdminUsersView.Presenter>
  implements AdminUsersView, IsWidget
{
  private Component _widget;

  @UiField
  ListView _userList;
  
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

  @UiFactory
  ListView createListView()
  {
    final UserProperties userProperties = GWT.create( UserProperties.class );

    return new ListView<UserVO, String>(
      new ListStore<UserVO>( userProperties.key() ), userProperties.name() );
  }

  interface UserProperties extends PropertyAccess<UserVO>
  {
    @Path("id")
    ModelKeyProvider<UserVO> key();

    @Path("name")
    ValueProvider<UserVO, String> name();
  }
}
