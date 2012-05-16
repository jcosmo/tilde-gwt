package au.com.stocksoftware.tide.server.test.util;

import java.util.Properties;

public class DatabaseUtil
{
  static final String URL_KEY = "javax.persistence.jdbc.url";
  static final String DRIVER_KEY = "javax.persistence.jdbc.driver";
  static final String USER_KEY = "javax.persistence.jdbc.user";
  static final String PASSWORD_KEY = "javax.persistence.jdbc.password";
  static final String TEST_DB_URL = "tide.test.db.url";
  static final String MSSQL_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
  static final String DB_URL_ENV_PROPERTY = "TIDE_TEST_DB_URL";

  private DatabaseUtil()
  {
  }

  static Properties initDatabaseProperties()
  {
    final String value = System.getenv().get( DB_URL_ENV_PROPERTY );
    final String url = System.getProperty( TEST_DB_URL, ( null == value ? "DATABASE_URL_UNSET" : value ) );

    final Properties properties = new Properties();
    properties.put( DRIVER_KEY, MSSQL_DRIVER );
    properties.put( URL_KEY, url );
    properties.put( "javax.persistence.transactionType", "RESOURCE_LOCAL" );
    properties.put( "javax.persistence.jtaDataSource", "" );
    return properties;
  }
}
