Domgen.repository(:Tide) do |repository|
  repository.enable_facet(:jpa)
  repository.enable_facet(:ejb)
  repository.enable_facet(:jaxb)
  repository.enable_facet(:jackson)
  #repository.enable_facet(:jws)
  repository.enable_facet(:imit)

  repository.ee.base_package = 'au.com.stocksoftware.tide'
  repository.ejb.base_package = 'au.com.stocksoftware.tide'
  repository.jpa.base_package = 'au.com.stocksoftware.tide'
  repository.gwt.package = 'au.com.stocksoftware.tide'

  repository.data_module(:Core) do |data_module|
    data_module.entity(:Client) do |t|
      t.integer(:ID, :primary_key => true)
      t.string(:Name, 255)
    end

    data_module.enumeration(:UserType, :integer, :values => {"STAFF" => 0 ,"ADMIN" => 1})

    data_module.entity(:User) do |t|
      t.integer(:ID, :primary_key => true)
      t.string(:Login, 255)
      t.string(:Name, 255)
      t.string(:Email, 255)
      t.string(:Password, 255)
      t.enumeration(:UserType, :UserType)
      t.unique_constraint([:Login])
      t.unique_constraint([:Name])
    end

    data_module.struct(:UserDTO) do |s|
      s.integer(:ID)
      s.text(:Login)
      s.text(:Name)
      s.text(:Email)
      s.enumeration(:UserType, :UserType)
    end

    data_module.entity(:Project) do |t|
      t.integer(:ID, :primary_key => true)
      t.string(:Name, 255)
      t.unique_constraint([:Name])
    end

    data_module.struct(:ProjectDTO) do |s|
      s.integer(:ID)
      s.text(:Name)
    end

    data_module.service(:UserService) do |s|
      s.method(:GetUsers) do |m|
        m.returns(:struct, :referenced_struct => :UserDTO, :collection_type => :sequence)
      end

      s.method(:AddUser) do |m|
        m.string(:Login, 255)
        m.string(:Name, 255)
        m.string(:Password, 255)
        m.string(:Email, 255)
        m.enumeration(:UserType, :UserType)
        m.returns(:struct, :referenced_struct => :UserDTO)
      end

      s.method(:DeleteUser) do |m|
        m.reference(:User)
      end

      s.method(:UpdateUser) do |m|
        m.reference(:User)
        m.string(:Login, 255)
        m.string(:Name, 255)
        m.string(:Email, 255)
        m.enumeration(:UserType, :UserType)
        m.returns(:struct, :referenced_struct => :UserDTO)
      end

      s.method(:SetPassword) do |m|
        m.reference(:User)
        m.string(:Password, 255)
      end
    end

    data_module.service(:ProjectService) do |s|
      s.method(:GetProjects) do |m|
        m.returns(:struct, :referenced_struct => :ProjectDTO, :collection_type => :sequence)
      end

      s.method(:AddProject) do |m|
        m.string(:Name, 255)
        m.returns(:struct, :referenced_struct => :ProjectDTO)
      end

      s.method(:DeleteProject) do |m|
        m.reference(:Project)
      end

      s.method(:UpdateProject) do |m|
        m.reference(:Project)
        m.string(:Name, 255)
        m.returns(:struct, :referenced_struct => :ProjectDTO)
      end
    end
  end
end
