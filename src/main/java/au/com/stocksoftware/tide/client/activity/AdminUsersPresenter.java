package au.com.stocksoftware.tide.client.activity;

import au.com.stocksoftware.tide.client.view.AdminUsersView;
import au.com.stocksoftware.tide.client.view.AdminUsersView.Presenter;
import au.com.stocksoftware.tide.client.view.AdminView;
import com.google.inject.Inject;

public class AdminUsersPresenter
  implements Presenter
{
  @Inject
  private AdminUsersView _view;
}
