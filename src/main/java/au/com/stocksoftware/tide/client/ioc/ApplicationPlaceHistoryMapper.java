package au.com.stocksoftware.tide.client.ioc;

import au.com.stocksoftware.tide.client.place.AdminPlace;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers( { AdminPlace.Tokenizer.class } )
public interface ApplicationPlaceHistoryMapper
  extends PlaceHistoryMapper
{
}
