package au.com.stocksoftware.tide.client.activity;

import au.com.stocksoftware.tide.client.data_type.core.UserDTO;
import au.com.stocksoftware.tide.client.service.core.UserService;
import au.com.stocksoftware.tide.client.service.util.Converter;
import au.com.stocksoftware.tide.client.view.AdminUsersView;
import au.com.stocksoftware.tide.client.view.AdminUsersView.Presenter;
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
}
