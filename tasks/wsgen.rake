module Buildr
  module Wsgen

    # See the following links on how cxf can generate more artifacts
    #  http://cxf.apache.org/docs/wsdl-to-java.html
    #  http://cxf.apache.org/docs/javascript-clients.html
    #  http://cxf.apache.org/docs/index.html
    #  http://cxf.apache.org/docs/java-to-wsdl.html

    class << self

      # The specs for requirements
      def dependencies
        [
          'org.apache.cxf:cxf:jar:2.5.2',
          'org.apache.neethi:neethi:jar:3.0.1',
          'org.apache.velocity:velocity:jar:1.7',
          'commons-collections:commons-collections:jar:3.2.1',
          'commons-lang:commons-lang:jar:2.6',
          'org.apache.ws.xmlschema:xmlschema-core:jar:2.0.1',
          'wsdl4j:wsdl4j:jar:1.6.2'
        ]
      end

      def java2wsdl(output_dir, classname, options = {})
        dependencies = (options[:dependencies] || []) + self.dependencies
        cp = Buildr.artifacts(dependencies).each(&:invoke).map(&:to_s)

        service_name = options[:service_name] || classname

        address = options[:address] || "http://example.org/#{service_name}.wsdl"

        args = []

        args << "-wsdl"
        args << "-d"
        args << "#{output_dir}/META-INF/wsdl"
        #args << "-soap12"
        args << "-frontend"
        args << "jaxws"
        args << "-address"
        args << address
        args << "-o"
        args << "#{service_name}.wsdl"
        args << classname

        mkdir_p "#{output_dir}/META-INF/wsdl"
        mkdir_p "#{output_dir}/src"
        mkdir_p "#{output_dir}/classes"

        begin
          Java::Commands.java 'org.apache.cxf.tools.java2ws.JavaToWS', *(args + [{:classpath => cp, :properties => options[:properties], :java_args => options[:java_args]}])
        rescue => e
          if options[:fail_on_error].nil? || options[:fail_on_error]
            raise e
          else
            p e
          end
        end
      end

      def wsdl2java(output_dir, wsdl_filename, service_name, package)
        src_dir = "#{output_dir}/src"
        mkdir_p src_dir
        classes_dir = "#{output_dir}/classes"
        mkdir_p classes_dir
        sh "wsimport -target 2.0 -s #{src_dir} -d #{classes_dir} -target 2.0 -keep -p #{package} -wsdllocation /META-INF/wsdl/#{service_name}.wsdl #{wsdl_filename}"
      end
    end
  end
end

def wsgen(base_package, classname, target_project, source_project)

  service_name = classname.gsub(/^.*\.([^.]*)BoundaryEJB$/,"\\1")
  target_dir = target_project._(:generated, :ws, service_name)

  mkdir_p target_dir
  dependencies = (source_project.compile.dependencies + [source_project.compile.target])
  Buildr::Wsgen.java2wsdl(target_dir, classname, :dependencies => dependencies, :service_name => service_name)

  client_package = "#{base_package}.client.#{Domgen::Naming.underscore(service_name)}"

  Buildr::Wsgen.wsdl2java(target_dir, target_project._(:generated, :ws, service_name, "/META-INF/wsdl/#{service_name}.wsdl"), service_name, client_package)
end
