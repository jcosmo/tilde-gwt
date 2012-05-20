package au.com.stocksoftware.tide.client;

import au.com.stocksoftware.tide.client.view.ui.BorderLayoutExample;
import au.com.stocksoftware.tide.client.view.ui.BorderLayoutUiBinderDynamicAttributeExample;
import au.com.stocksoftware.tide.client.view.ui.BorderLayoutUiBinderExample;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.Viewport;

public final class Tide
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    /*
    final BorderLayoutExample example = new BorderLayoutExample();
    Widget widget = example.asWidget();
    Viewport viewport = new Viewport();
    viewport.add( widget );
    RootPanel.get().add( viewport );
    */

    /*
    final BorderLayoutUiBinderExample example = new BorderLayoutUiBinderExample();
    Widget widget = example.asWidget();
    Viewport viewport = new Viewport();
    viewport.add( widget );
    RootPanel.get().add( viewport );
    */

    final BorderLayoutUiBinderDynamicAttributeExample example = new BorderLayoutUiBinderDynamicAttributeExample();
    Widget widget = example.asWidget();
    Viewport viewport = new Viewport();
    viewport.add( widget );
    RootPanel.get().add( viewport );
  }
}