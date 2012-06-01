package au.com.stocksoftware.tide.client.view;

import au.com.stocksoftware.tide.client.activity.AdminUsersPresenter;
import au.com.stocksoftware.tide.client.entity.UserVO;
import java.util.List;

public interface AdminUsersView
  extends View<AdminUsersView.Presenter>
{
  void setUsers( List<UserVO> users );

  void clearUsers();

  void setPresenter( AdminUsersPresenter presenter );

  void showAddUser();

  void showUser( UserVO user );

  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
    void addUserPressed();
    
    void userSelected( UserVO user );
  }
}
