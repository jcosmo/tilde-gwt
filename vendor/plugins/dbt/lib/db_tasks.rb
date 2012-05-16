# Note the following terminology is used throughout the plugin
# * database_key: a symbolic name of database. i.e. "central", "master", "core",
#   "ifis", "msdb" etc
# * env: a development environment. i.e. "test", "development", "production"
# * module_name: the name of the database directory in which sets of related database
#   files are stored. i.e. "Audit", "Auth", "Interpretation", ...
# * config_key: the name of entry in YAML file to look up configuration. Typically
#   constructed by database_key and env separated by an underscore. i.e.
#   "central_development", "master_test" etc.

# It should also be noted that the in some cases there is a database_key and
# module_key with the same name. This was due to legacy reasons and should be avoided
# in the future as it is confusing

require 'db_doc'

class DbTasks

  class Config

    class << self
      attr_writer :environment

      def environment
        @environment || 'development'
      end

      attr_writer :task_prefix

      def task_prefix
        @task_prefix || 'dbt'
      end

      # TODO: Move to specific DbConfig
      attr_writer :default_collation

      def default_collation
        @default_collation || 'SQL_Latin1_General_CP1_CS_AS'
      end

      attr_writer :driver

      def driver
        @driver || 'Mssql'
      end

      attr_writer :default_database

      def default_database
        @default_database || :default
      end

      attr_writer :default_import

      def default_import
        @default_import || :default
      end

      # config_file is where the yaml config file is located
      attr_writer :config_filename

      def config_filename
        raise "config_filename not specified" unless @config_filename
        @config_filename
      end

      # log_filename is where the log file is created
      attr_writer :log_filename

      def log_filename
        raise "log_filename not specified" unless @log_filename
        @log_filename
      end

      # search_dirs is an array of paths that are searched in order for artifacts for each module
      attr_writer :default_search_dirs

      def default_search_dirs
        raise "default_search_dirs not specified" unless @default_search_dirs
        @default_search_dirs
      end

      attr_writer :default_up_dirs

      def default_up_dirs
        @default_up_dirs || ['.', 'types', 'views', 'functions', 'stored-procedures', 'misc']
      end

      attr_writer :default_finalize_dirs

      def default_finalize_dirs
        @default_finalize_dirs || ['triggers', 'finalize']
      end

      attr_writer :default_pre_import_dirs

      def default_pre_import_dirs
        @default_pre_import_dirs || ['import-hooks/pre']
      end

      attr_writer :default_post_import_dirs

      def default_post_import_dirs
        @default_post_import_dirs || ['import-hooks/post']
      end

      attr_writer :default_down_dirs

      def default_down_dirs
        @default_down_dirs || ['down']
      end

      attr_writer :index_file_name

      def index_file_name
        @index_file_name || 'index.txt'
      end

    end
  end

  module FilterContainer
    def add_filter(&block)
      self.filters << block
    end

    def add_database_name_filter(pattern, database_key, optional = false)
      add_filter do |sql|
        DbTasks.filter_database_name(sql, pattern, DbTasks.config_key(database_key), optional)
      end
    end

    # Filter the SQL files replacing specified pattern with specified value
    def add_property_filter(pattern, value)
      add_filter do |sql|
        sql.gsub(pattern, value)
      end
    end

    # Makes the import scripts support statements such as
    #   ASSERT_ROW_COUNT(1)
    #   ASSERT_ROW_COUNT(SELECT COUNT(*) FROM Foo)
    #   ASSERT_UNCHANGED_ROW_COUNT()
    #   ASSERT(@Id IS NULL)
    #
    def add_import_assert_filters
      add_filter do |sql|
        sql = sql.gsub(/ASSERT_UNCHANGED_ROW_COUNT\(\)/, <<SQL)
IF (SELECT COUNT(*) FROM @@TARGET@@.@@TABLE@@) != (SELECT COUNT(*) FROM @@SOURCE@@.@@TABLE@@)
BEGIN
  RAISERROR ('Actual row count for @@TABLE@@ does not match expected rowcount', 16, 1) WITH SETERROR
END
SQL
        sql = sql.gsub(/ASSERT_ROW_COUNT\((.*)\)/, <<SQL)
IF (SELECT COUNT(*) FROM @@TARGET@@.@@TABLE@@) != (\\1)
BEGIN
  RAISERROR ('Actual row count for @@TABLE@@ does not match expected rowcount', 16, 1) WITH SETERROR
END
SQL
        sql = sql.gsub(/ASSERT\((.+)\)/, <<SQL)
IF NOT (\\1)
BEGIN
  RAISERROR ('Failed to assert \\1', 16, 1) WITH SETERROR
END
SQL
        sql
      end
    end

    def filters
      @filters ||= []
    end
  end

  class ImportDefinition
    include FilterContainer

    def initialize(database, key, options)
      @database = database
      @key = key
      @modules = options[:modules]
      @dir = options[:dir]
      @reindex = options[:reindex]
      @pre_import_dirs = options[:pre_import_dirs]
      @post_import_dirs = options[:post_import_dirs]
    end

    attr_accessor :database
    attr_accessor :key

    def modules
      @modules || database.modules
    end

    def dir
      @dir || "import"
    end

    # TODO: Move to specific DbConfig
    attr_writer :reindex

    def reindex?
      @reindex.nil? ? true : @reindex
    end

    # TODO: Move to specific DbConfig
    attr_writer :shrink

    def shrink?
      @shrink.nil? ? false : @shrink
    end

    attr_writer :pre_import_dirs

    def pre_import_dirs
      @pre_import_dirs || DbTasks::Config.default_pre_import_dirs
    end

    attr_writer :post_import_dirs

    def post_import_dirs
      @post_import_dirs || DbTasks::Config.default_post_import_dirs
    end

    def filters
      data.base.filters + @filters
    end

    def validate
      self.modules.each do |module_key|
        if !database.modules.include?(module_key)
          raise "Module #{module_key} in import #{self.key} does not exist in database module list #{self.database.modules.inspect}"
        end
      end
    end
  end

  class ModuleGroupDefinition

    def initialize(database, key, options)
      @database = database
      @key = key
      @modules = options[:modules]
      @import_enabled = options[:import_enabled]
    end

    attr_accessor :database
    attr_accessor :key

    attr_writer :modules

    def modules
      raise "Missing modules configuration for module_group #{key}" unless @modules
      @modules
    end

    def import_enabled?
      @import_enabled.nil? ? false : @import_enabled
    end

    def validate
      self.modules.each do |module_key|
        if !database.modules.include?(module_key)
          raise "Module #{module_key} in module group #{self.key} does not exist in database module list #{self.database.modules.inspect}"
        end
      end
    end
  end

  class DatabaseDefinition
    include FilterContainer

    def initialize(key, options)
      @key = key
      @collation = DbTasks::Config.default_collation
      @modules = options[:modules] if options[:modules]
      @backup = options[:backup] if options[:backup]
      @restore = options[:restore] if options[:restore]
      @datasets = options[:datasets] if options[:datasets]
      @collation = options[:collation] if options[:collation]
      @schema_overrides = options[:schema_overrides] if options[:schema_overrides]

      @imports = {}
      imports_config = options[:imports]
      if imports_config
        imports_config.keys.each do |import_key|
          import_config = imports_config[import_key]
          if import_config
            @imports[import_key] = ImportDefinition.new(self, import_key, import_config)
          end
        end
      end
      @module_groups = {}
      module_groups_config = options[:module_groups]
      if module_groups_config
        module_groups_config.keys.each do |module_group_key|
          module_group_config = module_groups_config[module_group_key]
          if module_group_config
            @module_groups[module_group_key] = ModuleGroupDefinition.new(self, module_group_key, module_group_config)
          end
        end
      end
    end

    def validate
      @imports.values.each {|d| d.validate}
      @module_groups.values.each {|d| d.validate}
    end

    # symbolic name of database
    attr_reader :key

    # List of modules to import
    attr_reader :imports

    # List of module_groups configs
    attr_reader :module_groups

    attr_writer :modules

    def task_prefix
      "#{DbTasks::Config.task_prefix}#{DbTasks.default_database?(self.key) ? '' : ":#{self.key}"}"
    end

    # List of modules to process for database
     def modules
       @modules = @modules.call if @modules.is_a?(Proc)
       @modules
     end

    # Database version. Stuffed as an extended property and used when creating filename.
    attr_accessor :version

    # The collation name for database. Nil means take the default_collation, if that is nil then take db default
    attr_accessor :collation

    attr_writer :search_dirs

    def search_dirs
      @search_dirs || DbTasks::Config.default_search_dirs
    end

    def dirs_for_database(subdir)
      search_dirs.map { |d| "#{d}/#{subdir}" }
    end

    attr_writer :up_dirs

    # Return the list of dirs to process when "upping" module
    def up_dirs
      @up_dirs || DbTasks::Config.default_up_dirs
    end

    attr_writer :down_dirs

    # Return the list of dirs to process when "downing" module
    def down_dirs
      @down_dirs || DbTasks::Config.default_down_dirs
    end

    attr_writer :finalize_dirs

    # Return the list of dirs to process when finalizing module.
    # i.e. Getting database ready for use. Often this is the place to add expensive triggers, constraints and indexes
    # after the import
    def finalize_dirs
      @finalize_dirs || DbTasks::Config.default_finalize_dirs
    end

    attr_writer :datasets

    # List of datasets that should be defined.
    def datasets
      @datasets || []
    end

    attr_writer :enable_separate_import_task

    def enable_separate_import_task?
      @enable_separate_import_task.nil? ? false : @enable_separate_import_task
    end

    attr_writer :enable_import_task_as_part_of_create

    def enable_import_task_as_part_of_create?
      @enable_import_task_as_part_of_create.nil? ? false : @enable_import_task_as_part_of_create
    end

    attr_writer :backup

    # Should the a backup task be defined for database?
    def backup?
      @backup || false
    end

    attr_writer :restore

    # Should the a restore task be defined for database?
    def restore?
      @restore || false
    end

    # Map of module => schema overrides
    # i.e. What database schema is created for a specific module
    def schema_overrides
      @schema_overrides || {}
    end

    def schema_name_for_module(module_name)
      schema_overrides[module_name] || module_name
    end

    def define_table_order_resolver(&block)
      @table_order_resolver = block
    end

    def table_ordering(module_name)
      raise "No table resolver so unable to determine table ordering for module #{module_name}" unless @table_order_resolver
      @table_order_resolver.call(module_name)
    end

    # Enable domgen support. Assume the database is associated with a single repository
    # definition, a single task to generate sql etc.
    def enable_domgen(repository_key, load_task_name, generate_task_name)
      define_table_order_resolver do |module_key|
        require 'domgen'
        data_module = Domgen.repository_by_name(repository_key).data_module_by_name(module_key.to_s)
        data_module.entities.select { |entity| !entity.abstract? }.collect do |entity|
          entity.sql.qualified_table_name
        end
      end

      self.modules = Proc.new do
        require 'domgen'
        Domgen.repository_by_name(repository_key).data_modules.collect{|data_module| data_module.name}
      end

      task "#{task_prefix}:load_config" => load_task_name
      task "#{task_prefix}:pre_build" => generate_task_name
    end

    # Enable db doc support. Assume that all the directories in up/down will have documentation and
    # will generate relative to specified directory.
    def enable_db_doc(target_directory)
      task "#{task_prefix}:db_doc"
      task "#{task_prefix}:pre_build" => ["#{task_prefix}:db_doc"]

      (up_dirs + down_dirs).each do |relative_dir_name|
        dirs_for_database(relative_dir_name).each do |dir|
          task "#{task_prefix}:db_doc" => DbTasks::DbDoc.define_doc_tasks(dir, "#{target_directory}/#{relative_dir_name}")
        end
      end
    end
  end

  @@defined_init_tasks = false
  @@database_driver_hooks = []
  @@databases = {}
  @@configurations = {}
  @@configuration_data = {}

  def self.init_database(database_key, &block)
    setup_connection(config_key(database_key), false, &block)
  end

  def self.init_control_database(database_key, &block)
    setup_connection(config_key(database_key), true, &block)
  end

  def self.add_database_driver_hook(&block)
    @@database_driver_hooks << block
  end

  def self.add_database(database_key, options = {})
    self.define_basic_tasks

    raise "Database with key #{database_key} already defined." if @@databases.has_key?(database_key)

    database = DatabaseDefinition.new(database_key, options)
    @@databases[database_key] = database

    yield database if block_given?

    define_tasks_for_database(database)
  end

  def self.filter_database_name(sql, pattern, config_key, optional = true)
    return sql if optional && self.configuration_data[config_key].nil?
    sql.gsub(pattern, configuration_for_key(config_key).catalog_name)
  end

  def self.dump_tables_to_fixtures(tables, fixture_dir)
    tables.each do |table_name|
      File.open(table_name_to_fixture_filename(fixture_dir, table_name), 'wb') do |file|
        puts("Dumping #{table_name}\n")
        const_name = :"DUMP_SQL_FOR_#{clean_table_name(table_name).gsub('.', '_')}"
        if Object.const_defined?(const_name)
          sql = Object.const_get(const_name)
        else
          sql = "SELECT * FROM #{table_name}"
        end

        records = YAML::Omap.new
        i = 0
        db.select_rows(sql).each do |record|
          records["r#{i += 1}"] = record
        end

        file.write records.to_yaml
      end
    end
  end

  def self.load_modules_fixtures(database_key, module_name)
    database = database_for_key(database_key)
    init_database(database.key) do
      load_fixtures(database, module_name)
    end

  end

  private

  IMPORT_RESUME_AT_ENV_KEY = "IMPORT_RESUME_AT"

  def self.partial_import_completed?
    !!ENV[IMPORT_RESUME_AT_ENV_KEY]
  end

  def self.database_for_key(database_key)
    database = @@databases[database_key]
    raise "Missing database for key #{database_key}" unless database
    database
  end

  def self.define_tasks_for_database(database)
    task "#{database.task_prefix}:load_config" => ["#{DbTasks::Config.task_prefix}:global:load_config"] do
      database.validate
    end

    # Database dropping

    desc "Drop the #{database.key} database."
    task "#{database.task_prefix}:drop" => ["#{database.task_prefix}:load_config"] do
      banner('Dropping database', database.key)
      drop(database)
    end

    # Database creation

    task "#{database.task_prefix}:pre_build" => ["#{DbTasks::Config.task_prefix}:all:pre_build"]

    desc "Create the #{database.key} database."
    task "#{database.task_prefix}:create" => ["#{database.task_prefix}:pre_build", "#{database.task_prefix}:load_config"] do
      banner('Creating database', database.key)
      create_database(database)
      init_database(database.key) do
        perform_create_action(database, :up)
        perform_create_action(database, :finalize)
      end
    end

    # Data set loading etc
    database.datasets.each do |dataset_name|
      desc "Loads #{dataset_name} data"
      task "#{database.task_prefix}:datasets:#{dataset_name}" => ["#{database.task_prefix}:load_config"] do
        banner("Loading Dataset #{dataset_name}", database.key)
        init_database(database.key) do
          database.modules.each do |module_name|
            load_dataset(database, module_name, dataset_name)
          end
        end
      end
    end

    # Import tasks
    if database.enable_separate_import_task?
      database.imports.values.each do |imp|
        define_import_task("#{database.task_prefix}", imp, "contents")
      end
    end

    database.module_groups.values.each do |module_group|
      define_module_group_tasks(module_group)
    end

    if database.enable_import_task_as_part_of_create?
      database.imports.values.each do |imp|
        key = ""
        key = ":" + imp.key.to_s unless default_import?(imp.key)
        desc "Create the #{database.key} database by import."
        task "#{database.task_prefix}:create_by_import#{key}" => ["#{database.task_prefix}:load_config", "#{database.task_prefix}:pre_build"] do
          banner("Creating Database By Import", database.key)
          create_database(database) unless partial_import_completed?
          init_database(database.key) do
            perform_create_action(database, :up) unless partial_import_completed?
            perform_import_action(imp, false, nil)
            perform_create_action(database, :finalize)
          end
        end
      end
    end

    if database.backup?
      desc "Perform backup of #{database.key} database"
      task "#{database.task_prefix}:backup" => ["#{database.task_prefix}:load_config"] do
        banner("Backing up Database", database.key)
        backup(database)
      end
    end

    if database.restore?
      desc "Perform restore of #{database.key} database"
      task "#{database.task_prefix}:restore" => ["#{database.task_prefix}:load_config"] do
        banner("Restoring Database", database.key)
        restore(database)
      end
    end
  end

  def self.define_module_group_tasks(module_group)
    database = module_group.database
    desc "Up the #{module_group.key} module group in the #{database.key} database."
    task "#{database.task_prefix}:#{module_group.key}:up" => ["#{database.task_prefix}:load_config", "#{database.task_prefix}:pre_build"] do
      banner("Upping module group '#{module_group.key}'", database.key)
      init_database(database.key) do
        database.modules.each do |module_name|
          next unless module_group.modules.include?(module_name)
          create_module(database, module_name, :up)
          create_module(database, module_name, :finalize)
        end
      end
    end

    desc "Down the #{module_group.key} schema group in the #{database.key} database."
    task "#{database.task_prefix}:#{module_group.key}:down" => ["#{database.task_prefix}:load_config", "#{database.task_prefix}:pre_build"] do
      banner("Downing module group '#{module_group.key}'", database.key)
      init_database(database.key) do
        database.modules.reverse.each do |module_name|
          next unless module_group.modules.include?(module_name)
          process_module(database, module_name, :down)
          tables = database.table_ordering(module_name)
          schema_name = database.schema_name_for_module(module_name)
          db.drop_schema(schema_name, tables)
        end
      end
    end

    database.imports.values.each do |imp|
      import_modules = imp.modules.select { |module_name| module_group.modules.include?(module_name) }
      if module_group.import_enabled? && !import_modules.empty?
        description = "contents of the #{module_group.key} module group"
        define_import_task("#{database.task_prefix}:#{module_group.key}", imp, description, module_group)
      end
    end
  end

  def self.backup(database)
    init_control_database(database.key) do
      db.backup(database, configuration_for_key(config_key(database.key)))
    end
  end

  def self.restore(database)
    init_control_database(database.key) do
      db.restore(database, configuration_for_key(config_key(database.key)))
    end
  end

  def self.create_database(database)
    configuration = configuration_for_key(config_key(database.key))
    return if configuration.no_create?
    init_control_database(database.key) do
      db.drop(database, configuration)
      db.create_database(database, configuration)
    end
  end

  def self.drop(database)
    init_control_database(database.key) do
      db.drop(database, configuration_for_key(config_key(database.key)))
    end
  end

  def self.import(imp, module_name, should_perform_delete)
    ordered_tables = imp.database.table_ordering(module_name)

    # check the import configuration is set
    configuration_for_key(config_key(imp.database.key, "import"))

    # Iterate over module in dependency order doing import as appropriate
    # Note: that tables with initial fixtures are skipped
    tables = ordered_tables.reject do |table|
      fixture_for_creation(imp.database, module_name, table)
    end
    if should_perform_delete && !partial_import_completed?
      tables.reverse.each do |table|
        info("Deleting #{clean_table_name(table)}")
        run_sql_batch("DELETE FROM #{table}")
      end
    end

    tables.each do |table|
      if ENV[IMPORT_RESUME_AT_ENV_KEY] == DbTasks.clean_table_name(table)
        run_sql_batch("DELETE FROM #{table}")
        ENV[IMPORT_RESUME_AT_ENV_KEY] = nil
      end
      if !partial_import_completed?
        db.pre_table_import(imp, module_name, table)
        perform_import(imp.database, module_name, table, imp.dir)
        db.post_table_import(imp, module_name, table)
      end
    end

    if ENV[IMPORT_RESUME_AT_ENV_KEY].nil?
      db.post_data_module_import(imp, module_name)
    end
  end

  def self.create_module(database, module_name, mode)
    schema_name = database.schema_name_for_module(module_name)
    db.create_schema(schema_name)
    process_module(database, module_name, mode)
  end

  def self.define_import_task(prefix, imp, description, module_group = nil)
    is_default_import = default_import?(imp.key)
    desc_prefix = is_default_import ? 'Import' : "#{imp.key.to_s.capitalize} import"

    task_name = is_default_import ? :import : :"import:#{imp.key}"
    desc "#{desc_prefix} #{description} of the #{imp.database.key} database."
    task "#{prefix}:#{task_name}" => ["#{imp.database.task_prefix}:load_config"] do
      banner("Importing Database#{is_default_import ? '' :" (#{imp.key})"}", imp.database.key)
      init_database(imp.database.key) do
        perform_import_action(imp, true, module_group)
      end
    end
  end

  def self.perform_create_action(database, mode)
    database.modules.each_with_index do |module_name, idx|
      create_module(database, module_name, mode)
    end
  end

  def self.collect_files(directories)

    index = []
    files = []

    directories.each do |dir|

      index_file = File.join(dir, DbTasks::Config.index_file_name)
      index_entries =
        File.exists?(index_file) ? File.new(index_file).readlines.collect { |filename| filename.strip } : []
      index_entries.each do |e|
        exists = false
        directories.each do |d|
          if File.exists?(File.join(d, e))
            exists = true
            break
          end
        end
        raise "A specified index entry does not exist on the disk #{e}" unless exists
      end

      index += index_entries

      if File.exists?(dir)
        files += Dir["#{dir}/*.sql"]
      end

    end

    file_map = {}

    files.each do |filename|
      basename =  File.basename(filename)
      file_map[basename] = (file_map[basename] || []) + [filename]
    end
    duplicates = file_map.reject { |basename, filenames| filenames.size == 1 }.values

    if !duplicates.empty?
      raise "Files with duplicate basename not allowed.\n\t#{duplicates.collect{|filenames| filenames.join("\n\t")}.join("\n\t")}"
    end

    files.sort! do |x, y|
      x_index = index.index(File.basename(x))
      y_index = index.index(File.basename(y))
      if x_index.nil? && y_index.nil?
         File.basename(x) <=> File.basename(y)
      elsif x_index.nil? && !y_index.nil?
        1
      elsif y_index.nil? && !x_index.nil?
        -1
      else
        x_index <=> y_index
      end
    end

    files
  end

  def self.perform_import_action(imp, should_perform_delete, module_group)
    if module_group.nil?
      imp.pre_import_dirs.each do |dir|
        files = collect_files(imp.database.dirs_for_database(dir))
        run_sql_files(imp.database, dir_display_name(dir), files, true)
      end unless partial_import_completed?
    end
    imp.modules.each do |module_key|
      if module_group.nil? || module_group.modules.include?(module_key)
        import(imp, module_key, should_perform_delete)
      end
    end
    if partial_import_completed?
      raise "Partial import unable to be completed as bad table name supplied #{ENV[IMPORT_RESUME_AT_ENV_KEY]}"
    end
    if module_group.nil?
      imp.post_import_dirs.each do |dir|
        files = collect_files(imp.database.dirs_for_database(dir))
        run_sql_files(imp.database, dir_display_name(dir), files, true)
      end
    end
    db.post_database_import(imp)
  end

  def self.dir_display_name(dir)
    (dir == '.' ? 'Base' : dir.humanize)
  end

  def self.define_basic_tasks
    if !@@defined_init_tasks
      task "#{DbTasks::Config.task_prefix}:global:load_config" do
        @@database_driver_hooks.each do |database_hook|
          database_hook.call
        end
        self.configuration_data = YAML::load(ERB.new(IO.read(DbTasks::Config.config_filename)).result)
      end

      task "#{DbTasks::Config.task_prefix}:all:pre_build"

      @@defined_init_tasks = true
    end
  end

  def self.config_key(database_key, env = DbTasks::Config.environment)
    default_database?(database_key) ? env : "#{database_key}_#{env}"
  end

  def self.default_database?(database_key)
    database_key.to_s == DbTasks::Config.default_database.to_s
  end

  def self.default_import?(import_key)
    import_key.to_s == DbTasks::Config.default_import.to_s
  end

  def self.run_import_sql(database, table, sql, script_file_name = nil, print_dot = false)
    sql = filter_sql(sql, database.filters)
    sql = sql.gsub(/@@TABLE@@/, table) if table
    sql = filter_database_name(sql, /@@SOURCE@@/, config_key(database.key, "import"))
    sql = filter_database_name(sql, /@@TARGET@@/, config_key(database.key))
    run_sql_batch(sql, script_file_name, print_dot, true)
  end

  def self.generate_standard_import_sql(table)
    sql = "INSERT INTO @@TARGET@@.#{table}("
    columns = db.column_names_for_table(table)
    sql += columns.join(', ')
    sql += ")\n  SELECT "
    sql += columns.join(', ')
    sql += " FROM @@SOURCE@@.#{table}\n"
    sql
  end

  def self.perform_standard_import(database, table)
    run_import_sql(database, table, generate_standard_import_sql(table))
  end

  def self.perform_import(database, module_name, table, import_dir)
    fixture_file = fixture_for_import(database, module_name, table, import_dir)
    sql_file = sql_for_import(database, module_name, table, import_dir)
    is_sql = !fixture_file && sql_file

    info("Importing #{DbTasks.clean_table_name(table)} (By #{fixture_file ? 'F' : is_sql ? 'S' : "D"})")
    if fixture_file
      load_fixture(table, fixture_file)
    elsif is_sql
      run_import_sql(database, table, IO.readlines(sql_file).join, sql_file, true)
    else
      perform_standard_import(database, table)
    end
  end

  def self.setup_connection(config_key, open_control_database, &block)
    db.open(configuration_for_key(config_key), open_control_database, DbTasks::Config.log_filename)
    if block_given?
      begin
        yield
      ensure
        db.close
      end
    end
  end

  def self.process_module(database, module_name, mode)
    dirs = mode == :up ? database.up_dirs : mode == :down ? database.down_dirs : database.finalize_dirs
    dirs.each do |dir|
      files = collect_files(dirs_for_module(database, module_name, dir))
      run_sql_files(database, "#{'%-10s' % "#{module_name}:" } #{dir_display_name(dir)}", files, false)
    end
    load_fixtures(database, module_name) if mode == :up
  end

  def self.load_fixtures(database, module_name)
    load_fixtures_from_dirs(database, module_name, dirs_for_module(database, module_name, 'fixtures'))
  end

  def self.load_dataset(database, module_name, dataset_name)
    load_fixtures_from_dirs(database, module_name, dirs_for_module(database, module_name, "datasets/#{dataset_name}"))
  end

  def self.load_fixtures_from_dirs(database, module_name, dirs)
    fixtures = {}
    database.table_ordering(module_name).each do |table_name|
      dirs.each do |dir|
        filename = table_name_to_fixture_filename(dir, table_name)
        fixtures[table_name] = filename if File.exists?(filename)
      end
    end

    database.table_ordering(module_name).reverse.each do |table_name|
      if fixtures[table_name]
        run_sql_batch("DELETE FROM #{table_name}")
      end
    end

    database.table_ordering(module_name).each do |table_name|
      filename = fixtures[table_name]
      next unless filename
      info("Loading fixture: #{clean_table_name(table_name)}")
      load_fixture(table_name, filename)
    end
  end

  def self.table_name_to_fixture_filename(dir, table_name)
    "#{dir}/#{clean_table_name(table_name)}.yml"
  end

  def self.clean_table_name(table_name)
    table_name.tr('[]"''', '')
  end

  def self.load_fixture(table_name, filename)
    yaml = YAML::load(IO.read(filename))
    # Skip empty files
    return unless yaml
    # NFI
    yaml_value =
      if yaml.respond_to?(:type_id) && yaml.respond_to?(:value)
        yaml.value
      else
        [yaml]
      end

    yaml_value.each do |fixture|
      raise "Bad data for #{table_name} fixture named #{fixture}" unless fixture.respond_to?(:each)
      fixture.each do |name, data|
        raise "Bad data for #{table_name} fixture named #{name} (nil)" unless data

        db.insert_row(table_name, data)
      end
    end
  end

  def self.run_sql_batch(sql, script_file_name = nil, print_dot = false, execute_in_control_database = false)
    sql.gsub(/\r/, '').split(/(\s|^)GO(\s|$)/).reject { |q| q.strip.empty? }.each_with_index do |ddl, index|
      $stdout.putc '.' if print_dot
      begin
        db.execute(ddl, execute_in_control_database)
      rescue
        if script_file_name.nil? || index.nil?
          raise $!
        else
          raise "An error occurred while trying to execute batch ##{index + 1} of #{File.basename(script_file_name)}:\n#{$!}"
        end
      end
    end
    $stdout.putc "\n" if print_dot
  end

  def self.configuration_for_key(config_key)
    existing = @@configurations[config_key.to_s]
    return existing if existing
    c = self.configuration_data[config_key.to_s]
    raise "Missing config for #{config_key}" unless c
    configuration = DbTasks.const_get("#{DbTasks::Config.driver}DbConfig").new(c)
    @@configurations[config_key.to_s] = configuration
  end

  def self.configuration_data
    @@configuration_data
  end

  def self.configuration_data=(configuration_data)
    @@configuration_data = configuration_data
    @@configurations = {}
  end

  def self.run_filtered_sql_batch(database, sql, script_file_name = nil)
    sql = filter_sql(sql, database.filters)
    run_sql_batch(sql, script_file_name)
  end

  def self.filter_sql(sql, filters)
    filters.each do |filter|
      sql = filter.call(sql)
    end
    sql
  end

  def self.run_sql_files(database, label, files, is_import)
    files.each do |filename|
      run_sql_file(database, label, filename, is_import)
    end
  end

  def self.run_sql_file(database, label, filename, is_import)
    info("#{label}: #{File.basename(filename)}")
    sql = IO.readlines(filename).join
    if is_import
      run_import_sql(database, nil, sql, filename)
    else
      run_filtered_sql_batch(database, sql, filename)
    end
  end

  def self.dirs_for_module(database, module_name, subdir = nil)
    database.search_dirs.map { |d| "#{d}/#{module_name}#{ subdir ? "/#{subdir}" : ''}" }
  end

  def self.first_file_from(files)
    files.each do |file|
      if File.exist?(file)
        return file
      end
    end
    nil
  end

  def self.fixture_for_creation(database, module_name, table)
    first_file_from(dirs_for_module(database, module_name, table_name_to_fixture_filename("fixtures", table)))
  end

  def self.fixture_for_import(database, module_name, table, import_dir)
    first_file_from(dirs_for_module(database, module_name, table_name_to_fixture_filename(import_dir, table)))
  end

  def self.sql_for_import(database, module_name, table, import_dir)
    first_file_from(dirs_for_module(database, module_name, "#{import_dir}/#{clean_table_name(table)}.sql"))
  end

  def self.banner(message, database_key)
    info("**** #{message}: (Database: #{database_key}, Environment: #{DbTasks::Config.environment}) ****")
  end

  def self.info(message)
    puts message
  end

  def self.db
    @db ||= DbTasks.const_get("#{DbTasks::Config.driver}DbDriver").new
  end

  class DbConfig
    def initialize(configuration)
      raise NotImplementedError
    end

    def catalog_name
      raise NotImplementedError
    end

    def no_create?
      raise NotImplementedError
    end
  end

  class DbDriver
    def insert_row(table_name, row)
      raise NotImplementedError
    end

    def execute(sql, execute_in_control_database = false)
      raise NotImplementedError
    end

    def select_rows(sql)
      raise NotImplementedError
    end

    def create_schema(schema_name)
      raise NotImplementedError
    end

    def drop_schema(schema_name, tables)
      raise NotImplementedError
    end

    def column_names_for_table(table)
      raise NotImplementedError
    end

    def open(config, open_control_database, log_filename)
      raise NotImplementedError
    end

    def close
      raise NotImplementedError
    end

    def create_database(database, configuration)
      raise NotImplementedError
    end

    def drop(database, configuration)
      raise NotImplementedError
    end

    def backup(database, configuration)
      raise NotImplementedError
    end

    def restore(database, configuration)
      raise NotImplementedError
    end

    def pre_table_import(imp, module_name, table)
      raise NotImplementedError
    end

    def post_table_import(imp, module_name, table)
      raise NotImplementedError
    end

    def post_data_module_import(imp, module_name)
      raise NotImplementedError
    end
  end
end

require 'ar_db_driver.rb'
require 'mssql_db_driver.rb'
require 'postgres_db_driver.rb'