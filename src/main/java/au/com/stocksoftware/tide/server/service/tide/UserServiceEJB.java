package au.com.stocksoftware.tide.server.service.tide;

import au.com.stocksoftware.tide.server.data_type.core.UserDTO;
import au.com.stocksoftware.tide.server.data_type.core.UserType;
import au.com.stocksoftware.tide.server.entity.core.User;
import au.com.stocksoftware.tide.server.entity.core.UserComparator;
import au.com.stocksoftware.tide.server.entity.core.dao.UserDAO;
import au.com.stocksoftware.tide.server.service.core.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.annotation.security.DeclareRoles;

@Stateless( name = UserService.EJB_NAME )
@DeclareRoles("Authenticated")
public class UserServiceEJB
  implements UserService
{
  private static final Logger LOG = Logger.getLogger( UserServiceEJB.class.getName() );

  @EJB
  private UserDAO _userDAO;

  @Resource
  private EJBContext _sessionContext;

  @Override
  @Nonnull
  public List<UserDTO> getUsers()
  {
    LOG.severe(" Principal Name : " + _sessionContext.getCallerPrincipal().getName());
    LOG.severe(" Principal ToString : " + _sessionContext.getCallerPrincipal().toString());
    LOG.severe(" Is Authenticated : " + _sessionContext.isCallerInRole("Authenticated"));
    LOG.severe(" Is FooBar : " + _sessionContext.isCallerInRole("FooBar"));
    final List<User> users = _userDAO.findAll();
    Collections.sort( users, UserComparator.COMPARATOR );

    final List<UserDTO> result = new ArrayList<UserDTO>( users.size() );
    for ( final User user : users )
    {
      result.add( new UserDTO( user.getID(), user.getLogin(), user.getName(), user.getEmail(), user.getUserType() ) );
    }
    return result;
  }

  @Override
  @Nonnull
  public UserDTO addUser( @Nonnull final String login,
                          @Nonnull final String name,
                          @Nonnull final String password,
                          @Nonnull final String email,
                          @Nonnull final UserType userType )
  {
    final User user = new User();
    user.setLogin( login );
    user.setName( name );
    user.setPassword( password );
    user.setEmail( email );
    user.setUserType( userType );
    _userDAO.persist( user );

    return new UserDTO( user.getID(), user.getLogin(), user.getName(), user.getEmail(), user.getUserType() );
  }

  @Override
  public void deleteUser( @Nonnull final User user )
  {
    _userDAO.remove( user );
  }

  @Override
  @Nonnull
  public UserDTO updateUser( @Nonnull final User ref,
                             @Nonnull final String login,
                             @Nonnull final String name,
                             @Nonnull final String email,
                             @Nonnull final UserType userType )
  {
    final User user = _userDAO.findByID( ref.getID() );
    user.setLogin( login );
    user.setName( name );
    user.setEmail( email );
    user.setUserType( userType );
    _userDAO.persist( user );
    return new UserDTO( user.getID(), user.getLogin(), user.getName(), user.getEmail(), user.getUserType() );
  }

  @Override
  public void setPassword( @Nonnull final User user, @Nonnull final String password )
  {
    user.setPassword( password );
    _userDAO.persist( user );
  }
}
