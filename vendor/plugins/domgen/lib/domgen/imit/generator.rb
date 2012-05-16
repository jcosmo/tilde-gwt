module Domgen
  module Generator
    module Imit
      TEMPLATE_DIRECTORY = "#{File.dirname(__FILE__)}/templates"
      HELPERS = [Domgen::Java::Helper]
      FACETS = [:imit]
    end
  end
end
Domgen.template_set(:imit) do |template_set|
  template_set.template(Domgen::Generator::Imit::FACETS,
                        :entity,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/entity.java.erb",
                        'main/java/#{entity.imit.qualified_name.gsub(".","/")}.java',
                        Domgen::Generator::Imit::HELPERS)
  template_set.template(Domgen::Generator::Imit::FACETS,
                        :data_module,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/mapper.java.erb",
                        'main/java/#{data_module.imit.qualified_mapper_name.gsub(".","/")}.java',
                        Domgen::Generator::Imit::HELPERS)
end

Domgen.template_set(:imit_json) do |template_set|
  template_set.template(Domgen::Generator::Imit::FACETS,
                        :repository,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/change_mapper.java.erb",
                        'main/java/#{repository.imit.qualified_change_mapper_name.gsub(".","/")}.java',
                        Domgen::Generator::Imit::HELPERS)
end

Domgen.template_set(:imit_gwt_proxy) do |template_set|
  template_set.template(Domgen::Generator::Imit::FACETS,
                        :service,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/service.java.erb",
                        'main/java/#{service.imit.qualified_name.gsub(".","/")}.java',
                        Domgen::Generator::Imit::HELPERS)
  template_set.template(Domgen::Generator::Imit::FACETS + [:gwt],
                        :service,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/proxy.java.erb",
                        'main/java/#{service.imit.qualified_proxy_name.gsub(".","/")}.java',
                        Domgen::Generator::Imit::HELPERS)
  template_set.template(Domgen::Generator::Imit::FACETS + [:gwt],
                        :repository,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/services_module.java.erb",
                        'main/java/#{repository.imit.qualified_services_module_name.gsub(".","/")}.java',
                        [Domgen::Java::Helper])
  template_set.template(Domgen::Generator::Imit::FACETS,
                        :exception,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/exception.java.erb",
                        'main/java/#{exception.imit.qualified_name.gsub(".","/")}.java',
                        Domgen::Generator::Imit::HELPERS)
end
Domgen.template_set(:imit_gwt_proxy_service_test) do |template_set|
  template_set.template(Domgen::Generator::Imit::FACETS + [:gwt],
                        :repository,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/mock_services_module.java.erb",
                        'test/java/#{repository.imit.qualified_mock_services_module_name.gsub(".","/")}.java',
                        [Domgen::Java::Helper])
end

Domgen.template_set(:imit_jpa) do |template_set|
  facets = Domgen::Generator::Imit::FACETS + [:jpa]
  helpers = Domgen::Generator::Imit::HELPERS + [Domgen::JPA::Helper, Domgen::Java::Helper]
  template_set.template(facets,
                        :data_module,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/jpa_encoder.java.erb",
                        'main/java/#{data_module.imit.qualified_jpa_encoder_name.gsub(".","/")}.java',
                        helpers)
  template_set.template(facets,
                        :data_module,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/router_interface.java.erb",
                        'main/java/#{data_module.imit.qualified_router_interface_name.gsub(".","/")}.java',
                        helpers)
  template_set.template(facets,
                        :repository,
                        "#{Domgen::Generator::Imit::TEMPLATE_DIRECTORY}/message_generator.java.erb",
                        'main/java/#{repository.imit.qualified_message_generator_name.gsub(".","/")}.java',
                        helpers)
end
