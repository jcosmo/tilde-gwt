package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.view.AdminView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;

public class AdminUI
  extends AbstractUI<AdminView.Presenter>
  implements AdminView, IsWidget
{
  private Component _widget;

  @Override
  public void setUserList( final String users )
  {
  }

  @UiTemplate( "AdminView.ui.xml" )
  interface AdminViewUiBinder
    extends UiBinder<Component, AdminUI>
  {

  }

  private static AdminViewUiBinder _uiBinder = GWT.create( AdminViewUiBinder.class );

  public AdminUI()
  {
    _widget = _uiBinder.createAndBindUi( this );
  }

  @Override
  public Widget asWidget()
  {
    return _widget;
  }
}
