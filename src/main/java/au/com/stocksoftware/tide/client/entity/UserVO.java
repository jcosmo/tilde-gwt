package au.com.stocksoftware.tide.client.entity;

import java.util.List;
import javax.annotation.Nonnull;

public class UserVO
{
  private int _id;
  private String _login;
  private String _name;
  private String _email;

  public UserVO( final int id,
                 @Nonnull final String login,
                 @Nonnull final String name,
                 @Nonnull final String email )
  {
    _id = id;
    _login = login;
    _name = name;
    _email = email;
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
}
