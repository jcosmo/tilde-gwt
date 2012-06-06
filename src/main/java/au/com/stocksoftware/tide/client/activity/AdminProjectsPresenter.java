package au.com.stocksoftware.tide.client.activity;

import au.com.stocksoftware.tide.client.data_type.core.ProjectDTO;
import au.com.stocksoftware.tide.client.entity.ProjectVO;
import au.com.stocksoftware.tide.client.service.core.ProjectService;
import au.com.stocksoftware.tide.client.service.util.Converter;
import au.com.stocksoftware.tide.client.view.AdminProjectsView;
import au.com.stocksoftware.tide.client.view.AdminProjectsView.Presenter;
import com.google.inject.Inject;
import java.util.List;
import org.realityforge.replicant.client.AsyncCallback;

public class AdminProjectsPresenter
  implements Presenter
{
  @Inject
  private AdminProjectsView _view;

  @Inject
  private ProjectService _projectService;

  private ProjectVO _currentProject;
  private boolean _newProject;

  public void refreshProjects()
  {
    _view.clearProjects();

    _projectService.getProjects(
      new AsyncCallback<List<ProjectDTO>>()
      {
        @Override
        public void onSuccess( final List<ProjectDTO> result )
        {
          _view.setProjects( Converter.projectDTOsToVOs( result ) );
        }
      } );
  }

  @Override
  public void actionAddProject()
  {
    _newProject = true;
    _currentProject = null;
    _view.showAddProject();
  }

  @Override
  public void actionProjectSelected( final ProjectVO project )
  {
    _newProject = false;
    _currentProject = project;
    _view.showProject( _currentProject );
  }

  @Override
  public void actionCancel()
  {
    _newProject = false;
    _currentProject = null;
    _view.clearCurrentProject();
  }

  @Override
  public void actionEdit()
  {
    if ( null != _currentProject )
    {
      _view.editProject( _currentProject );
    }
  }

  @Override
  public void actionSave()
  {
    final ProjectVO newValues = _view.getCurrentValues();
    if ( null != _currentProject )
    {
      _projectService.updateProject( _currentProject.getId(), newValues.getName(),
                                     new AsyncCallback<ProjectDTO>()
                                     {
                                       @Override
                                       public void onSuccess( final ProjectDTO updatedProject )
                                       {
                                         _currentProject = null;
                                         _view.clearCurrentProject();
                                         refreshProjects();
                                       }
                                     } );
    }
    else if ( _newProject )
    {
      _projectService.addProject( newValues.getName(),
                            new AsyncCallback<ProjectDTO>()
                            {
                              @Override
                              public void onSuccess( final ProjectDTO newProject )
                              {
                                _newProject = false;
                                _view.clearCurrentProject();
                                refreshProjects();
                              }
                            } );
    }
  }

  @Override
  public void actionDelete()
  {
    if ( null != _currentProject )
    {
      _projectService.deleteProject( _currentProject.getId(),
                               new AsyncCallback<Void>()
                               {
                                 @Override
                                 public void onSuccess( final Void ignored )
                                 {
                                   _currentProject = null;
                                   _view.clearCurrentProject();
                                   refreshProjects();
                                 }
                               } );
    }
  }
}
