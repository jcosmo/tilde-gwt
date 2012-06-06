package au.com.stocksoftware.tide.client.view.ui;

import au.com.stocksoftware.tide.client.activity.AdminProjectsPresenter;
import au.com.stocksoftware.tide.client.entity.ProjectVO;
import au.com.stocksoftware.tide.client.view.AdminProjectsView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import java.util.List;

public class AdminProjectsUI
  extends AbstractUI<AdminProjectsView.Presenter>
  implements AdminProjectsView, IsWidget
{
  private Component _widget;

  @UiField
  ListView _list;

  @UiField
  TextButton _addButton;

  @UiField
  TextButton _editButton;

  @UiField
  TextButton _deleteButton;

  @UiField
  TextButton _saveButton;

  @UiField
  TextButton _cancelButton;

  @UiField
  TextField _nameField;

  @UiField
  ContentPanel _contentPanel;

  Presenter _presenter;

  @UiTemplate( "AdminProjectsView.ui.xml" )
  interface AdminProjectsViewUiBinder
    extends UiBinder<Component, AdminProjectsUI>
  {
  }

  private static AdminProjectsViewUiBinder _uiBinder = GWT.create( AdminProjectsViewUiBinder.class );

  public AdminProjectsUI()
  {
    _widget = _uiBinder.createAndBindUi( this );
  }

  @Override
  public void setProjects( final List<ProjectVO> projects )
  {
    _list.getStore().replaceAll( projects );
    clearCurrentProject();
  }

  @Override
  public void clearProjects()
  {
    clearCurrentProject();
    _list.getStore().clear();
  }

  @Override
  public void setPresenter( final AdminProjectsPresenter presenter )
  {
    _presenter = presenter;
  }

  @Override
  public void showAddProject()
  {
    _list.getSelectionModel().deselectAll( );
    updateValues( null );
    enableFields( true );
    _nameField.focus();
    configureButtons( false, true, true, false );
  }

  @Override
  public void showProject( final ProjectVO project )
  {
    updateValues( project );
    enableFields( false );
    configureButtons( true, false, false, true );
  }

  @Override
  public void editProject( final ProjectVO project )
  {
    updateValues( project );
    enableFields( true );
    configureButtons( false, true, true, false );
  }

  @Override
  public void clearCurrentProject()
  {
    updateValues( null );
    enableFields( false );
    configureButtons( false, false, false, false );
    _list.getSelectionModel().deselectAll( );
  }

  @Override
  public ProjectVO getCurrentValues()
  {
    return new ProjectVO( -1, _nameField.getCurrentValue() );
  }

  @Override
  public Widget asWidget()
  {
    return _widget;
  }

  @UiFactory
  ListView createListView()
  {
    final ProjectProperties projectProperties = GWT.create( ProjectProperties.class );

    final ListView<ProjectVO, String> list = new ListView<ProjectVO, String>(
      new ListStore<ProjectVO>( projectProperties.key() ), projectProperties.name() );

    list.getSelectionModel().addSelectionHandler( new SelectionHandler<ProjectVO>() {
      @Override
      public void onSelection( final SelectionEvent<ProjectVO> projectVOSelectionEvent )
      {
        onProjectSelected( projectVOSelectionEvent );
      }
    } );

    return list;
  }

  interface ProjectProperties
    extends PropertyAccess<ProjectVO>
  {
    @Path( "id" )
    ModelKeyProvider<ProjectVO> key();

    @Path( "name" )
    ValueProvider<ProjectVO, String> name();
  }

  @UiHandler( { "_addButton" } )
  public void onAddProjectClicked( final SelectEvent event )
  {
    _presenter.actionAddProject();
  }

  @UiHandler( { "_cancelButton" } )
  public void onCancelClicked( final SelectEvent event )
  {
    _presenter.actionCancel();
  }

  @UiHandler( { "_deleteButton" } )
  public void onDeleteClicked( final SelectEvent event )
  {
    _presenter.actionDelete();
  }

  @UiHandler( { "_editButton" } )
  public void onEditClicked( final SelectEvent event )
  {
    _presenter.actionEdit();
  }

  @UiHandler( { "_saveButton" } )
  public void onSaveClicked( final SelectEvent event )
  {
    _presenter.actionSave();
  }

  public void onProjectSelected( final SelectionEvent<ProjectVO> event )
  {
    _presenter.actionProjectSelected( event.getSelectedItem() );
  }

  private void configureButtons( final boolean edit,
                                 final boolean cancel,
                                 final boolean save,
                                 final boolean delete )
  {
    _editButton.setVisible( edit );
    _cancelButton.setVisible( cancel );
    _saveButton.setVisible( save );
    _deleteButton.setVisible( delete );
    _contentPanel.getButtonBar().syncSize();
    _contentPanel.syncSize();
  }

  private void enableFields( final boolean enabled )
  {
    _nameField.setEnabled( enabled );
  }

  private void updateValues( final ProjectVO project )
  {
    _nameField.setValue( null == project ? "" : project.getName() );
  }
}
