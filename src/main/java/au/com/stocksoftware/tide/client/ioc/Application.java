package au.com.stocksoftware.tide.client.ioc;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimplePanel;
import javax.inject.Inject;

public class Application
{
  private final ApplicationActivityMapper _applicationActivityMapper;
  private final EventBus _eventBus;
  private final SimplePanel _simplePanel;

  @Inject
  public Application( final ApplicationActivityMapper applicationActivityMapper,
                      final EventBus eventBus,
                      final SimplePanel simplePanel )
  {
    _applicationActivityMapper = applicationActivityMapper;
    _eventBus = eventBus;
    _simplePanel = simplePanel;
  }

  public void activate()
  {
    new ActivityManager( _applicationActivityMapper, _eventBus ).setDisplay( _simplePanel );
  }
}
