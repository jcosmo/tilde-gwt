package au.com.stocksoftware.tide.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AdminPlace
  extends Place
{
  public static enum AdminTask
  {
    START,
    USERS,
    PROJECTS
  }
  
  private AdminTask _task;

  public AdminPlace( final AdminTask task )
  {
    _task = task;
  }

  public AdminTask getTask()
  {
    return _task;
  }

  public static class Tokenizer
    implements PlaceTokenizer<AdminPlace>
  {
    @Override
    public AdminPlace getPlace( final String token )
    {
      return new AdminPlace( AdminTask.valueOf( token ) );
    }

    @Override
    public String getToken( final AdminPlace place )
    {
      return place.getTask().toString();
    }
  }
}
