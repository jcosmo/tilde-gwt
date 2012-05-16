package au.com.stocksoftware.tide.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import javax.annotation.Nonnull;

public interface View<P extends Presenter>
  extends IsWidget
{
  void bind( @Nonnull P presenter );
}
