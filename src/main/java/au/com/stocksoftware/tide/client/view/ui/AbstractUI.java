package au.com.stocksoftware.tide.client.view.ui;

import com.google.gwt.user.client.ui.Composite;
import au.com.stocksoftware.tide.client.view.Presenter;
import au.com.stocksoftware.tide.client.view.View;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractUI<P extends Presenter>
  extends Composite
  implements View<P>
{
  @Nullable
  private P _presenter;

  public void bind( @Nonnull final P presenter )
  {
    if ( null != _presenter )
    {
      unbind();
    }
    _presenter = presenter;
  }

  public void unbind()
  {
    _presenter = null;
  }

  @SuppressWarnings( { "NullableProblems" } )
  @Nonnull
  protected final P getPresenter()
  {
    if ( null == _presenter )
    {
      throw new IllegalStateException( "Attempting to call getPresenter() when null" );
    }
    else
    {
      return _presenter;
    }
  }
}
