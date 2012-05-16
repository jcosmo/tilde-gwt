def workspace_dir
  File.expand_path(File.dirname(__FILE__) + '/..')
end

# TODO: Remove this cruft once dbt is AR free
$LOAD_PATH.insert(0, "#{workspace_dir}/vendor/rails-2.2.2/activesupport/lib")
$LOAD_PATH.insert(0, "#{workspace_dir}/vendor/rails-2.2.2/activerecord/lib")
$LOAD_PATH.insert(0, "#{workspace_dir}/vendor/gems/activerecord-jdbc-adapter-1.0.0/lib")
$LOAD_PATH.insert(0, "#{workspace_dir}/vendor/plugins/dbt/lib")

require 'db_tasks'

DbTasks::Config.environment = ENV['DB_ENV'] if ENV['DB_ENV']
DbTasks::Config.log_filename = "#{workspace_dir}/target/dbt/logs/db.log"
DbTasks.add_database_driver_hook { db_driver_setup }
DbTasks::Config.driver = 'Mssql'
DbTasks::Config.config_filename = File.expand_path("#{workspace_dir}/config/database.yml")

def define_dbt_tasks(project)
  DbTasks.add_database(:default,
                       :imports => {:default => {}}) do |database|
    database.version = project.version
    generated_dir = "#{workspace_dir}/databases/generated"
    database.search_dirs = [generated_dir, "#{workspace_dir}/databases"]
    database.enable_domgen(:Tide, 'domgen:load', 'domgen:sql')
    database.add_import_assert_filters
  end
end

def db_driver_setup
  load_jtds
  db_date_setup
end

def load_jtds
  Buildr.artifact(:jtds).invoke
  require Buildr.artifact(:jtds).to_s
end

def db_date_setup
  require 'activerecord'

  ::ActiveSupport::CoreExtensions::Time::Conversions::DATE_FORMATS[:default] = '%d %b %Y'
  ::ActiveSupport::CoreExtensions::Date::Conversions::DATE_FORMATS[:default] = '%d %b %Y'

  # Futz with date formats to ensure that we don't get month/day mixup
  ::ActiveSupport::CoreExtensions::Date::Conversions::DATE_FORMATS.merge!(:db => '%d %b %Y')
  ::ActiveSupport::CoreExtensions::Time::Conversions::DATE_FORMATS.merge!(:db => '%d %b %Y %H:%M:%S')
end
