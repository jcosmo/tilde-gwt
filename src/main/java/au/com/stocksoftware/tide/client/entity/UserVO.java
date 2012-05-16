package au.com.stocksoftware.tide.client.entity;

import java.util.List;
import javax.annotation.Nonnull;

public class UserVO
{
  private int _id;
  private String _name;

  public UserVO( final int id,
                 @Nonnull final String name )
  {
    _id = id;
    _name = name;
  }

  public int getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }
}
