package au.com.stocksoftware.tide.client.ioc;

import au.com.stocksoftware.tide.client.activity.AdminActivity;
import au.com.stocksoftware.tide.client.framework.mvp.AbstractActivityMapper;
import au.com.stocksoftware.tide.client.framework.mvp.ActivityAsyncProxy;
import au.com.stocksoftware.tide.client.place.AdminPlace;
import javax.inject.Inject;
import javax.inject.Provider;

public class ApplicationActivityMapper
  extends AbstractActivityMapper
{
  @Inject
  public ApplicationActivityMapper( final Provider<ActivityAsyncProxy<AdminActivity>> adminActivity )
  {
    registerProvider( AdminPlace.class, adminActivity );
  }
}
