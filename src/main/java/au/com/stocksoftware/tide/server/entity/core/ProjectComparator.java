package au.com.stocksoftware.tide.server.entity.core;

import java.util.Comparator;

public class ProjectComparator
  implements Comparator<Project>
{
  public static final ProjectComparator COMPARATOR = new ProjectComparator();

  @Override
  public int compare( final Project project1, final Project project2 )
  {
    if ( null == project1 && null == project2 )
    {
      return 0;
    }
    else if ( null == project1 )
    {
      return 1;
    }
    else if ( null == project2 )
    {
      return -1;
    }

    return project1.getName().compareToIgnoreCase( project2.getName() );
  }
}
