module Buildr::IntellijIdea
  class IdeaProject
    private

    # Method required as it was elided from 1.4.7
    def partition_dependencies(dependencies)
      raise "Method partition_dependencies should be removed" if Buildr::VERSION > '1.4.7'
      libraries = []
      projects = []

      dependencies.each do |dependency|
        artifacts = Buildr.artifacts(dependency)
        artifacts_as_strings = artifacts.map(&:to_s)
        project = Buildr.projects.detect do |project|
          [project.packages, project.compile.target, project.resources.target, project.test.compile.target, project.test.resources.target].flatten.
            detect { |component| artifacts_as_strings.include?(component.to_s) }
        end
        if project
          projects << project
        else
          libraries += artifacts
        end
      end
      return libraries.uniq, projects.uniq
    end
  end
end
