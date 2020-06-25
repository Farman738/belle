package com.e.belle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.e.belle.AllApiLinks.AllApiLinks;
import com.e.belle.CustomExpandableListAdapter.CustomExpandableListAdapter;
import com.e.belle.CustomExpandableListAdapter.ExpandableListDataPump;
import com.e.belle.Http_Handler.GlobalPostingMethod;
import com.e.belle.Http_Handler.HttpHandlerModel;
import com.e.belle.LoginSingUpActivity.LoginActivity;
import com.e.belle.Model.AsyModel;
import com.e.belle.Model.LoginModel;
import com.e.belle.SessionManagment.SessionManagement;
import com.e.belle.ui.OrderApproval.OderListFragment;
import com.e.belle.ui.OrderSaleActivity.ActivityFragment;
import com.e.belle.ui.upgradeApp.UpgradeAppFragment;
import com.e.belle.ui.vieworder.ViewOrderFragment;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

  private AppBarConfiguration mAppBarConfiguration;
  private SessionManagement sessionManagement;
  private TextView tv_header_userName,tv_header_userEmail;
  private ProgressDialog progress;
  private DrawerLayout drawer;
  private NavigationView navigationView;
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    drawer = findViewById(R.id.drawer_layout);
    navigationView = findViewById(R.id.nav_view);

//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_activity,
//                R.id.nav_order_approval
////                R.id.nav_slideshow,
////                R.id.nav_tools, R.id.nav_share, R.id.nav_send
//        )
//                .setDrawerLayout(drawer)
//                .build();

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);

    bindInItData();
  }

  private void bindInItData() {

    Expandiblelistbind();

    sessionManagement = new SessionManagement(getApplicationContext());
    View headerLayout = navigationView.getHeaderView(0);
    tv_header_userName = headerLayout.findViewById(R.id.tv_header_userName);
    tv_header_userEmail = headerLayout.findViewById(R.id.tv_header_userEmail);
    tv_header_userName.setText(sessionManagement.getUserName());
    tv_header_userEmail.setText(sessionManagement.getUserEmail());

  }

  ExpandableListView expandableListView;
  ExpandableListAdapter expandableListAdapter;
  List<String> expandableListTitle;
  Map<String, List<String>> expandableListDetail;
  public int selectedchildPosition = 0;
  public String groupName = "";
  public void Expandiblelistbind() {
    expandableListView = findViewById(R.id.expandableListView);
    expandableListDetail = ExpandableListDataPump.getData(getApplicationContext());
    expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
    expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
    expandableListView.setAdapter(expandableListAdapter);



    expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
      @Override
      public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
      }
    });
    expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
      @Override
      public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();

      }
    });
    expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
      @Override
      public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long l) {

        int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition,childPosition));
        parent.setItemChecked(index,true);
        groupName = expandableListTitle.get(groupPosition);
        selectedchildPosition = index;
        SelectedChildNavigationExpandible(expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
        return false;
      }
    });
    expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      }
    });
  }
  void SelectedChildNavigationExpandible(String selectedChild) {
    switch (selectedChild){
      case "Approve Order" :{
        loadFragment(new OderListFragment(),"Approve Order");
        break;
      }case "Activity": {
        loadFragment(new ActivityFragment(),"Activity");
        break;
      }case "Upgrade app":{
        loadFragment(new UpgradeAppFragment(),"Upgrade app");
        break;
      }case "View Order":{
        loadFragment(new ViewOrderFragment(),"View Order");
        break;
      }
    }
  }
  public void loadFragment(androidx.fragment.app.Fragment newFragment, String FragmentName) {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();
    fragmentTransaction.replace(R.id.nav_host_fragment, newFragment).addToBackStack(null);
    fragmentTransaction.commit();
    closeSidePanel();
    this.setTitle(FragmentName);

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.dashboard, menu);
    return true;
  }
  void closeSidePanel() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    }
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.action_LogOut) {
      try {

        JSONObject postedJson = new JSONObject();
        postedJson.put("email_id", tv_header_userEmail.getText());
        postedJson.put("password", "");
        postedJson.put("flag", "Logout");
        new HitToServer().execute(new AsyModel(AllApiLinks.LoginApi,postedJson,"Logout"));

      } catch (Exception e) {}

    }
    return super.onOptionsItemSelected(item);
  }

  public class HitToServer extends AsyncTask<AsyModel,Void, HttpHandlerModel> {
    private GlobalPostingMethod hitObj = new GlobalPostingMethod();
    private String flagOfAction;

    protected void onPreExecute() {
      super.onPreExecute();
      progress = new ProgressDialog(Dashboard.this);
      progress.setMessage("Please Wait..");
      progress.setCanceledOnTouchOutside(false);
      progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      progress.show();

    }

    @Override
    protected HttpHandlerModel doInBackground(AsyModel... asyModels) {

      try {
        URL postingUrl = hitObj.createUrl(asyModels[0].getPostingUrl());
        flagOfAction = asyModels[0].getFlagOfAction();
        if (asyModels[0].getPostingJson() == null) {
          return hitObj.getHttpRequest(postingUrl);
        } else {
          return hitObj.postHttpRequest(postingUrl, asyModels[0].getPostingJson());
        }
      } catch (Exception e) {
        return hitObj.setReturnMessage(false, "Problem retrieving the user JSON results" + e.getMessage());
      }

    }

    protected void onPostExecute(HttpHandlerModel result) {
      super.onPostExecute(result);
      bindResponse(result, flagOfAction);
    }

    private void bindResponse(HttpHandlerModel result, String flagOfAction) {
      try {
        if (result.isConnectStatus()) {
          if (flagOfAction.equalsIgnoreCase("Logout"))
            bindLogOutResponse(result.getJsonResponse(), flagOfAction);
        } else {
          Snackbar.make(toolbar, result.getJsonResponse(), Snackbar.LENGTH_INDEFINITE).setAction("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
          }).show();
        }
      } catch (Exception e) {
      } finally {
      }
    }

    void bindLogOutResponse(String result, String flagOfAction) throws Exception {
      LoginModel templist = new Gson().fromJson(result, new TypeToken<LoginModel>() {
      }.getType());

      if (templist.condition) {
        sessionManagement.ClearSession();
        Intent mainIntent = new Intent(Dashboard.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
      } else {
        Snackbar.make(toolbar, templist.message, Snackbar.LENGTH_INDEFINITE).setAction("Cancel", new View.OnClickListener() {
          @Override
          public void onClick(View view) {

          }
        }).show();
      }
//             else {
//                Snackbar.make(toolbar, "Api Response Not Proper.", Snackbar.LENGTH_INDEFINITE).setAction("Cancel", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                }).show();
//            }
    }
  }
  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
            || super.onSupportNavigateUp();
  }

}
