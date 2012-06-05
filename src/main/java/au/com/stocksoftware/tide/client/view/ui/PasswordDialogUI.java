package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import au.com.stocksoftware.tide.client.view.AdminUsersView.Presenter;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

public class PasswordDialogUI
  implements IsWidget
{
  private Window _window;

  @UiField
  Label _userName;

  @UiField
  TextField _passwordField;

  @UiField
  TextButton _cancelButton;

  @UiField
  TextButton _saveButton;

  private UserVO _user;

  private Presenter _presenter;
  
  public void hide()
  {
    _window.hide();
  }

  @UiTemplate( "PasswordDialog.ui.xml" )
  interface PasswordDialogUiBinder
    extends UiBinder<Window, PasswordDialogUI>
  {
  }

  private static PasswordDialogUiBinder _uiBinder = GWT.create( PasswordDialogUiBinder.class );

  public PasswordDialogUI()
  {
    _window = _uiBinder.createAndBindUi( this );
  }

  public void show( final Presenter presenter, final UserVO user )
  {
    _presenter = presenter;
    _user = user;
    _userName.setText( user.getName() );
    _window.show();
    _passwordField.focus();
  }

  @Override
  public Widget asWidget()
  {
    return _window;
  }

  @UiHandler( "_cancelButton" )
  public void onCancelButtonClicked( final SelectEvent event )
  {
    _presenter.actionPasswordCancel( this );
  }

  @UiHandler( "_saveButton" )
  public void onSaveButtonClicked( final SelectEvent event )
  {
    _presenter.actionPasswordSave( this, _user, _passwordField.getText() );
  }
}
