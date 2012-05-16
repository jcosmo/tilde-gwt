package au.com.stocksoftware.tide.client.presenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import javax.inject.Inject;

/**
 * Callback invoked on completion of every job
 */
public class GlobalAsyncCallback
  implements AsyncCallback
{
  @Override
  public void onFailure( final Throwable caught )
  {
    //_messageView.showMessage( "Error", caught.toString() );
  }

  @Override
  public void onSuccess( final Object result )
  {
  }
}
