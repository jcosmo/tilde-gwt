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

    data_module.entity(:User) do |t|
      t.integer(:ID, :primary_key => true)
      t.string(:Name, 255)
      t.string(:Password, 255)
      t.unique_constraint([:Name])
    end

    data_module.struct(:UserDTO) do |s|
      s.integer(:ID)
      s.text(:Name)
    end

    data_module.service(:UserService) do |s|
      s.method(:GetUsers) do |m|
        m.returns(:struct, :referenced_struct => :UserDTO, :collection_type => :sequence)
      end

      s.method(:AddUser) do |m|
        m.string(:Name, 255)
        m.string(:Password, 255)
        m.returns(:struct, :referenced_struct => :UserDTO)
      end

      s.method(:SetPassword) do |m|
        m.reference(:User)
        m.string(:Password, 255)
      end
    end
  end
end
