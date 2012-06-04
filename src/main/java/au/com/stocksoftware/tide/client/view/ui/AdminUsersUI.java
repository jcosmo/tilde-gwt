package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.activity.AdminUsersPresenter;
import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
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
import com.sencha.gxt.widget.core.client.event.SelectEvent;
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
  TextButton _addButton;

  @UiField
  TextButton _editButton;

  @UiField
  TextButton _passwordButton;

  @UiField
  TextButton _deleteButton;

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
    clearCurrentUser();
  }

  @Override
  public void clearUsers()
  {
    clearCurrentUser();
    _userList.getStore().clear();
  }

  @Override
  public void setPresenter( final AdminUsersPresenter presenter )
  {
    _presenter = presenter;
  }

  @Override
  public void showAddUser()
  {
    _userList.getSelectionModel().deselectAll( );
    updateValues( null );
    enableFields( true );
    _userLogin.focus();
    configureButtons( false, true, false, true, false );
  }

  @Override
  public void showUser( final UserVO user )
  {
    updateValues( user );
    enableFields( false );
    configureButtons( true, false, true, false, true );
  }

  @Override
  public void editUser( final UserVO user )
  {
    updateValues( user );
    enableFields( true );
    configureButtons( false, true, false, true, false );
  }

  @Override
  public void clearCurrentUser()
  {
    updateValues( null );
    enableFields( false );
    configureButtons( false, false, false, false, false );
    _userList.getSelectionModel().deselectAll( );
  }

  @Override
  public UserVO getCurrentValues()
  {
    return new UserVO( -1, _userLogin.getCurrentValue(), _userName.getCurrentValue(), _userEmail.getCurrentValue() );
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
  public void onAddUserClicked( final SelectEvent event )
  {
    _presenter.actionAddUser();
  }

  @UiHandler( { "_cancelButton" } )
  public void onCancelClicked( final SelectEvent event )
  {
    _presenter.actionCancel();
  }

  @UiHandler( { "_deleteButton" } )
  public void onDeleteClicked( final SelectEvent event )
  {
    _presenter.actionDelete();
  }

  @UiHandler( { "_editButton" } )
  public void onEditClicked( final SelectEvent event )
  {
    _presenter.actionEdit();
  }

  @UiHandler( { "_passwordButton" } )
  public void onPasswordClicked( final SelectEvent event )
  {
    _presenter.actionPassword();
  }

  @UiHandler( { "_saveButton" } )
  public void onSaveClicked( final SelectEvent event )
  {
    _presenter.actionSave();
  }

  public void onUserSelected( final SelectionEvent<UserVO> event )
  {
    _presenter.actionUserSelected( event.getSelectedItem() );
  }

  private void configureButtons( final boolean edit,
                                 final boolean cancel,
                                 final boolean password,
                                 final boolean save,
                                 final boolean delete )
  {
    _editButton.setVisible( edit );
    _cancelButton.setVisible( cancel );
    _passwordButton.setVisible( password );
    _saveButton.setVisible( save );
    _deleteButton.setVisible( delete );
    _userContentPanel.getButtonBar().syncSize();
    _userContentPanel.syncSize();
  }

  private void enableFields( final boolean enabled )
  {
    _userLogin.setEnabled( enabled );
    _userEmail.setEnabled( enabled );
    _userName.setEnabled( enabled );
  }

  private void updateValues( final UserVO user )
  {
    _userLogin.setValue( null == user ? "" : user.getLogin() );
    _userName.setValue( null == user ? "" : user.getName() );
    _userEmail.setValue( null == user ? "" : user.getEmail() );
  }
}
