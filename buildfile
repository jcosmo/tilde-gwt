layout = Layout::Default.new
layout[:target, :generated] = "generated"

desc "Tide"
define('tide', :layout => layout) do
  project.version = `git describe --tags --always`.strip
  project.group = 'au.com.stocksoftware.tide'
  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Tide, :ee, [:ee, :jpa_model_catalog], project._(:target, :generated, "domgen/ee"), project)
  Domgen::GenerateTask.new(:Tide, :gwt, [:gwt], project._(:target, :generated, "domgen/gwt"), project)

  compile.with :javax_ejb,
               :javax_persistence,
               :javax_servlet,
               :javax_validation,
               :javax_annotation,
               :jackson_core,
               :jackson_mapper,
               :gxt,
               :javax_annotation,
               :gwt_user,
               :google_guice,
               :aopalliance,
               :google_guice_assistedinject,
               :javax_inject,
               :gwt_gin,
               :replicant,
               :replicant_sources,
               :gwt_user



  test.with :mockito, :guiceyloops, :eclipselink, :jtds
  test.using :testng

  tide_module = gwt(["au.com.stocksoftware.tide.TideDev"],
                        :dependencies => [project.compile.dependencies,
                                          project.compile.target,
                                          :gxt,
                                          :replicant,
                                          :replicant_sources,
                                          :javax_validation,
                                          :javax_validation_sources],
                        :java_args => ["-Xms512M", "-Xmx1024M", "-XX:PermSize=128M", "-XX:MaxPermSize=256M"],
                        :draft_compile => (ENV["FAST_GWT"] == 'true'))

  package(:war).tap do |war|
    war.include "#{tide_module}/WEB-INF"
    war.include "#{tide_module}/tide"
    war.with :libs => [:replicant, :gwt_user]
    war.enhance([tide_module.name])
  end

  clean { rm_rf _(:target, :generated) }
  clean { rm_rf _("services") }

  iml.add_gwt_facet({"eweb.tide.CalendarDev" => true, "eweb.tide.Calendar" => false}, :settings => {:compilerMaxHeapSize => "1024"})

  iml.add_jpa_facet(:persistence_xml => _(:generated, "main/jpa/resources/META-INF/persistence.xml"),
                    :provider_enabled => "EclipseLink")
  iml.add_ejb_facet
  iml.add_web_facet

  ipr.add_exploded_war_artifact(project,
                                :dependencies => [project, :gwt_user],
                                :enable_gwt => true,
                                :enable_ejb => true,
                                :enable_jpa => true)
end

define_dbt_tasks(Buildr.project('tide'))

def define_service_project(classname)
  service_name = classname.gsub(/^.*\.([^.]*)BoundaryEJB$/, "\\1")
  source_project = Buildr.project('tide')
  layout = Layout::Default.new
  layout[:target, :generated] = "generated"
  desc "#{service_name} client artifacts"
  define service_name, :layout => layout, :base_dir => "services/#{service_name}" do
    project.version = source_project.version
    project.group = source_project.group
    project.no_ipr
    project.no_iml
    t = task("wsgen") do
      wsgen('eweb.tide', classname, project, source_project)
    end

    pkg = package(:jar)
    pkg.include(_(:generated, :ws, service_name, "META-INF")).exclude('*.xjb')
    pkg.include(_(:generated, :ws, service_name, "classes/eweb"))
    pkg.enhance [t.name]

    src_pkg = package(:sources)
    src_pkg.include(_(:generated, :ws, service_name, "META-INF")).exclude('*.xjb')
    src_pkg.include(_(:generated, :ws, service_name, "src/eweb"))
    src_pkg.enhance [t.name]

    wsdl = file("#{_(:generated, :ws, service_name, "META-INF")}/wsdl/#{service_name}.wsdl")
    wsdl.enhance [t.name]

    task 'upload' do
      wsdl_artifact = artifact("#{project.group}:#{project.id}:wsdl:#{project.version}").from("#{_(:generated, :ws, service_name, "META-INF")}/wsdl/#{service_name}.wsdl")
      wsdl_artifact.invoke
      wsdl_artifact.upload
    end
  end
end

define_service_project("eweb.tide.server.service.tide.TideServiceBoundaryEJB")

