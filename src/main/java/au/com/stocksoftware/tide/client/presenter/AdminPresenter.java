package au.com.stocksoftware.tide.client.presenter;

import au.com.stocksoftware.tide.client.data_type.core.UserDTO;
import au.com.stocksoftware.tide.client.entity.UserVO;
import au.com.stocksoftware.tide.client.service.core.UserService;
import au.com.stocksoftware.tide.client.service.util.Converter;
import au.com.stocksoftware.tide.client.view.AdminView;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.List;
import org.realityforge.replicant.client.AsyncCallback;

public class AdminPresenter
  implements AdminView.Presenter
{

  @Override
  public void onUserClicked( final int userID )
  {
  }

  private AdminView _view;

  private UserService _userService;
  private EventBus _eventBus;
  private List<UserVO> _users;

  @Inject
  public AdminPresenter( final AdminView view,
                         final UserService userService,
                         final EventBus eventBus )
  {
    _view = view;
    _view.bind( this );
    _userService = userService;
    _eventBus = eventBus;

    _userService.getUsers( new AsyncCallback<List<UserDTO>>()
    {
      @Override
      public void onSuccess( final List<UserDTO> userDTOs )
      {
        handleUsersLoaded( userDTOs );
      }
    } );
  }

  private void handleUsersLoaded( final List<UserDTO> userDTOs )
  {
    _users = Converter.userDTOsToVOs( userDTOs );
  }
}

