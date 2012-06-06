package au.com.stocksoftware.tide.client.entity;

import javax.annotation.Nonnull;

public class ProjectVO
{
  private int _id;
  private String _name;

  public ProjectVO( final int id, @Nonnull final String name )
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
