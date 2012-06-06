package au.com.stocksoftware.tide.client.view;

import au.com.stocksoftware.tide.client.activity.AdminProjectsPresenter;
import au.com.stocksoftware.tide.client.entity.ProjectVO;
import java.util.List;

public interface AdminProjectsView
  extends View<AdminProjectsView.Presenter>
{
  void setProjects( List<ProjectVO> projects );

  void clearProjects();

  void setPresenter( AdminProjectsPresenter presenter );

  void showAddProject();

  void showProject( ProjectVO project );

  void clearCurrentProject();
  
  ProjectVO getCurrentValues();

  void editProject( ProjectVO project );

  interface Presenter
    extends au.com.stocksoftware.tide.client.view.Presenter
  {
    void actionAddProject();
    
    void actionProjectSelected( ProjectVO project );

    void actionCancel();

    void actionEdit();

    void actionSave();

    void actionDelete();
  }
}
