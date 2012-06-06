package au.com.stocksoftware.tide.server.service.tide;

import au.com.stocksoftware.tide.server.data_type.core.ProjectDTO;
import au.com.stocksoftware.tide.server.entity.core.Project;
import au.com.stocksoftware.tide.server.entity.core.ProjectComparator;
import au.com.stocksoftware.tide.server.entity.core.dao.ProjectDAO;
import au.com.stocksoftware.tide.server.service.core.ProjectService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless( name = ProjectService.EJB_NAME )
public class ProjectServiceEJB
  implements ProjectService
{
  @EJB
  private ProjectDAO _projectDAO;


  @Override
  @Nonnull
  public List<ProjectDTO> getProjects()
  {
    final List<Project> projects = _projectDAO.findAll();
    Collections.sort( projects, ProjectComparator.COMPARATOR );

    final List<ProjectDTO> result = new ArrayList<ProjectDTO>( projects.size() );
    for ( final Project project : projects )
    {
      result.add( new ProjectDTO( project.getID(), project.getName() ) );
    }
    return result;
  }

  @Override
  @Nonnull
  public ProjectDTO addProject( @Nonnull final String name )
  {
    final Project project = new Project();
    project.setName( name );
    _projectDAO.persist( project );

    return new ProjectDTO( project.getID(), project.getName() );
  }

  @Override
  public void deleteProject( @Nonnull final Project project )
  {
    _projectDAO.remove( project );
  }

  @Override
  public ProjectDTO updateProject( @Nonnull final Project ref,
                                   @Nonnull final String name )
  {
    final Project project = _projectDAO.findByID( ref.getID() );
    project.setName( name );
    _projectDAO.persist( project );
    return new ProjectDTO( project.getID(), project.getName() );
  }
}
