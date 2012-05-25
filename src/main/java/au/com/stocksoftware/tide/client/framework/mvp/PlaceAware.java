package au.com.stocksoftware.tide.client.framework.mvp;

import com.google.gwt.place.shared.Place;

/**
 * Service interface implemented by activities that need access to the place.
 */
public interface PlaceAware
{
  /**
   * Attempt to change the place of an activity.
   * The method should return false if the activity does not support changing to the specified place.
   *
   * @param place the place.
   * @param <P>   the type of the place.
   * @return true if the place was changed, false otherwise.
   */
  <P extends Place> boolean atPlace( final P place );
}
