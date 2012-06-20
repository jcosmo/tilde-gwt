package au.com.stocksoftware.tide.client.activity;

import au.com.stocksoftware.tide.client.data_type.core.UserDTO;
import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.service.core.UserService;
import au.com.stocksoftware.tide.client.service.util.Converter;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import au.com.stocksoftware.tide.client.view.AdminUsersView.Presenter;
import au.com.stocksoftware.tide.client.view.ui.PasswordDialogUI;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import java.util.List;
import org.realityforge.replicant.client.AsyncCallback;

public class AdminUsersPresenter
  implements Presenter
{
  @Inject
  private AdminUsersView _view;

  @Inject
  private UserService _userService;

  private UserVO _currentUser;
  private boolean _newUser;

  public void refreshUsers()
  {
    _view.clearUsers();

    _userService.getUsers(
      new AsyncCallback<List<UserDTO>>()
      {
        @Override
        public void onSuccess( final List<UserDTO> result )
        {
          _view.setUsers( Converter.userDTOsToVOs( result ) );
        }
      } );
  }

  @Override
  public void actionAddUser()
  {
    _newUser = true;
    _currentUser = null;
    _view.showAddUser();
  }

  @Override
  public void actionUserSelected( final UserVO user )
  {
    _newUser = false;
    _currentUser = user;
    _view.showUser( _currentUser );
  }

  @Override
  public void actionCancel()
  {
    _newUser = false;
    _currentUser = null;
    _view.clearCurrentUser();
  }

  @Override
  public void actionEdit()
  {
    if ( null != _currentUser )
    {
      _view.editUser( _currentUser );
    }
  }

  @Override
  public void actionSave()
  {
    final UserVO newValues = _view.getCurrentValues();
    if ( null != _currentUser )
    {
      _userService.updateUser( _currentUser.getId(), newValues.getLogin(), newValues.getName(), newValues.getEmail(),
                               newValues.getUserType(),
                               new AsyncCallback<UserDTO>()
                               {
                                 @Override
                                 public void onSuccess( final UserDTO updatedUser )
                                 {
                                   _currentUser = null;
                                   _view.clearCurrentUser();
                                   refreshUsers();
                                 }
                               } );
    }
    else if ( _newUser )
    {
      _userService.addUser( newValues.getLogin(), newValues.getName(), "password", newValues.getEmail(),
                            newValues.getUserType(),
                            new AsyncCallback<UserDTO>()
                            {
                              @Override
                              public void onSuccess( final UserDTO newUser )
                              {
                                _newUser = false;
                                _view.clearCurrentUser();
                                refreshUsers();
                              }
                            } );
    }
  }

  @Override
  public void actionPassword()
  {
    if ( null != _currentUser )
    {
      final PasswordDialogUI passwordDialog = GWT.create( PasswordDialogUI.class );
      passwordDialog.show( this, _currentUser );
    }
  }

  @Override
  public void actionDelete()
  {
    if ( null != _currentUser )
    {
      _userService.deleteUser( _currentUser.getId(),
                               new AsyncCallback<Void>()
                               {
                                 @Override
                                 public void onSuccess( final Void ignored )
                                 {
                                   _currentUser = null;
                                   _view.clearCurrentUser();
                                   refreshUsers();
                                 }
                               } );
    }
  }

  @Override
  public void actionPasswordCancel( final PasswordDialogUI dialog )
  {
    dialog.hide();
  }

  @Override
  public void actionPasswordSave( final PasswordDialogUI dialog, final UserVO user, final String newPassword )
  {
    _userService.setPassword( user.getId(), newPassword,
                             new AsyncCallback<Void>()
                             {
                               @Override
                               public void onSuccess( final Void ignored )
                               {
                                 dialog.hide();
                               }
                             } );
  }
}
