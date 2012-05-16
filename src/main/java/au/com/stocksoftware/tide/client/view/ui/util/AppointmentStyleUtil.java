package au.com.stocksoftware.tide.client.view.ui.util;

import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AppointmentStyleUtil
{
  public static final Map<AppointmentStyle, String> STYLES = new LinkedHashMap<AppointmentStyle, String>();

  static
  {
    STYLES.put( AppointmentStyle.BLUE, "#2952A3" );
    STYLES.put( AppointmentStyle.RED, "#A32929" );
    STYLES.put( AppointmentStyle.GREEN, "#0D7813" );
    STYLES.put( AppointmentStyle.PURPLE, "#7A367A" );
    STYLES.put( AppointmentStyle.DARK_PURPLE, "#5229A3" );
    STYLES.put( AppointmentStyle.STEELE_BLUE, "#29527A" );
    STYLES.put( AppointmentStyle.LIGHT_BLUE, "#1B887A" );
    STYLES.put( AppointmentStyle.TEAL, "#28754E" );
    STYLES.put( AppointmentStyle.LIGHT_TEAL, "#4A716C" );
    STYLES.put( AppointmentStyle.LIGHT_GREEN, "#528800" );
    STYLES.put( AppointmentStyle.YELLOW_GREEN, "#88880E" );
    STYLES.put( AppointmentStyle.YELLOW, "#AB8B00" );
    STYLES.put( AppointmentStyle.ORANGE, "#BE6D00" );
    STYLES.put( AppointmentStyle.RED_ORANGE, "#B1440E" );
    STYLES.put( AppointmentStyle.LIGHT_BROWN, "#865A5A" );
    STYLES.put( AppointmentStyle.LIGHT_PURPLE, "#705770" );
    STYLES.put( AppointmentStyle.GREY, "#4E5D6C" );
    STYLES.put( AppointmentStyle.BLUE_GREY, "#5A6986" );
    STYLES.put( AppointmentStyle.YELLOW_GREY, "#6E6E41" );
    STYLES.put( AppointmentStyle.BROWN, "#8D6F47" );
    STYLES.put( AppointmentStyle.PINK, "#B1365F" );
  }

  private AppointmentStyleUtil()
  {
  }
}
