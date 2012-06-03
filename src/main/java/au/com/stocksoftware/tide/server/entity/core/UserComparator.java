package au.com.stocksoftware.tide.server.entity.core;

import java.util.Comparator;

public class UserComparator
  implements Comparator<User>
{
  public static final UserComparator COMPARATOR = new UserComparator();

  @Override
  public int compare( final User user1, final User user2 )
  {
    if ( null == user1 && null == user2 )
    {
      return 0;
    }
    else if ( null == user1 )
    {
      return 1;
    }
    else if ( null == user2 )
    {
      return -1;
    }

    return user1.getName().compareToIgnoreCase( user2.getName() );
  }
}
