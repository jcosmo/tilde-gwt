package au.com.stocksoftware.tide.client.view;

public interface AdminView
  extends View<AdminView.Presenter>
{
  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
    void onUserClicked( int userID );
  }

  void setUserList( String users );
}
