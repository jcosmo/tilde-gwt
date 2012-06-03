package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.activity.AdminUsersPresenter;
import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.form.TextField;
import java.util.List;

public class AdminUsersUI
  extends AbstractUI<AdminUsersView.Presenter>
  implements AdminUsersView, IsWidget
{
  private Component _widget;

  @UiField
  ListView _userList;

  @UiField
  Button _addButton;

  @UiField
  TextButton _editButton;

  @UiField
  TextButton _passwordButton;

  @UiField
  TextButton _saveButton;

  @UiField
  TextButton _cancelButton;

  @UiField
  TextField _userName;

  @UiField
  TextField _userEmail;

  @UiField
  TextField _userLogin;

  @UiField
  ContentPanel _userContentPanel;
  
  Presenter _presenter;

  @UiTemplate( "AdminUsersView.ui.xml" )
  interface AdminUsersViewUiBinder
    extends UiBinder<Component, AdminUsersUI>
  {
  }

  private static AdminUsersViewUiBinder _uiBinder = GWT.create( AdminUsersViewUiBinder.class );

  public AdminUsersUI()
  {
    _widget = _uiBinder.createAndBindUi( this );
  }

  @Override
  public void setUsers( final List<UserVO> users )
  {
    _userList.getStore().replaceAll( users );
    resetView();
  }

  @Override
  public void clearUsers()
  {
    _userList.getStore().clear();
    resetView();
  }

  private void resetView()
  {
    /*
    _editButton.setVisible( false );
    _cancelButton.setVisible( false );
    _passwordButton.setVisible( false );
    _saveButton.setVisible( false );
    */
    _userName.setText( "" );
    _userEmail.setText( "" );
    _userLogin.setText( "" );
  }

  @Override
  public void setPresenter( final AdminUsersPresenter presenter )
  {
    _presenter = presenter;
  }

  @Override
  public void showAddUser()
  {
    resetView();
    _userName.focus();
    _addButton.setVisible( true );
    _cancelButton.setVisible( true );
  }

  @Override
  public void showUser( final UserVO user )
  {
    resetView();
    _userLogin.setText( user.getLogin() );
    _userName.setText( user.getName() );
    _userEmail.setText( user.getEmail() );
    _editButton.setVisible( true );
    _passwordButton.setVisible( true );
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

    final ListView<UserVO, String> list = new ListView<UserVO, String>(
      new ListStore<UserVO>( userProperties.key() ), userProperties.name() );

    list.getSelectionModel().addSelectionHandler( new SelectionHandler<UserVO>() {
      @Override
      public void onSelection( final SelectionEvent<UserVO> userVOSelectionEvent )
      {
        onUserSelected( userVOSelectionEvent );
      }
    } );

    return list;
  }

  interface UserProperties
    extends PropertyAccess<UserVO>
  {
    @Path( "id" )
    ModelKeyProvider<UserVO> key();

    @Path( "name" )
    ValueProvider<UserVO, String> name();
  }

  @UiHandler( { "_addButton" } )
  public void onAddUserClicked( final ClickEvent event )
  {
    _presenter.addUserPressed();
  }

  public void onUserSelected( final SelectionEvent<UserVO> event )
  {
    _presenter.userSelected( event.getSelectedItem() );
  }
}
