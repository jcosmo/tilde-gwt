package au.com.stocksoftware.tide.client.view;

import au.com.stocksoftware.tide.client.entity.UserVO;
import java.util.List;

public interface AdminUsersView
  extends View<AdminUsersView.Presenter>
{
  void setUsers( List<UserVO> users );

  void clearUsers();

  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
  }
}
