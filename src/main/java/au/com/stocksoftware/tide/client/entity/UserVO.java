package au.com.stocksoftware.tide.client.entity;

import au.com.stocksoftware.tide.client.data_type.core.UserType;
import java.util.List;
import javax.annotation.Nonnull;

public class UserVO
{
  private int _id;
  private String _login;
  private String _name;
  private String _email;
  private UserType _userType;

  public UserVO( final int id,
                 @Nonnull final String login,
                 @Nonnull final String name,
                 @Nonnull final String email,
                 @Nonnull final UserType userType )
  {
    _id = id;
    _login = login;
    _name = name;
    _email = email;
    _userType = userType;
  }

  public int getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public String getEmail()
  {
    return _email;
  }

  public String getLogin()
  {
    return _login;
  }

  public UserType getUserType()
  {
    return _userType;
  }
}
