module ActiveRecord
  module ConnectionAdapters
    class JdbcConnection
      attr_reader :adapter, :connection_factory

      # @native_database_types - setup properly by adapter= versus set_native_database_types.
      #   This contains type information for the adapter.  Individual adapters can make tweaks
      #   by defined modify_types
      #
      # @native_types - This is the default type settings sans any modifications by the
      # individual adapter.  My guess is that if we loaded two adapters of different types
      # then this is used as a base to be tweaked by each adapter to create @native_database_types

      def initialize(config)
        @config = config.symbolize_keys!
        @config[:retry_count] ||= 5
        @config[:connection_alive_sql] ||= "select 1"
        @jndi_connection = false
        @connection = nil
        if @config[:jndi]
          begin
            configure_jndi
          rescue => e
            warn "JNDI data source unavailable: #{e.message}; trying straight JDBC"
            configure_jdbc
          end
        else
          configure_jdbc
        end
        connection # force the connection to load
        set_native_database_types
        @stmts = {}
      rescue Exception => e
        raise "The driver encountered an error: #{e}"
      end

      def adapter=(adapter)
        @adapter = adapter
        @native_database_types = dup_native_types
        @adapter.modify_types(@native_database_types)
      end

      # Duplicate all native types into new hash structure so it can be modified
      # without destroying original structure.
      def dup_native_types
        types = {}
        @native_types.each_pair do |k, v|
          types[k] = v.inject({}) do |memo, kv|
            memo[kv.first] = begin kv.last.dup rescue kv.last end
            memo
          end
        end
        types
      end
      private :dup_native_types

      def jndi_connection?
        @jndi_connection
      end

      def active?
        @connection
      end

      private
      def configure_jndi
        jndi = @config[:jndi].to_s
        ctx = javax.naming.InitialContext.new
        ds = ctx.lookup(jndi)
        @connection_factory = JdbcConnectionFactory.impl do
          ds.connection
        end
        unless @config[:driver]
          @config[:driver] = connection.meta_data.connection.java_class.name
        end
        @jndi_connection = true
      end

      def configure_jdbc
        driver = @config[:driver].to_s
        user   = @config[:username].to_s
        pass   = @config[:password].to_s
        url    = @config[:url].to_s

        unless driver && url
          raise ::ActiveRecord::ConnectionFailed, "jdbc adapter requires driver class and url"
        end

        jdbc_driver = JdbcDriver.new(driver)
        jdbc_driver.load
        @connection_factory = JdbcConnectionFactory.impl do
          jdbc_driver.connection(url, user, pass)
        end
      end
    end
  end
end
