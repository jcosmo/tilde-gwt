package au.com.stocksoftware.tide.server.service.tide;

import au.com.stocksoftware.tide.server.data_type.core.UserDTO;
import au.com.stocksoftware.tide.server.entity.core.User;
import au.com.stocksoftware.tide.server.entity.core.dao.UserDAO;
import au.com.stocksoftware.tide.server.service.core.UserService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless( name = UserService.EJB_NAME )
public class UserServiceEJB
  implements UserService
{
  @EJB
  private UserDAO _userDAO;


  @Override
  @Nonnull
  public List<UserDTO> getUsers()
  {
    final List<User> users = _userDAO.findAll();
    final List<UserDTO> result = new ArrayList<UserDTO>( users.size() ); 
    for ( final User user : users )
    {
      result.add( new UserDTO( user.getID(), user.getName() ) );
    }
    return result;
  }

  @Override
  @Nonnull
  public UserDTO addUser( @Nonnull final String name, @Nonnull final String password )
  {
    final User user = new User();
    user.setName( name );
    user.setPassword( password );
    _userDAO.persist( user );

    return new UserDTO( user.getID(), user.getName() );
  }

  @Override
  public void setPassword( @Nonnull final User user, @Nonnull final String password )
  {
    user.setPassword( password );
    _userDAO.persist( user );
  }
}
