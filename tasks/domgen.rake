def workspace_dir
  File.expand_path(File.dirname(__FILE__) + '/..')
end

$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/vendor/plugins/domgen/lib"))

require 'domgen'

Domgen::Sql.dialect = Domgen::Sql::MssqlDialect
Domgen::LoadSchema.new("#{workspace_dir}/architecture.rb")

Domgen::GenerateTask.new(:Tide, :sql, [:mssql], "#{workspace_dir}/databases/generated")
Domgen::Xmi::GenerateXMITask.new(:Tide, :xmi, "#{workspace_dir}/target/xmi/Tide.xmi")
