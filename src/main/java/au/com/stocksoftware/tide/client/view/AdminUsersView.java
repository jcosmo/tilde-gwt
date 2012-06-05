package au.com.stocksoftware.tide.client.view;

import au.com.stocksoftware.tide.client.activity.AdminUsersPresenter;
import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.view.ui.PasswordDialogUI;
import java.util.List;

public interface AdminUsersView
  extends View<AdminUsersView.Presenter>
{
  void setUsers( List<UserVO> users );

  void clearUsers();

  void setPresenter( AdminUsersPresenter presenter );

  void showAddUser();

  void showUser( UserVO user );

  void clearCurrentUser();
  
  UserVO getCurrentValues();

  void editUser( UserVO user );

  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
    void actionAddUser();
    
    void actionUserSelected( UserVO user );

    void actionCancel();

    void actionEdit();

    void actionSave();

    void actionPassword();

    void actionDelete();

    void actionPasswordCancel( final PasswordDialogUI dialog );

    void actionPasswordSave( final PasswordDialogUI dialog, UserVO user, String newPassword );
  }
}
